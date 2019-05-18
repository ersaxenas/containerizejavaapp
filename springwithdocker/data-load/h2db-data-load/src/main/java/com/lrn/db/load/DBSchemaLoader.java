package com.lrn.db.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Optional;
import java.util.Scanner;

public class DBSchemaLoader {
    private final Logger logger = LoggerFactory.getLogger(DBSchemaLoader.class);
    private String adminUserName = "admin";
    private String adminPassword = "admin";
    private String dataBaseName = "contactsdb";
    private String tableName = "CONTACT_DETAILS";

    public static void main(String[] args) throws SQLException {
        System.out.println(System.getenv("consul_service"));

        DBSchemaLoader dbSchemaLoader = new DBSchemaLoader();
        Optional<Connection> connectionOptional = dbSchemaLoader.createDb();
        if (connectionOptional.isPresent()) {
            try (Connection conn = connectionOptional.get()) {
                dbSchemaLoader.createTables(conn);
                dbSchemaLoader.loadContactDetails(conn);
                conn.commit();
            }
        } else {
            System.exit(-1);
        }
    }

    private void loadContactDetails(Connection connection) {
        if (checkIfDataExists(connection)) {
            logger.info("Data already present in the table : " + tableName);
            return;
        }
        try (InputStream inpStream = getClass().getResourceAsStream("/contacts_details_data.sql")) {
            new Scanner(inpStream).useDelimiter("\n").forEachRemaining(insertDML -> executeSql(insertDML, connection));
        } catch (IOException exp) {
            logger.error("Exception occurred while trying to open data load file : " + exp.getMessage(), exp);
            throw new IllegalStateException("Exception occurred while trying to open data load file : " + exp.getMessage());
        }
        logger.info("Data has been loaded to table : " + tableName + " successfully.");
    }

    private boolean checkIfDataExists(Connection connection) {
        String checkdata = "SELECT COUNT(1) as cnt FROM " + tableName;
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(checkdata);
        ) {
            if (resultSet.next()) {
                int cnt = resultSet.getInt("cnt");
                if (cnt > 0) {
                    return true;
                }
            }
        } catch (SQLException exp) {
            logger.error("Exception occurred while trying to executed SQL : " + exp.getMessage(), exp);
        }
        return false;
    }

    private void createTables(Connection connection) {
        String contactDetailTableDDL = "CREATE TABLE " +
                tableName +
                "(CONTACT_ID INT PRIMARY KEY, " +
                "NAME VARCHAR, " +
                "ADDRESS VARCHAR, " +
                "PHONE VARCHAR, " +
                "EMAIL VARCHAR, " +
                " )";
        if (!executeSql(contactDetailTableDDL, connection)) {
            logger.error("Failed to create DB table. Please check logs for details.");
        }

    }

    private boolean executeSql(String sql, Connection connection) {
        try {
            return connection.createStatement().execute(sql);
        } catch (SQLException exp) {
            logger.error("Exception occurred while trying to executed SQL : " + exp.getMessage(), exp);
            return false;
        }
    }

    private Optional<Connection> createDb() {
        logger.info("Creating database : IN-MEM : " + dataBaseName);
        String dbUrl = "jdbc:h2:tcp://contact-h2db:1521/mem:" + dataBaseName + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        logger.info("Admin user : " + adminUserName);
        logger.info("dburl : " + dbUrl);
        try {
            Connection conn = DriverManager.getConnection(dbUrl, adminUserName, adminPassword);
            logger.debug("Connection successfully created.");
            return Optional.of(conn);
        } catch (SQLException exp) {
            logger.error("Exception occurred while trying to create database : " + exp.getMessage(), exp);
            return Optional.empty();
        }
    }

}
