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
package org.fisco.bcos.sdk.transaction.decoder;

import java.util.List;
import java.util.Map;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionManager;
import org.fisco.bcos.sdk.transaction.manager.TransactionManagerFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * EventDecodeTest
 *
 * @Description: EventDecodeTest
 * @author maojiayu
 * @data Aug 28, 2020 10:50:53 PM
 *
 */
public class EventDecodeTest {

    private static final String configFile = "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private final String abi =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"bytesArray\",\"type\":\"bytes1[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"a\",\"type\":\"address[]\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes1\"}],\"name\":\"getByBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes1[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"s\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"}]";

    @Test
    public void testDecode() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        ABICodec abiCodec = new ABICodec(client.getCryptoInterface());
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if(!response.getTransactionReceipt().getStatus().equals("0x0")) {
            System.out.println(response.getReturnMessage());
            return;
        }
        List<Object> list =
                abiCodec.decodeEvent(abi, "LogInit", response.getTransactionReceipt().getLogs().get(0).getData());
        Assert.assertEquals("test2", list.get(1));
        Map<String, List<Object>> map = response.getEventResultMap();
        Assert.assertEquals("test2", map.get("LogInit").get(1));
    }
}
