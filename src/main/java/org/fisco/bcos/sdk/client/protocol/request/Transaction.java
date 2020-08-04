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

package org.fisco.bcos.sdk.client.protocol.request;

public class Transaction {
    private String from;
    private String to;
    private String encodedFunction;

    /**
     * @param from
     * @param to
     * @param encodedFunction
     */
    public Transaction(String from, String to, String encodedFunction) {
        super();
        this.from = from;
        this.to = to;
        this.encodedFunction = encodedFunction;
    }

    /** @return the from */
    public String getFrom() {
        return from;
    }
    /** @param from the from to set */
    public void setFrom(String from) {
        this.from = from;
    }
    /** @return the to */
    public String getTo() {
        return to;
    }
    /** @param to the to to set */
    public void setTo(String to) {
        this.to = to;
    }
    /** @return the encodedFunction */
    public String getEncodedFunction() {
        return encodedFunction;
    }
    /** @param encodedFunction the encodedFunction to set */
    public void setEncodedFunction(String encodedFunction) {
        this.encodedFunction = encodedFunction;
    }
}
