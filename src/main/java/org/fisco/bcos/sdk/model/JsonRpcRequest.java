/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class JsonRpcRequest<T> {
    // for set the json id
    private static AtomicLong nextIdGetter = new AtomicLong(0);
    // the jsonrpc version, default is 2.0
    private String jsonRpcVersion = "2.0";
    // rpc method
    private String method;
    // params for the rpc interface
    private List<T> params;
    // the json rpc request id
    private long id;

    public JsonRpcRequest(String method, List<T> params) {
        this.method = method;
        this.params = params;
        this.id = nextIdGetter.getAndIncrement();
    }

    // getter and setter for the class members
    public String getJsonRpcVersion() {
        return this.jsonRpcVersion;
    }

    public String getMethod() {
        return this.method;
    }

    public long getId() {
        return this.id;
    }

    public List<T> getParams() {
        return this.params;
    }
}
