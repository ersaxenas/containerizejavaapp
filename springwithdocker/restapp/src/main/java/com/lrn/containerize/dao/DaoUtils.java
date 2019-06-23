package com.lrn.containerize.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class DaoUtils {
    private final Logger logger = LoggerFactory.getLogger(DaoUtils.class);
    private final String dbServer = "contact-h2db:1521";
    private final int dbPort = 1521;
    private final String dataBaseName = "contactsdb";
    private final String dbUserName = "admin";
    private final String dbPassword = "admin";

    public Optional<Connection> getDbConnection() {
      return createDb();
    }

    private Optional<Connection> createDb() {
        logger.info("Creating connection to database : IN-MEM : " + dataBaseName);
        String dbUrl = "jdbc:h2:tcp://"+dbServer+":"+dbPort+"/mem:" + dataBaseName + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        logger.info("Admin user : " + dbUserName);
        logger.info("dburl : " + dbUrl);
        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            logger.debug("Connection successfully created.");
            return Optional.of(conn);
        } catch (SQLException exp) {
            logger.error("Exception occurred while trying to create database : " + exp.getMessage(), exp);
            return Optional.empty();
        }
    }
}
