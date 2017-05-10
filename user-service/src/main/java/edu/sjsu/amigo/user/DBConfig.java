/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package edu.sjsu.amigo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBFactory;
import edu.sjsu.amigo.user.db.DatabaseModule;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author rwatsh on 3/26/17.
 */
@Log
public class DBConfig {
    @Inject
    DBFactory dbFactory;

    @NotEmpty
    @JsonProperty
    private String server = "user-db";

    @Min(1)
    @Max(65535)
    @JsonProperty
    private int port = 27017;

    @NotEmpty
    @JsonProperty
    private String dbName = "user_db";
    private DBClient dbClient;

    @JsonIgnore
    public DBClient getDbClient() {
        return dbClient;
    }

    @JsonProperty
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DBClient build(Environment environment) {
        String dbServer = System.getenv("DB");
        if (dbServer != null) {
            server = dbServer;
        }
        dbClient = dbFactory.create(server, port, dbName);
        log.info("Connected to db");
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                dbClient.useDB(dbName);
            }

            @Override
            public void stop() throws Exception {
                dbClient.close();
            }
        });
        return dbClient;
    }

    public DBConfig() {
        try {
            Thread.sleep(20000);
        } catch(Exception e) {

        }
        Module module = new DatabaseModule();
        Guice.createInjector(module).injectMembers(this);

    }
}
