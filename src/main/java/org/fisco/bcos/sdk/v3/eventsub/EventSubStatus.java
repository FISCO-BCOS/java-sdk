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

package org.fisco.bcos.sdk.v3.eventsub;

public enum EventSubStatus {
    /** When make a subscribe of an event, the node would response the status. */
    SUCCESS(0),
    PUSH_COMPLETED(1),
    INVALID_PARAMS(-41000),
    INVALID_REQUEST(-41001),
    GROUP_NOT_EXIST(-41002),
    INVALID_RANGE(-41003),
    INVALID_RESPONSE(-41004),
    REQUEST_TIMEOUT(-41005),
    SDK_PERMISSION_DENIED(-41006),
    // reserve 100 errors
    OTHER_ERROR(42000),
    ;

    private int status;

    private EventSubStatus(int status) {
        this.setStatus(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static EventSubStatus fromIntStatus(int status) {
        for (EventSubStatus e : EventSubStatus.values()) {
            if (e.getStatus() == status) {
                return e;
            }
        }
        return EventSubStatus.OTHER_ERROR;
    }

    public static String getDescMessage(int status) {
        return getDescMessage(fromIntStatus(status));
    }

    public static String getDescMessage(EventSubStatus status) {

        String desc;

        switch (status) {
            case SUCCESS:
                desc = "success";
                break;
            case PUSH_COMPLETED:
                desc = "push completed";
                break;
            case INVALID_PARAMS:
                desc = "params invalid";
                break;
            case INVALID_REQUEST:
                desc = "register request not valid format";
                break;
            case REQUEST_TIMEOUT:
                desc = "register request timeout";
                break;
            case GROUP_NOT_EXIST:
                desc = "group not exist";
                break;
            case INVALID_RANGE:
                desc = "register parameters not in a range within permision";
                break;
            case INVALID_RESPONSE:
                desc = "response message not invalid format";
                break;
            case SDK_PERMISSION_DENIED:
                desc = "the SDK is not allowed to access this group.";
                break;
            default:
                {
                    desc = "other errors";
                    break;
                }
        }

        return desc;
    }
}
