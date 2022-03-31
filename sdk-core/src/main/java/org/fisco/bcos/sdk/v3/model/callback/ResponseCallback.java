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

package org.fisco.bcos.sdk.v3.model.callback;

import org.fisco.bcos.sdk.v3.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ResponseCallback is to define a callback to handle response from node. */
public abstract class ResponseCallback {

    private static Logger logger = LoggerFactory.getLogger(ResponseCallback.class);

    private long timeoutValue = -1;

    /**
     * OnResponse
     *
     * @param response the response from node
     */
    public abstract void onResponse(Response response);

    public void onError(String errorMessage) {
        Response response = new Response();
        response.setErrorCode(-5000);
        response.setErrorMessage(errorMessage);
        onResponse(response);
    }

    public void setTimeoutValue(long timeoutValue) {
        this.timeoutValue = timeoutValue;
    }

    public long getTimeoutValue() {
        return this.timeoutValue;
    }
}
