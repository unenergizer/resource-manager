package com.forgestorm.resourcemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class ResourceManagerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManagerApplication.class);

    @Autowired
    private Environment environment;

    /**
     * Start our SpringBoot server! Make sure the server is supplied the args below!
     * --db.url=jdbc:mysql://localhost:3306/mydb --db.username=root --db.password=secret
     *
     * @param args Database args need to be supplied. See example above.
     */
    public static void main(String[] args) {
        SpringApplication.run(ResourceManagerApplication.class, args);
    }

    /**
     * Configure the SpringBoot server to take database details by command
     * line arguments. This repo is public, so we don't want to share those
     * details in an "application.properties" file.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        LOGGER.debug("--- DataSource Environment Data ---");
        LOGGER.debug("db.url: " + environment.getProperty("db.url"));
        LOGGER.debug("db.username: " + environment.getProperty("db.username"));
        LOGGER.debug("db.password: " + environment.getProperty("db.password"));

        dataSource.setUrl(environment.getProperty("db.url"));
        dataSource.setUsername(environment.getProperty("db.username"));
        dataSource.setPassword(environment.getProperty("db.password"));

        return dataSource;
    }
}
