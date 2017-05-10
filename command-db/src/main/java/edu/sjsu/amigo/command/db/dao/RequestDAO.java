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
import edu.sjsu.amigo.command.db.model.Request;
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
public class RequestDAO extends BaseDAOImpl<Request> {
    public RequestDAO(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
    }

    @Override
    public void update(List<Request> entityList) throws DBException {
        for (Request request: entityList) {
            List<Request> requests = fetchById(new ArrayList<String>() {{add(request.getRequestId());}});
            Request existingRequest = null;
            if (requests != null && !requests.isEmpty()) {
                existingRequest = requests.get(0);
            }
            if (existingRequest != null) {
                UpdateOperations<Request> ops = this.createUpdateOperations();
                if (request.getCommandExecuted() != null) {
                    ops.set("commandExecuted", request.getCommandExecuted());
                }
                if (request.getResp() != null) {
                    ops.set("resp", request.getResp());
                }
                if (request.getRespRecvdTime() != null) {
                    ops.set("respRecvdTime", request.getRespRecvdTime());
                }
                if (request.getStartTime() != null) {
                    ops.set("startTime", request.getStartTime());
                }
                if (request.getStatus() != null) {
                    ops.set("status", request.getStatus());
                }

                Query<Request> updateQuery = this.createQuery().field(Mapper.ID_KEY).equal(request.getRequestId());
                UpdateResults results = this.update(updateQuery, ops);
            }
        }
    }
}
