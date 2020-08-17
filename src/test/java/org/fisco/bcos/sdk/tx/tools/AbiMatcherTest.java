package org.fisco.bcos.sdk.tx.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.tools.AbiMatchHandler;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.junit.Assert;
import org.junit.Test;

public class AbiMatcherTest {

    @SuppressWarnings({"static-access"})
    @Test
    public void testBasic() throws Exception {
        String abiStr =
                "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        List<ABIDefinition> ABIDefinitionList = ContractAbiUtil.getFuncABIDefinition(abiStr);
        AbiMatchHandler abiMatchHandler = new AbiMatchHandler();
        List<Object> list = new ArrayList<>();
        list.add("hello");
        ArrayList<ABIDefinition> result =
                (ArrayList<ABIDefinition>)
                        abiMatchHandler
                                .matchPossibleDefinitions(ABIDefinitionList, "set", list)
                                .collect(Collectors.toList());
        Assert.assertTrue(1 == result.size());
    }
}
