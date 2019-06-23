package com.lrn.containerize.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DBConfiguration.class);
    private final String dbServer = "contact-h2db";
    private final int dbPort = 1521;
    private final String dataBaseName = "contactsdb";
    private final String dbUserName = "admin";
    private final String dbPassword = "admin";


    @Bean(name = "contactsdb")
    public DataSource getDataSource() {
        logger.info("Creating connection to database : IN-MEM : " + dataBaseName);
        String dbUrl = "jdbc:h2:tcp://"+dbServer+":"+dbPort+"/mem:" + dataBaseName + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        logger.info("Admin user : " + dbUserName);
        logger.info("dburl : " + dbUrl);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }
}
