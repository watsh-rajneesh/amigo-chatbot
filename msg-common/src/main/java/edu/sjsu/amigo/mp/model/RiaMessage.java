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

package edu.sjsu.amigo.mp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Message received from RIA (Raspberry Pi Intelligent Assistant).
 *
 * @see <a href="https://github.com/sjsucohort6/ria">RIA</a>
 *
 * @author rwatsh on 4/23/17.
 */
@Data
public class RiaMessage extends Message {
    /*
     * ID of RIA device that can be used to identify the registered user in the system.
     */
    @JsonProperty
    private String riaId;

    public RiaMessage() {}

    public RiaMessage(String time, String userEmail, String userName, String content, String riaId) {
        super(BotType.RIA.name(), time, userEmail, userName, content);
        this.riaId = riaId;
    }
}
