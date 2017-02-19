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
package edu.sjsu.amigo.cp.tasks.azure;

import edu.sjsu.amigo.cp.tasks.api.*;

/**
 * Azure command executor.
 *
 * @author rwatsh on 2/16/17.
 */
public class AzureCommandExecutor implements CommandExecutor {
    @Override
    public Response executeCommand(Command cmd) throws CommandExecutionException {
        throw new UnsupportedOperationException("Azure is not yet supported!");
    }
}
