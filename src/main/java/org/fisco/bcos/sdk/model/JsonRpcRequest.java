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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class JsonRpcRequest<T> {
    // for set the json id
    private static AtomicLong nextIdGetter = new AtomicLong(0);
    // the jsonrpc version, default is 2.0
    private String jsonrpc = "2.0";
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

    public static AtomicLong getNextIdGetter() {
        return nextIdGetter;
    }

    public static void setNextIdGetter(AtomicLong nextIdGetter) {
        JsonRpcRequest.nextIdGetter = nextIdGetter;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<T> getParams() {
        return params;
    }

    public void setParams(List<T> params) {
        this.params = params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonRpcRequest<?> that = (JsonRpcRequest<?>) o;
        return id == that.id
                && Objects.equals(jsonrpc, that.jsonrpc)
                && Objects.equals(method, that.method)
                && Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonrpc, method, params, id);
    }

    @Override
    public String toString() {
        return "JsonRpcRequest{"
                + "jsonrpc='"
                + jsonrpc
                + '\''
                + ", method='"
                + method
                + '\''
                + ", params="
                + params
                + ", id="
                + id
                + '}';
    }
}
