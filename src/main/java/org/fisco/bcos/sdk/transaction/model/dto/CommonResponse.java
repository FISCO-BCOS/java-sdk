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

/**
 * CommonResponse @Description: CommonResponse
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:15:35 PM
 */
public class CommonResponse {
    private int returnCode;
    private String returnMessage;

    /** @return the returnCode */
    public int getReturnCode() {
        return returnCode;
    }

    /** @param returnCode the returnCode to set */
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    /** @return the returnMessage */
    public String getReturnMessage() {
        return returnMessage;
    }

    /** @param returnMessage the returnMessage to set */
    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
