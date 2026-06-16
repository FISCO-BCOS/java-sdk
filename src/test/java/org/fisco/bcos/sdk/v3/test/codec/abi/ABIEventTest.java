package org.fisco.bcos.sdk.v3.test.codec.abi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.v3.codec.abi.tools.TopicTools;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.test.codec.TestUtils;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

public class ABIEventTest {
    private static final String abi =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"bytesArray\",\"type\":\"bytes1[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"a\",\"type\":\"address[]\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes1\"}],\"name\":\"getByBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes1[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"s\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"}]";
    private static final String encoded =
            "0x0000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000000000000000000000000000000000000147365742076616c75657320e5ad97e7aca6e4b8b2000000000000000000000000";
    private static final String indexedAbi =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"id\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]";

    @Test
    public void testDecode() {
        ContractCodec abiCodec = new ContractCodec(TestUtils.getCryptoSuite(), false);
        try {
            EventLog log = new EventLog(encoded, new ArrayList<>());
            List<Object> list = abiCodec.decodeEvent(abi, "LogSetValues", log);
            Assert.assertEquals(list.size(), 3);
            Assert.assertEquals(list.get(0).toString(), "20");
            Assert.assertEquals(
                    list.get(1).toString(),
                    "[0x0000000000000000000000000000000000000001, 0x0000000000000000000000000000000000000002, 0x0000000000000000000000000000000000000003]");
            System.out.println("decode event log content, " + list);
        } catch (ContractCodecException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeIndexedEventByInterface() {
        ContractCodec abiCodec = new ContractCodec(TestUtils.getCryptoSuite(), false);
        String eventSignature = "Transfer(address,uint256,uint256)";
        String from = "0x0000000000000000000000000000000000000123";
        BigInteger id = BigInteger.valueOf(42);
        BigInteger value = BigInteger.valueOf(99);

        List<String> topics = new ArrayList<>();
        topics.add(Numeric.toHexString(abiCodec.getFunctionEncoder().buildMethodId(eventSignature)));
        TopicTools topicTools = new TopicTools(TestUtils.getCryptoSuite());
        topics.add(topicTools.addressToTopic(from));
        topics.add(topicTools.integerToTopic(id));

        EventLog log = new EventLog(Numeric.toHexString(TypeEncoder.encode(new Uint256(value))), topics);
        ABIDefinition abiDefinition = TestUtils.getContractABIDefinition(indexedAbi).getEvents().get("Transfer").get(0);

        try {
            List<String> decodedTopics = abiCodec.decodeIndexedEvent(log, abiDefinition);
            Assert.assertEquals(3, decodedTopics.size());
            Assert.assertEquals(topics.get(0), decodedTopics.get(0));
            Assert.assertEquals(from, decodedTopics.get(1));
            Assert.assertEquals(id.toString(), decodedTopics.get(2));

            List<Object> decodedByInterface =
                    abiCodec.decodeEventByInterface(indexedAbi, eventSignature, log);
            Assert.assertEquals(3, decodedByInterface.size());
            Assert.assertEquals(from, decodedByInterface.get(0));
            Assert.assertEquals(id.toString(), decodedByInterface.get(1));
            Assert.assertEquals(value, decodedByInterface.get(2));

            List<String> decodedByInterfaceToString =
                    abiCodec.decodeEventByInterfaceToString(indexedAbi, eventSignature, log);
            Assert.assertEquals(3, decodedByInterfaceToString.size());
            Assert.assertEquals(from, decodedByInterfaceToString.get(0));
            Assert.assertEquals(id.toString(), decodedByInterfaceToString.get(1));
            Assert.assertEquals(value.toString(), decodedByInterfaceToString.get(2));
        } catch (ContractCodecException | ClassNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }
}
