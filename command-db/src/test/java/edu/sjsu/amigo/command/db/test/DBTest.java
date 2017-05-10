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

package edu.sjsu.amigo.command.db.test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import edu.sjsu.amigo.command.db.DatabaseModule;
import edu.sjsu.amigo.db.common.BaseDAO;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBFactory;
import org.testng.annotations.*;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.logging.Logger;

public abstract class DBTest<T extends BaseDAO, S> {
    @Inject
    private DBFactory dbFactory;
    protected T dao;
    private Class<T> tClass;

    public String dbName;
    private static final Logger log = Logger.getLogger(DBTest.class.getName());
    protected static DBClient client;
    private long startTime;

    public DBTest() {
        Module module = new DatabaseModule();
        Guice.createInjector(module).injectMembers(this);

    }

    @BeforeClass
    @Parameters({"server", "port", "dbName"})
    public void setUp(@Optional("localhost") String server,
                      @Optional("27017") String port,
                      @Optional("cmd_db") String dbName) throws Exception {
        client = dbFactory.create(server, Integer.parseInt(port), dbName);

        this.dbName = dbName;
        /*
         * Use reflection to infer the class for T type.
         */
        this.tClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        dao = (T) client.getDAO(tClass);
        client.dropDB(this.dbName);
    }

    @AfterClass
    public void tearDown() throws Exception {
        client.close();
    }

    @BeforeMethod
    public void createDB() {
        client.useDB(dbName);
        log.info("********************");
        startTime = System.currentTimeMillis();
    }

    @AfterMethod
    public void dropDB() {
        //client.dropDB(dbName);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        log.info(MessageFormat.format("********* Time taken: {0} ms", diff));
    }

    /*
     * Abstract test methods to be implemented by concrete test classes.
     */
    @Test
    abstract public void testAdd() throws Exception;

    @Test
    abstract public void testRemove() throws Exception;

    @Test
    abstract public void testUpdate() throws Exception;

    @Test
    abstract public void testFetch() throws Exception;

    /**
     * Common test methods shared across test sub classes.
     *
     * Add below...
     *
     */
}
