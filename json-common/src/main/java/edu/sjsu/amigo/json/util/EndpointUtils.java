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

package edu.sjsu.amigo.json.util;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author rwatsh on 3/27/17.
 */
public class EndpointUtils {
    public static final String ENDPOINT_VERSION_STRING= "v1.0";
    public static final String ENDPOINT_ROOT = "/api/" + ENDPOINT_VERSION_STRING;

    public static URI getCreatedResourceURI (UriInfo info,
                                             URI resourcePath, Long resourceId) {
        URI uri = info.getAbsolutePathBuilder()
                .uri(resourcePath).path(info.getPath())
                .path(resourceId.toString())
                .build();

        return uri;
    }
}
