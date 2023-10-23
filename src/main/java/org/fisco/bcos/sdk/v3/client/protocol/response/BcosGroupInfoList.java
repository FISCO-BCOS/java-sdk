package org.fisco.bcos.sdk.v3.client.protocol.response;

import java.util.List;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;

public class BcosGroupInfoList extends JsonRpcResponse<List<BcosGroupInfo.GroupInfo>> {

    @Override
    public List<BcosGroupInfo.GroupInfo> getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(List<BcosGroupInfo.GroupInfo> result) {
        super.setResult(result);
    }
}
