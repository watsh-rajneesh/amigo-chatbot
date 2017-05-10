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

import edu.sjsu.amigo.command.db.dao.RequestDAO;
import edu.sjsu.amigo.command.db.model.Request;
import edu.sjsu.amigo.command.db.model.Status;
import edu.sjsu.amigo.db.common.DBException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author rwatsh on 4/16/17.
 */
public class RequestDAOTest extends DBTest<RequestDAO, Request> {
    private static final Logger log = Logger.getLogger(RequestDAOTest.class.getName());

    @Override
    public void testAdd() throws Exception {
        testCreateRequest();
    }

    private List<String> testCreateRequest() throws DBException {
        Request request = createRequest();

        RequestDAO requestDAO = (RequestDAO) client.getDAO(RequestDAO.class);
        List<String> insertedIds = requestDAO.add(new ArrayList<Request>() {{
            add(request);
        }});
        List<Request> providers = requestDAO.fetchById(insertedIds);
        Assert.assertNotNull(providers);
        log.info("Provider created: " + providers);
        return insertedIds;
    }

    private Request createRequest() {
        Request r = new Request();
        r.setCommandExecuted("aws iam list-users");
        r.setRequestId("84a5-bc63ad413f0a");
        r.setResp("{\n" +
                "    \"Users\": [\n" +
                "        {\n" +
                "            \"UserName\": \"admin\",\n" +
                "            \"PasswordLastUsed\": \"2016-02-18T04:21:55Z\",\n" +
                "            \"CreateDate\": \"2016-02-13T00:20:57Z\",\n" +
                "            \"UserId\": \"AIDIDID\",\n" +
                "            \"Path\": \"/\",\n" +
                "            \"Arn\": \"arn:aws:iam::064674:user/admin\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"UserName\": \"rwatsh\",\n" +
                "            \"Path\": \"/\",\n" +
                "            \"CreateDate\": \"2016-09-17T16:08:47Z\",\n" +
                "            \"UserId\": \"DGDGFG\",\n" +
                "            \"Arn\": \"arn:aws:iam::064674:user/rwatsh\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");
        r.setRespRecvdTime(new Date());
        r.setStartTime(new Date());
        r.setStatus(Status.SUCCESS);
        return r;
    }

    @Override
    public void testRemove() throws Exception {
        List<String> insertedIds = testCreateRequest();
        Assert.assertNotNull(insertedIds);
        long countRemovedEntries = dao.remove(insertedIds);
        Assert.assertTrue(countRemovedEntries > 0, "Failed to delete any service");
    }

    @Override
    public void testUpdate() throws Exception {

    }

    @Override
    public void testFetch() throws Exception {
        List<String> insertedIds = testCreateRequest();
        Assert.assertNotNull(insertedIds);
        List<Request> providers = dao.fetchById(insertedIds);
        Assert.assertNotNull(providers);
    }
}
