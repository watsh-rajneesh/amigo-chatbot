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

package edu.sjsu.amigo.command.db.dao;

import com.mongodb.MongoClient;
import edu.sjsu.amigo.command.db.model.Provider;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.db.common.mongodb.BaseDAOImpl;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwatsh on 4/16/17.
 */
public class ProviderDAO extends BaseDAOImpl<Provider>{

    public ProviderDAO(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
    }

    @Override
    public void update(List<Provider> entityList) throws DBException {
        for (Provider provider: entityList) {
            List<Provider> providers = fetchById(new ArrayList<String>() {{add(provider.getCloudProvider());}});
            Provider existingProvider = null;
            if (providers != null && !providers.isEmpty()) {
                existingProvider = providers.get(0);
            }
            if (existingProvider != null) {
                UpdateOperations<Provider> ops = this.createUpdateOperations();
                if (provider.getCommands() != null) {
                    ops.set("commands", provider.getCommands());
                }


                Query<Provider> updateQuery = this.createQuery().field(Mapper.ID_KEY).equal(provider.getCloudProvider());
                UpdateResults results = this.update(updateQuery, ops);
            }
        }
    }
}
