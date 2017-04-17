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

package edu.sjsu.amigo.command.db.mongodb;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import edu.sjsu.amigo.command.db.dao.ProviderDAO;
import edu.sjsu.amigo.command.db.dao.RequestDAO;
import edu.sjsu.amigo.command.db.model.Provider;
import edu.sjsu.amigo.db.common.BaseDAO;
import edu.sjsu.amigo.db.common.mongodb.MongoDBClientBase;

/**
 * @author rwatsh on 4/16/17.
 */
public class MongoDBClient extends MongoDBClientBase {
    private ProviderDAO providerDAO;
    private RequestDAO requestDAO;
    /**
     * Constructs a MongoDB client instance.
     * <p>
     * This is private so it can only be instantiated via DI (using Guice).
     *
     * @param server server hostname or ip
     * @param port   port number for mongodb service
     * @param dbName name of db to use
     */
    @Inject
    private MongoDBClient(@Assisted("server") String server, @Assisted("port") int port, @Assisted("dbName") String dbName) {
        super(server, port, dbName);
        morphia.mapPackageFromClass(Provider.class);
        providerDAO = new ProviderDAO(mongoClient, morphia, dbName);
        requestDAO = new RequestDAO(mongoClient, morphia, dbName);
    }

    @Override
    public Object getDAO(Class<? extends BaseDAO> clazz) {
        if (clazz != null) {
            if (clazz.getTypeName().equalsIgnoreCase(ProviderDAO.class.getTypeName())) {
                return providerDAO;
            }
            if (clazz.getTypeName().equalsIgnoreCase(RequestDAO.class.getTypeName())) {
                return requestDAO;
            }
        }
        return null;
    }
}
