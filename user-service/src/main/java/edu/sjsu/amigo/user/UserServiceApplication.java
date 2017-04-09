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

import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.user.auth.PrincipalUser;
import edu.sjsu.amigo.user.auth.SimpleAuthenticator;
import edu.sjsu.amigo.user.health.DBHealthCheck;
import edu.sjsu.amigo.user.rest.EndpointUtils;
import edu.sjsu.amigo.user.rest.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;

/**
 * @author rwatsh on 3/26/17.
 */
@Log
public class UserServiceApplication extends Application<UserServiceConfiguration> {

    private DBClient dbClient;

    public static void main(final String[] args) throws Exception {
        new UserServiceApplication().run(args);

    }

    @Override
    public String getName() {
        return "User Service";
    }

    @Override
    public void initialize(final Bootstrap<UserServiceConfiguration> bootstrap) {
        /*
         * Register the static html contents to be served from /assets directory and accessible from browser from
         * http://<host>:<port>/openstack
         */
        //bootstrap.addBundle(new AssetsBundle("/assets", "/openstack", "index.html"));
        /*bootstrap.addBundle(new SwaggerBundle<UserServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(UserServiceConfiguration userServiceConfiguration) {
                return userServiceConfiguration.swaggerBundleConfiguration;
            }
        });*/
    }



    @Override
    public void run(UserServiceConfiguration userServiceConfiguration, Environment environment) throws Exception {
        dbClient = userServiceConfiguration.getDbConfig().build(environment);
        log.info("Connected to db: " + dbClient.getConnectString());
        /*
         * Setup basic authentication against DB table.
         */
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<PrincipalUser>()
                        .setAuthenticator(new SimpleAuthenticator(dbClient))
                        .setRealm("amigo_user")
                        .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(PrincipalUser.class));

        environment.healthChecks().register("database", new DBHealthCheck(dbClient));
        /*
         * Register resources with jersey.
         */
        final UserResource userResource = new UserResource(dbClient);

        /*
         * Setup jersey environment.
         */
        environment.jersey().setUrlPattern(EndpointUtils.ENDPOINT_ROOT + "/*");
        environment.jersey().register(userResource);
        log.info("Done with all initializations for user service");
    }
}
