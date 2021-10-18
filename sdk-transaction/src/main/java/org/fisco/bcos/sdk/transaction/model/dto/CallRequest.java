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
package org.fisco.bcos.sdk.transaction.model.dto;

import org.fisco.bcos.sdk.codec.wrapper.ABIDefinition;

/**
 * CallRequest @Description: CallRequest
 *
 * @author maojiayu
 */
public class CallRequest {
    private String from;
    private String to;
    private byte[] encodedFunction;
    private ABIDefinition abi;

    public CallRequest(String from, String to, byte[] encodedFunction) {
        this.from = from;
        this.to = to;
        this.encodedFunction = encodedFunction;
    }

    /**
     * @param from the transaction sender
     * @param to the contract address
     * @param encodedFunction the encoded methods and params
     * @param abi the abi definition of the function
     */
    public CallRequest(String from, String to, byte[] encodedFunction, ABIDefinition abi) {
        this(from, to, encodedFunction);
        this.abi = abi;
    }

    /** @return the from */
    public String getFrom() {
        return this.from;
    }

    /** @param from the from to set */
    public void setFrom(String from) {
        this.from = from;
    }

    /** @return the to */
    public String getTo() {
        return this.to;
    }

    /** @param to the to to set */
    public void setTo(String to) {
        this.to = to;
    }

    /** @return the encodedFunction */
    public byte[] getEncodedFunction() {
        return this.encodedFunction;
    }

    /** @param encodedFunction the encodedFunction to set */
    public void setEncodedFunction(byte[] encodedFunction) {
        this.encodedFunction = encodedFunction;
    }

    /** @return the abi */
    public ABIDefinition getAbi() {
        return this.abi;
    }

    /** @param abi the abi to set */
    public void setAbi(ABIDefinition abi) {
        this.abi = abi;
    }
}
