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
package edu.sjsu.amigo.cp.api;

/**
 * Exception thrown when unable to execute a command.
 *
 * @author rwatsh on 2/14/17.
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(String msg) {
        super(msg);
    }

    public CommandExecutionException(Throwable t) {
        super(t);
    }
}
