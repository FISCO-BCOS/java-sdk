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

package org.fisco.bcos.sdk.client;

import org.fisco.bcos.sdk.model.Response;

/**
 * Callback function to executed when client get response from the node.
 *
 * @author Maggie
 * @param <T> for the response data structures in package client/response
 */
public interface RespCallback<T> {
    /**
     * onResponse is the call back function
     *
     * @param t the response data structure
     */
    void onResponse(T t);

    void onError(Response errorResponse);
}
