/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.server;

import com.vaadin.flow.server.frontend.FallibleCommand;

/**
 * Thrown by {@link FallibleCommand} if it's unable to complete its execution.
 *
 * @author Vaadin Ltd
 * @since 2.0
 *
 */
public class ExecutionFailedException extends Exception {

    /**
     * Creates a new exception instance.
     */
    public ExecutionFailedException() {
    }

    /**
     * Creates a new exception instance with the given {@code message}.
     *
     * @param message
     *            the exception message
     */
    public ExecutionFailedException(String message) {
        super(message);
    }

    /**
     * Creates a new exception instance with the given {@code cause}.
     *
     * @param cause
     *            the exception cause
     */
    public ExecutionFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception instance with the given {@code message} and
     * {@code cause}.
     *
     * @param message
     *            the exception message
     * @param cause
     *            the exception cause
     */
    public ExecutionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
