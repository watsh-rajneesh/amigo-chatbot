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

package edu.sjsu.amigo.db.common.mongodb;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import edu.sjsu.amigo.db.common.BaseDAO;
import edu.sjsu.amigo.db.common.DBException;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic DAO implementation for all mongoDB entities.
 *
 * Implements all methods except update() that needs to be implemented for each entity in its DAO class.
 *
 * TODO make update() method generic too.
 *
 * @author rwatsh on 3/27/17.
 */
public abstract class BaseDAOImpl<T> extends BasicDAO<T, String> implements BaseDAO<T> {
    protected Morphia morphia;
    protected BaseDAOImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
        this.morphia = morphia;
    }

    @Override
    public synchronized List<String> add(List<T> entityList) throws DBException {
        try {
            List<String> insertedIds = new ArrayList<>();
            if (entityList != null) {
                for (T entity : entityList) {
                    Key<T> key = this.save(entity);
                    insertedIds.add(key.getId().toString());
                }
            }
            return insertedIds;
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    public synchronized void addOrUpdate(T entity, String id) throws DBException {
        List<String> ids = new ArrayList<>();

        List<T> entities = fetchById(new ArrayList<String>() {{
            add(id);
        }});
        if (entities == null || entities.isEmpty()) {
            // new entity so add
            add(new ArrayList<T>(){{add(entity);}});
        } else {
            // existing task, update
            update(new ArrayList<T>(){{add(entity);}});
        }
    }

    @Override
    public synchronized long remove(List<String> entityIdsList) throws DBException {
        Query<T> query = this.createQuery().field(Mapper.ID_KEY).in(entityIdsList);
        return this.deleteByQuery(query).getN();
    }

    @Override
    public synchronized List<T> fetchById(List<String> entityIdsList) throws DBException {
        List<String> objectIds = new ArrayList<>();
        if (entityIdsList != null) {
            for (String id : entityIdsList) {
                objectIds.add(id);
            }
        }

        if (!objectIds.isEmpty()) {
            Query<T> query = this.createQuery().field(Mapper.ID_KEY).in(objectIds);
            QueryResults<T> results = this.find(query);
            return results.asList();
        } else {
            Query<T> query = this.createQuery();
            QueryResults<T> results = this.find(query);
            return results.asList();
        }
    }

    @Override
    public synchronized List<T> fetch(String query, Class<T> clazz) throws DBException {
        List<T> services = new ArrayList<>();
        DBObject dbObjQuery;
        DBCursor cursor;
        if (!(query == null)) {
            dbObjQuery = (DBObject) JSON.parse(query);
            cursor = this.getCollection().find(dbObjQuery);
        } else {
            cursor = this.getCollection().find();
        }

        List<DBObject> dbObjects = cursor.toArray();
        for (DBObject dbObject: dbObjects) {
            T service = morphia.fromDBObject(clazz, dbObject);
            services.add(service);
        }
        return services;
    }
}
