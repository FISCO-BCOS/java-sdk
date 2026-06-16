package org.fisco.bcos.sdk.v3.test.contract.auth;

import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.junit.Assert;
import org.junit.Test;

public class ProposalInfoTest {

    @Test
    public void testDefaultConstructor() {
        ProposalInfo proposalInfo = new ProposalInfo();

        Assert.assertEquals(7, proposalInfo.getValue().size());
        Assert.assertEquals(
                "(address,address,uint8,uint256,uint8,address[],address[])",
                proposalInfo.getTypeAsString());
        Assert.assertEquals(Address.DEFAULT, proposalInfo.getValue().get(0));
        Assert.assertEquals(Address.DEFAULT, proposalInfo.getValue().get(1));
    }
}
