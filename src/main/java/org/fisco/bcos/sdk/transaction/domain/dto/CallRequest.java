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
package org.fisco.bcos.sdk.transaction.domain.dto;

/**
 * CallRequest @Description: CallRequest
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:09:48 PM
 */
public class CallRequest extends CommonRequest {
    private String from;
    private String to;
    private String encodedFunction;
    private String abi;

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
    /** @return the abi */
    public String getAbi() {
        return abi;
    }
    /** @param abi the abi to set */
    public void setAbi(String abi) {
        this.abi = abi;
    }
}
