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

import edu.sjsu.amigo.command.db.dao.ProviderDAO;
import edu.sjsu.amigo.command.db.model.Command;
import edu.sjsu.amigo.command.db.model.Provider;
import edu.sjsu.amigo.db.common.DBException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author rwatsh on 4/16/17.
 */
public class ProviderDAOTest extends DBTest<ProviderDAO, Provider> {
    private static final Logger log = Logger.getLogger(ProviderDAOTest.class.getName());

    @Override
    public void testAdd() throws Exception {
        testCreateProvider();
    }

    private List<String> testCreateProvider() throws DBException {
        Provider provider = createProvider();

        ProviderDAO providerDAO = (ProviderDAO) client.getDAO(ProviderDAO.class);
        List<String> insertedIds = providerDAO.add(new ArrayList<Provider>() {{
            add(provider);
        }});
        List<Provider> providers = providerDAO.fetchById(insertedIds);
        Assert.assertNotNull(providers);
        log.info("Provider created: " + providers);
        return insertedIds;
    }

    private Provider createProvider() {
        Provider provider = new Provider();
        provider.setCloudProvider("aws");
        provider.setDockerImage("sjsucohort6/docker_awscli");
        List<Command> commands = new ArrayList<>();
        Command cmd = new Command();
        cmd.setCommand("aws iam list-users");
        String intent = "aws iam users";
        cmd.setIntents(
                new ArrayList<String>() {{
                    add(intent);
                }}
        );
        commands.add(cmd);
        provider.setCommands(commands);
        return provider;
    }

    @Override
    public void testRemove() throws Exception {
        List<String> insertedIds = testCreateProvider();
        Assert.assertNotNull(insertedIds);
        long countRemovedEntries = dao.remove(insertedIds);
        Assert.assertTrue(countRemovedEntries > 0, "Failed to delete any service");
    }

    @Override
    public void testUpdate() throws Exception {

    }

    @Override
    public void testFetch() throws Exception {
        List<String> insertedIds = testCreateProvider();
        Assert.assertNotNull(insertedIds);
        List<Provider> providers = dao.fetchById(insertedIds);
        Assert.assertNotNull(providers);
    }
}
