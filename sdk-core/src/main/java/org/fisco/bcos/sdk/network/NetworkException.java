/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.network;

import org.fisco.bcos.sdk.utils.SystemInformation;

/**
 * Network exception
 *
 * @author Maggie
 */
public class NetworkException extends Exception {
    public static final int SSL_HANDSHAKE_FAILED = 1;
    public static final int CONNECT_FAILED = 2;
    public static final int INIT_CONTEXT_FAILED = 3;
    private int errorCode = 0;

    public NetworkException(String message, int errorCode) {
        super(message + "\n" + SystemInformation.getSystemInformation());
        this.errorCode = errorCode;
    }

    public NetworkException(String message) {
        super(message + "\n" + SystemInformation.getSystemInformation());
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause) {
        super(message + "\n" + SystemInformation.getSystemInformation(), cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
