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

package edu.sjsu.amigo.mp.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The intent object returned from the wit.ai service.
 *
 * {
         "msg_id" : "5e89f3d9-93d1-4dfa-b128-73fda21974b3",
         "_text" : "list my ec2 instances on aws and azure",
         "entities" : {
             "intent" : [ {
                 "confidence" : 0.9771069792658154,
                 "type" : "value",
                 "value" : "list"
                 }, {
                 "confidence" : 0.9721842327959846,
                 "type" : "value",
                 "value" : "ec2"
                 }, {
                 "confidence" : 0.873736763412216,
                 "type" : "value",
                 "value" : "aws"
                 }, {
                 "confidence" : 0.7501236802989762,
                 "type" : "value",
                 "value" : "azure",
                 "suggested" : true
             } ]
         }
 }
 This class represents each element of intent JSON array.
 *
 * @author rwatsh on 3/8/17.
 */
@Data
public class IntentElem {
    @JsonProperty
    private String confidence;
    @JsonProperty
    private String type;
    @JsonProperty
    private String value;
    @JsonProperty
    private boolean suggested;
}
