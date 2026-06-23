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
package org.fisco.bcos.sdk.v3.test.transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.DefaultGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.StaticEIP1559GasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.StaticGasProvider;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.BasicDeployRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.BasicRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.CommonConstant;
import org.fisco.bcos.sdk.v3.transaction.model.bo.AbiInfo;
import org.fisco.bcos.sdk.v3.transaction.model.bo.BinInfo;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CommonResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.JsonException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionRetCodeConstants;
import org.fisco.bcos.sdk.v3.transaction.nonce.DefaultNonceAndBlockLimitProvider;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;
import org.junit.Assert;
import org.junit.Test;

public class TxHelpersCoverageTest {

    // ======================= Convert =======================

    @Test
    public void testConvertToWeiFromString() {
        Assert.assertEquals(
                0, Convert.toWei("1", Convert.Unit.WEI).compareTo(BigDecimal.ONE));
        Assert.assertEquals(
                0,
                Convert.toWei("1", Convert.Unit.GWEI)
                        .compareTo(new BigDecimal("1000000000")));
        Assert.assertEquals(
                0,
                Convert.toWei("1", Convert.Unit.ETHER)
                        .compareTo(new BigDecimal("1000000000000000000")));
    }

    @Test
    public void testConvertToWeiFromBigDecimal() {
        BigDecimal result = Convert.toWei(new BigDecimal("2"), Convert.Unit.KWEI);
        Assert.assertEquals(0, result.compareTo(new BigDecimal("2000")));
    }

    @Test
    public void testConvertFromWeiFromString() {
        BigDecimal result = Convert.fromWei("1000000000", Convert.Unit.GWEI);
        Assert.assertEquals(0, result.compareTo(BigDecimal.ONE));
    }

    @Test
    public void testConvertFromWeiFromBigDecimal() {
        BigDecimal result = Convert.fromWei(new BigDecimal("1000"), Convert.Unit.KWEI);
        Assert.assertEquals(0, result.compareTo(BigDecimal.ONE));
    }

    @Test
    public void testConvertAllUnitsRoundTrip() {
        for (Convert.Unit unit : Convert.Unit.values()) {
            BigDecimal wei = Convert.toWei("3", unit);
            BigDecimal back = Convert.fromWei(wei, unit);
            Assert.assertEquals(0, back.compareTo(new BigDecimal("3")));
        }
    }

    @Test
    public void testConvertUnitWeiFactors() {
        Assert.assertEquals(0, Convert.Unit.WEI.getWeiFactor().compareTo(BigDecimal.ONE));
        Assert.assertEquals(
                0, Convert.Unit.MWEI.getWeiFactor().compareTo(new BigDecimal("1000000")));
        Assert.assertEquals(
                0,
                Convert.Unit.SZABO.getWeiFactor().compareTo(new BigDecimal("1000000000000")));
        Assert.assertEquals(
                0,
                Convert.Unit.FINNEY
                        .getWeiFactor()
                        .compareTo(new BigDecimal("1000000000000000")));
        Assert.assertEquals(
                0,
                Convert.Unit.KETHER
                        .getWeiFactor()
                        .compareTo(new BigDecimal("1000000000000000000000")));
        Assert.assertEquals(
                0,
                Convert.Unit.METHER
                        .getWeiFactor()
                        .compareTo(new BigDecimal("1000000000000000000000000")));
        Assert.assertEquals(
                0,
                Convert.Unit.GETHER
                        .getWeiFactor()
                        .compareTo(new BigDecimal("1000000000000000000000000000")));
    }

    @Test
    public void testConvertUnitToString() {
        Assert.assertEquals("wei", Convert.Unit.WEI.toString());
        Assert.assertEquals("gwei", Convert.Unit.GWEI.toString());
        Assert.assertEquals("ether", Convert.Unit.ETHER.toString());
    }

    @Test
    public void testConvertUnitFromString() {
        Assert.assertEquals(Convert.Unit.GWEI, Convert.Unit.fromString("gwei"));
        Assert.assertEquals(Convert.Unit.GWEI, Convert.Unit.fromString("GWEI"));
        Assert.assertEquals(Convert.Unit.ETHER, Convert.Unit.fromString("Ether"));
        // matches enum name even though display name lookup fails
        Assert.assertEquals(Convert.Unit.WEI, Convert.Unit.fromString("WEI"));
    }

    @Test(expected = NullPointerException.class)
    public void testConvertUnitFromStringNull() {
        Convert.Unit.fromString(null);
    }

    // ======================= Gas providers =======================

    @Test
    public void testDefaultGasProvider() {
        DefaultGasProvider provider = new DefaultGasProvider();
        Assert.assertEquals(DefaultGasProvider.GAS_PRICE, provider.getGasPrice("func"));
        Assert.assertEquals(DefaultGasProvider.GAS_LIMIT, provider.getGasLimit("func"));
        Assert.assertEquals(DefaultGasProvider.GAS_PRICE, provider.getGasPrice(new byte[] {1}));
        Assert.assertEquals(DefaultGasProvider.GAS_LIMIT, provider.getGasLimit(new byte[] {1}));
        Assert.assertFalse(provider.isEIP1559Enabled());
        Assert.assertEquals(BigInteger.valueOf(9_000_000), DefaultGasProvider.GAS_LIMIT);
    }

    @Test
    public void testStaticGasProvider() {
        BigInteger price = BigInteger.valueOf(100);
        BigInteger limit = BigInteger.valueOf(2000);
        StaticGasProvider provider = new StaticGasProvider(price, limit);
        Assert.assertEquals(price, provider.getGasPrice("m"));
        Assert.assertEquals(price, provider.getGasPrice(new byte[] {0x1}));
        Assert.assertEquals(limit, provider.getGasLimit("m"));
        Assert.assertEquals(limit, provider.getGasLimit(new byte[] {0x1}));
        Assert.assertFalse(provider.isEIP1559Enabled());

        EIP1559Struct s1 = provider.getEIP1559Struct("m");
        Assert.assertEquals(BigInteger.ZERO, s1.getMaxFeePerGas());
        Assert.assertEquals(BigInteger.ZERO, s1.getMaxPriorityFeePerGas());
        Assert.assertEquals(limit, s1.getGasLimit());

        EIP1559Struct s2 = provider.getEIP1559Struct(new byte[] {0x1});
        Assert.assertEquals(limit, s2.getGasLimit());
    }

    @Test
    public void testStaticEIP1559GasProvider() {
        BigInteger maxFee = BigInteger.valueOf(500);
        BigInteger maxPriority = BigInteger.valueOf(50);
        BigInteger limit = BigInteger.valueOf(3000);
        StaticEIP1559GasProvider provider =
                new StaticEIP1559GasProvider(1L, maxFee, maxPriority, limit);
        Assert.assertEquals(maxFee, provider.getGasPrice("m"));
        Assert.assertEquals(maxFee, provider.getGasPrice(new byte[] {0x2}));
        Assert.assertEquals(limit, provider.getGasLimit("m"));
        Assert.assertEquals(limit, provider.getGasLimit(new byte[] {0x2}));
        Assert.assertTrue(provider.isEIP1559Enabled());

        EIP1559Struct s1 = provider.getEIP1559Struct("m");
        Assert.assertEquals(maxFee, s1.getMaxFeePerGas());
        Assert.assertEquals(maxPriority, s1.getMaxPriorityFeePerGas());
        Assert.assertEquals(limit, s1.getGasLimit());

        EIP1559Struct s2 = provider.getEIP1559Struct(new byte[] {0x2});
        Assert.assertEquals(maxPriority, s2.getMaxPriorityFeePerGas());
    }

    @Test
    public void testEIP1559StructGetters() {
        EIP1559Struct s =
                new EIP1559Struct(
                        BigInteger.valueOf(10),
                        BigInteger.valueOf(2),
                        BigInteger.valueOf(21000));
        Assert.assertEquals(BigInteger.valueOf(10), s.getMaxFeePerGas());
        Assert.assertEquals(BigInteger.valueOf(2), s.getMaxPriorityFeePerGas());
        Assert.assertEquals(BigInteger.valueOf(21000), s.getGasLimit());
    }

    // ======================= Nonce provider (pure parts only) =======================

    @Test
    public void testDefaultNonceProviderGetNonce() {
        DefaultNonceAndBlockLimitProvider provider = new DefaultNonceAndBlockLimitProvider();
        String nonce = provider.getNonce();
        Assert.assertNotNull(nonce);
        Assert.assertFalse(nonce.contains("-"));
        Assert.assertEquals(32, nonce.length());
        Assert.assertNotEquals(nonce, provider.getNonce());
    }

    @Test
    public void testDefaultNonceProviderGetNonceAsync() {
        DefaultNonceAndBlockLimitProvider provider = new DefaultNonceAndBlockLimitProvider();
        final String[] holder = new String[1];
        provider.getNonceAsync(nonce -> holder[0] = nonce);
        Assert.assertNotNull(holder[0]);
        Assert.assertEquals(32, holder[0].length());
    }

    // ======================= model/dto POJOs =======================

    @Test
    public void testCallRequest() {
        byte[] encoded = new byte[] {1, 2, 3};
        CallRequest request = new CallRequest("0xfrom", "0xto", encoded);
        Assert.assertEquals("0xfrom", request.getFrom());
        Assert.assertEquals("0xto", request.getTo());
        Assert.assertArrayEquals(encoded, request.getEncodedFunction());
        Assert.assertNull(request.getAbi());

        request.setFrom("0xa");
        request.setTo("0xb");
        request.setEncodedFunction(new byte[] {9});
        request.setSign("sig");
        Assert.assertEquals("0xa", request.getFrom());
        Assert.assertEquals("0xb", request.getTo());
        Assert.assertArrayEquals(new byte[] {9}, request.getEncodedFunction());
        Assert.assertEquals("sig", request.getSign());

        ABIDefinition abi = new ABIDefinition();
        request.setAbi(abi);
        Assert.assertSame(abi, request.getAbi());
    }

    @Test
    public void testCallRequestWithAbiConstructor() {
        ABIDefinition abi = new ABIDefinition();
        CallRequest request = new CallRequest("0xfrom", "0xto", new byte[] {1}, abi);
        Assert.assertSame(abi, request.getAbi());
        Assert.assertEquals("0xfrom", request.getFrom());
    }

    @Test
    public void testCommonResponse() {
        CommonResponse empty = new CommonResponse();
        Assert.assertEquals(0, empty.getReturnCode());
        Assert.assertEquals("", empty.getReturnMessage());

        CommonResponse response = new CommonResponse(5, "msg");
        Assert.assertEquals(5, response.getReturnCode());
        Assert.assertEquals("msg", response.getReturnMessage());

        response.setReturnCode(7);
        response.setReturnMessage("other");
        Assert.assertEquals(7, response.getReturnCode());
        Assert.assertEquals("other", response.getReturnMessage());
    }

    @Test
    public void testCallResponse() {
        CallResponse response = new CallResponse();
        response.setValues("v");
        response.setReturnObject(Arrays.asList("a", "b"));
        response.setReturnABIObject(Collections.emptyList());
        response.setResults(Collections.emptyList());
        response.setReturnCode(1);
        response.setReturnMessage("done");

        Assert.assertEquals("v", response.getValues());
        Assert.assertEquals(2, response.getReturnObject().size());
        Assert.assertTrue(response.getReturnABIObject().isEmpty());
        Assert.assertTrue(response.getResults().isEmpty());
        Assert.assertEquals(1, response.getReturnCode());
        Assert.assertEquals("done", response.getReturnMessage());
        Assert.assertNotNull(response.toString());
        Assert.assertTrue(response.toString().contains("CallResponse"));
    }

    @Test
    public void testTransactionResponse() {
        TransactionResponse empty = new TransactionResponse();
        Assert.assertEquals(0, empty.getReturnCode());

        TransactionResponse codeMsg = new TransactionResponse(3, "fail");
        Assert.assertEquals(3, codeMsg.getReturnCode());
        Assert.assertEquals("fail", codeMsg.getReturnMessage());

        TransactionResponse response = new TransactionResponse(null, 0, "ok");
        Assert.assertNull(response.getTransactionReceipt());

        response.setContractAddress("0xabc");
        response.setValues("[]");
        response.setEvents(null);
        response.setReceiptMessages("rm");
        response.setReturnObject(Arrays.asList(1, 2));
        response.setReturnABIObject(Collections.emptyList());
        response.setResults(Collections.emptyList());

        Assert.assertEquals("0xabc", response.getContractAddress());
        Assert.assertEquals("[]", response.getValues());
        Assert.assertNull(response.getEvents());
        Assert.assertEquals("rm", response.getReceiptMessages());
        Assert.assertEquals(2, response.getReturnObject().size());
        Assert.assertTrue(response.getReturnABIObject().isEmpty());
        Assert.assertTrue(response.getResults().isEmpty());
    }

    @Test
    public void testTransactionResponseValuesListEmpty() {
        TransactionResponse response = new TransactionResponse();
        Assert.assertNull(response.getValuesList());
        Assert.assertNull(response.getEventResultMap());
        response.setValues("");
        Assert.assertNull(response.getValuesList());
    }

    @Test
    public void testTransactionResponseValuesList() {
        TransactionResponse response = new TransactionResponse();
        response.setValues("[\"a\",\"b\"]");
        List<Object> values = response.getValuesList();
        Assert.assertNotNull(values);
        Assert.assertEquals(2, values.size());
    }

    @Test
    public void testTransactionResponseEventResultMap() {
        TransactionResponse response = new TransactionResponse();
        response.setEvents("{\"ev\":[[\"x\"]]}");
        Map<String, List<List<Object>>> map = response.getEventResultMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey("ev"));
    }

    @Test
    public void testResultCodeEnum() {
        Assert.assertEquals(0, ResultCodeEnum.SUCCESS.getCode());
        Assert.assertEquals("success", ResultCodeEnum.SUCCESS.getMessage());
        Assert.assertEquals(1, ResultCodeEnum.EXECUTE_ERROR.getCode());
        Assert.assertEquals(7, ResultCodeEnum.PARSE_ERROR.getCode());

        ResultCodeEnum value = ResultCodeEnum.UNKNOWN;
        value.setCode(99);
        value.setMessage("changed");
        Assert.assertEquals(99, value.getCode());
        Assert.assertEquals("changed", value.getMessage());
        // restore so test ordering does not affect other assertions
        value.setCode(3);
        value.setMessage("unknown exception");

        Assert.assertEquals(8, ResultCodeEnum.values().length);
        Assert.assertEquals(ResultCodeEnum.EVM_ERROR, ResultCodeEnum.valueOf("EVM_ERROR"));
    }

    // ======================= model/exception =======================

    @Test
    public void testTransactionBaseExceptionWithRetCode() {
        RetCode retCode = new RetCode(-100, "boom");
        TransactionBaseException ex = new TransactionBaseException(retCode);
        Assert.assertEquals("boom", ex.getMessage());
        Assert.assertSame(retCode, ex.getRetCode());
    }

    @Test
    public void testTransactionBaseExceptionWithCodeMsg() {
        TransactionBaseException ex = new TransactionBaseException(-200, "fail");
        Assert.assertEquals("fail", ex.getMessage());
        Assert.assertEquals(-200, ex.getRetCode().getCode());
        Assert.assertEquals("fail", ex.getRetCode().getMessage());
    }

    @Test
    public void testNoSuchTransactionFileException() {
        NoSuchTransactionFileException ex1 = new NoSuchTransactionFileException(-300, "nf");
        Assert.assertEquals(-300, ex1.getRetCode().getCode());

        RetCode retCode = new RetCode(-301, "missing");
        NoSuchTransactionFileException ex2 = new NoSuchTransactionFileException(retCode);
        Assert.assertEquals(-301, ex2.getRetCode().getCode());
        Assert.assertEquals("missing", ex2.getMessage());
    }

    @Test
    public void testTransactionException() {
        TransactionException ex = new TransactionException("oops");
        Assert.assertEquals("oops", ex.getMessage());
        Assert.assertFalse(ex.getTransactionHash().isPresent());
        Assert.assertEquals(0, ex.getStatus());

        ex.setStatus(2);
        ex.setGasUsed(BigInteger.valueOf(123));
        ex.setTransactionHash(Optional.of("0xhash"));
        Assert.assertEquals(2, ex.getStatus());
        Assert.assertEquals(BigInteger.valueOf(123), ex.getGasUsed());
        Assert.assertEquals("0xhash", ex.getTransactionHash().get());

        TransactionException byHash = new TransactionException("m", "0xhh");
        Assert.assertEquals("0xhh", byHash.getTransactionHash().get());

        TransactionException byStatus = new TransactionException("m", 9);
        Assert.assertEquals(9, byStatus.getStatus());

        TransactionException full =
                new TransactionException("m", 1, BigInteger.TEN, "0xf");
        Assert.assertEquals(1, full.getStatus());
        Assert.assertEquals(BigInteger.TEN, full.getGasUsed());
        Assert.assertEquals("0xf", full.getTransactionHash().get());

        TransactionException byCause = new TransactionException(new RuntimeException("rc"));
        Assert.assertNotNull(byCause.getCause());
    }

    @Test
    public void testContractException() {
        ContractException ex = new ContractException("error", -5);
        Assert.assertEquals("error", ex.getMessage());
        Assert.assertEquals(-5, ex.getErrorCode());
        Assert.assertNull(ex.getResponseOutput());
        Assert.assertNull(ex.getReceipt());

        ex.setErrorCode(-7);
        Assert.assertEquals(-7, ex.getErrorCode());

        ContractException msgOnly = new ContractException("only");
        Assert.assertEquals(-1, msgOnly.getErrorCode());

        ContractException withCause = new ContractException("c", new RuntimeException("x"));
        Assert.assertNotNull(withCause.getCause());

        Assert.assertNotNull(ex.toString());
        Assert.assertTrue(ex.toString().contains("ContractException"));
        Assert.assertEquals(ex, ex);
        Assert.assertNotEquals(ex, msgOnly);
        Assert.assertNotEquals(ex, null);
        // same errorCode and null responseOutput -> equal
        ContractException sameAsOnly = new ContractException("diffmsg");
        Assert.assertEquals(msgOnly, sameAsOnly);
        Assert.assertEquals(msgOnly.hashCode(), sameAsOnly.hashCode());
    }

    @Test
    public void testJsonException() {
        Assert.assertNotNull(new JsonException());
        Assert.assertEquals("m", new JsonException("m").getMessage());
        JsonException withCause = new JsonException("m", new RuntimeException("c"));
        Assert.assertNotNull(withCause.getCause());
        Assert.assertNotNull(new JsonException(new RuntimeException("c")).getCause());
    }

    @Test
    public void testTransactionRetCodeConstants() {
        Assert.assertEquals(0, TransactionRetCodeConstants.CODE_SUCCESS.getCode());
        Assert.assertEquals(-22301, TransactionRetCodeConstants.NO_SUCH_BINARY_FILE.getCode());
        Assert.assertEquals(-22302, TransactionRetCodeConstants.NO_SUCH_ABI_FILE.getCode());
        Assert.assertNotNull(new TransactionRetCodeConstants());
    }

    // ======================= model/bo =======================

    @Test
    public void testBinInfo() {
        Map<String, String> bins = new HashMap<>();
        bins.put("HelloWorld", "0x6080");
        BinInfo binInfo = new BinInfo(bins);
        Assert.assertEquals("0x6080", binInfo.getBin("HelloWorld"));
        Assert.assertNull(binInfo.getBin("Missing"));
    }

    @Test
    public void testAbiInfo() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        funcAbis.put("HelloWorld", Collections.singletonList(new ABIDefinition()));
        Map<String, ABIDefinition> constructAbis = new HashMap<>();
        ABIDefinition constructor = new ABIDefinition();
        constructAbis.put("HelloWorld", constructor);

        AbiInfo abiInfo = new AbiInfo(funcAbis, constructAbis);
        Assert.assertEquals(1, abiInfo.findFuncAbis("HelloWorld").size());
        Assert.assertSame(constructor, abiInfo.findConstructor("HelloWorld"));
        Assert.assertNull(abiInfo.findConstructor("Missing"));
    }

    @Test(expected = RuntimeException.class)
    public void testAbiInfoFindFuncAbisMissing() {
        AbiInfo abiInfo = new AbiInfo(new HashMap<>(), new HashMap<>());
        abiInfo.findFuncAbis("Nope");
    }

    @Test
    public void testCommonConstant() {
        Assert.assertEquals("binary", CommonConstant.BIN);
        Assert.assertEquals("abi", CommonConstant.ABI);
        Assert.assertEquals("constructor", CommonConstant.ABI_CONSTRUCTOR);
        Assert.assertEquals("function", CommonConstant.ABI_FUNCTION);
        Assert.assertNotNull(new CommonConstant());
    }

    // ======================= transactionv1 dto =======================

    @Test
    public void testBasicRequestShortConstructor() {
        EIP1559Struct eip = new EIP1559Struct(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN);
        BasicRequest request =
                new BasicRequest(
                        "abiStr",
                        "method",
                        "0xto",
                        BigInteger.valueOf(1),
                        BigInteger.valueOf(2),
                        BigInteger.valueOf(3),
                        eip);
        Assert.assertEquals("abiStr", request.getAbi());
        Assert.assertEquals("method", request.getMethod());
        Assert.assertEquals("0xto", request.getTo());
        Assert.assertEquals(BigInteger.valueOf(1), request.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), request.getGasPrice());
        Assert.assertEquals(BigInteger.valueOf(3), request.getGasLimit());
        Assert.assertSame(eip, request.getEip1559Struct());
        Assert.assertTrue(request.isEIP1559Enabled());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        Assert.assertEquals(TransactionVersion.V1, request.getVersion());
    }

    @Test
    public void testBasicRequestSetters() {
        BasicRequest request =
                new BasicRequest(null, null, null, null, null, null, null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        Assert.assertFalse(request.isEIP1559Enabled());

        request.setBlockLimit(BigInteger.valueOf(500));
        request.setNonce("nonce123");
        request.setExtension(new byte[] {7});
        Assert.assertEquals(BigInteger.valueOf(500), request.getBlockLimit());
        Assert.assertEquals("nonce123", request.getNonce());
        Assert.assertArrayEquals(new byte[] {7}, request.getExtension());
    }

    @Test
    public void testBasicRequestVersionSemantics() {
        BasicRequest request =
                new BasicRequest(null, null, null, null, null, null, null);
        // default V1, setVersion only upgrades
        request.setVersion(TransactionVersion.V0);
        Assert.assertEquals(TransactionVersion.V1, request.getVersion());
        request.setVersion(TransactionVersion.V2);
        Assert.assertEquals(TransactionVersion.V2, request.getVersion());
        // force downgrades
        request.setVersionForce(TransactionVersion.V0);
        Assert.assertEquals(TransactionVersion.V0, request.getVersion());
    }

    @Test
    public void testBasicRequestFullConstructor() {
        BasicRequest request =
                new BasicRequest(
                        TransactionVersion.V2,
                        "abi",
                        "method",
                        "0xto",
                        BigInteger.valueOf(100),
                        "nonceVal",
                        BigInteger.valueOf(1),
                        BigInteger.valueOf(2),
                        BigInteger.valueOf(3),
                        null,
                        new byte[] {5});
        Assert.assertEquals(TransactionVersion.V2, request.getVersion());
        Assert.assertEquals(BigInteger.valueOf(100), request.getBlockLimit());
        Assert.assertEquals("nonceVal", request.getNonce());
        Assert.assertArrayEquals(new byte[] {5}, request.getExtension());
        Assert.assertFalse(request.isEIP1559Enabled());
    }

    @Test
    public void testBasicDeployRequest() {
        BasicDeployRequest request =
                new BasicDeployRequest(
                        "abi",
                        "0xbin",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null);
        Assert.assertEquals("0xbin", request.getBin());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        request.setBin(null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setBin("0xnew");
        request.setTo("0xtarget");
        Assert.assertEquals("0xnew", request.getBin());
        Assert.assertEquals("0xtarget", request.getTo());
        Assert.assertNotNull(request.toString());
    }

    @Test
    public void testBasicDeployRequestFullConstructor() {
        BasicDeployRequest request =
                new BasicDeployRequest(
                        TransactionVersion.V1,
                        "abi",
                        "0xbin",
                        BigInteger.valueOf(50),
                        "n",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertEquals("0xbin", request.getBin());
        Assert.assertEquals(BigInteger.valueOf(50), request.getBlockLimit());
    }

    @Test
    public void testTransactionRequest() {
        TransactionRequest request =
                new TransactionRequest(
                        "abi",
                        "set",
                        "0xto",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setParams(Arrays.asList("p1", "p2"));
        Assert.assertEquals(2, request.getParams().size());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        Assert.assertNotNull(request.toString());
        Assert.assertTrue(request.toString().contains("TransactionRequest"));
    }

    @Test
    public void testTransactionRequestFullConstructor() {
        TransactionRequest request =
                new TransactionRequest(
                        TransactionVersion.V1,
                        "abi",
                        "set",
                        "0xto",
                        BigInteger.valueOf(10),
                        "nonce",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertEquals("set", request.getMethod());
        Assert.assertEquals("nonce", request.getNonce());
    }

    @Test
    public void testTransactionRequestWithStringParams() {
        TransactionRequestWithStringParams request =
                new TransactionRequestWithStringParams(
                        "abi",
                        "set",
                        "0xto",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setStringParams(Collections.singletonList("v"));
        Assert.assertEquals(1, request.getStringParams().size());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        Assert.assertNotNull(request.toString());

        TransactionRequestWithStringParams full =
                new TransactionRequestWithStringParams(
                        TransactionVersion.V1,
                        "abi",
                        "set",
                        "0xto",
                        BigInteger.valueOf(10),
                        "nonce",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertEquals("nonce", full.getNonce());
    }

    @Test
    public void testDeployTransactionRequest() {
        DeployTransactionRequest request =
                new DeployTransactionRequest(
                        "abi",
                        "0xbin",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setParams(Collections.singletonList("p"));
        Assert.assertEquals(1, request.getParams().size());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());

        DeployTransactionRequest full =
                new DeployTransactionRequest(
                        TransactionVersion.V1,
                        "abi",
                        "0xbin",
                        BigInteger.valueOf(10),
                        "nonce",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertEquals("0xbin", full.getBin());
    }

    @Test
    public void testDeployTransactionRequestWithStringParams() {
        DeployTransactionRequestWithStringParams request =
                new DeployTransactionRequestWithStringParams(
                        "abi",
                        "0xbin",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setStringParams(Collections.singletonList("p"));
        Assert.assertEquals(1, request.getStringParams().size());
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        Assert.assertNotNull(request.toString());

        DeployTransactionRequestWithStringParams full =
                new DeployTransactionRequestWithStringParams(
                        TransactionVersion.V1,
                        "abi",
                        "0xbin",
                        BigInteger.valueOf(10),
                        "nonce",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertEquals("0xbin", full.getBin());
    }

    @Test
    public void testAbiEncodedRequest() {
        AbiEncodedRequest request =
                new AbiEncodedRequest(
                        TransactionVersion.V1,
                        "abi",
                        "0xto",
                        BigInteger.valueOf(10),
                        "nonce",
                        BigInteger.ZERO,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        Assert.assertFalse(request.isTransactionEssentialSatisfy());
        request.setEncodedData(new byte[] {1, 2});
        Assert.assertTrue(request.isTransactionEssentialSatisfy());
        Assert.assertArrayEquals(new byte[] {1, 2}, request.getEncodedData());
        Assert.assertFalse(request.isCreate());
        request.setCreate(true);
        Assert.assertTrue(request.isCreate());
    }

    @Test
    public void testAbiEncodedRequestFromBasicRequest() {
        BasicRequest base =
                new BasicRequest(
                        TransactionVersion.V1,
                        "abi",
                        "method",
                        "0xto",
                        BigInteger.valueOf(5),
                        "n",
                        BigInteger.ONE,
                        BigInteger.ONE,
                        BigInteger.TEN,
                        null,
                        null);
        AbiEncodedRequest request = new AbiEncodedRequest(base);
        Assert.assertEquals("0xto", request.getTo());
        Assert.assertEquals("abi", request.getAbi());
        Assert.assertEquals(BigInteger.valueOf(5), request.getBlockLimit());
    }

    // ======================= TransactionRequestBuilder (pure builder paths) =======================

    @Test
    public void testBuilderBuildRequest() throws ContractException {
        TransactionRequestBuilder builder =
                new TransactionRequestBuilder("abi", "method", "0xto");
        TransactionRequest request = builder.buildRequest(Collections.singletonList("p"));
        Assert.assertEquals("abi", request.getAbi());
        Assert.assertEquals("method", request.getMethod());
        Assert.assertEquals("0xto", request.getTo());
        Assert.assertEquals(1, request.getParams().size());
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildRequestNullParams() throws ContractException {
        new TransactionRequestBuilder("abi", "method", "0xto").buildRequest(null);
    }

    @Test
    public void testBuilderBuildStringParamsRequest() throws ContractException {
        TransactionRequestBuilder builder =
                new TransactionRequestBuilder("abi", "method", "0xto");
        TransactionRequestWithStringParams request =
                builder.buildStringParamsRequest(Collections.singletonList("v"));
        Assert.assertEquals(1, request.getStringParams().size());
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildStringParamsRequestNull() throws ContractException {
        new TransactionRequestBuilder("abi", "method", "0xto").buildStringParamsRequest(null);
    }

    @Test
    public void testBuilderBuildDeployRequest() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder("abi", "0xbin");
        builder.setTo("0xtarget");
        DeployTransactionRequest request =
                builder.buildDeployRequest(Collections.singletonList("p"));
        Assert.assertEquals("0xbin", request.getBin());
        Assert.assertEquals("0xtarget", request.getTo());
        Assert.assertEquals(1, request.getParams().size());
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildDeployRequestNullParams() throws ContractException {
        new TransactionRequestBuilder("abi", "0xbin").buildDeployRequest(null);
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildDeployRequestNoBin() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder();
        builder.setAbi("abi");
        builder.buildDeployRequest(Collections.singletonList("p"));
    }

    @Test
    public void testBuilderBuildDeployStringParamsRequest() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder("abi", "0xbin");
        DeployTransactionRequestWithStringParams request =
                builder.buildDeployStringParamsRequest(Collections.singletonList("v"));
        Assert.assertEquals(1, request.getStringParams().size());
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildDeployStringParamsRequestNullParams() throws ContractException {
        new TransactionRequestBuilder("abi", "0xbin").buildDeployStringParamsRequest(null);
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildDeployStringParamsRequestNoBin() throws ContractException {
        new TransactionRequestBuilder().setAbi("abi").buildDeployStringParamsRequest(
                Collections.singletonList("v"));
    }

    @Test
    public void testBuilderBuildAbiEncodedRequest() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder();
        builder.setAbi("abi").setTo("0xto");
        AbiEncodedRequest request = builder.buildAbiEncodedRequest(new byte[] {9, 8});
        Assert.assertArrayEquals(new byte[] {9, 8}, request.getEncodedData());
        Assert.assertEquals("0xto", request.getTo());
    }

    @Test(expected = ContractException.class)
    public void testBuilderBuildAbiEncodedRequestNull() throws ContractException {
        new TransactionRequestBuilder().buildAbiEncodedRequest(null);
    }

    @Test
    public void testBuilderSettersAndVersion() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder();
        builder.setAbi("abi")
                .setMethod("method")
                .setTo("0xto")
                .setBin("0xbin")
                .setBlockLimit(BigInteger.valueOf(99))
                .setNonce("nn")
                .setValue(BigInteger.ONE)
                .setGasPrice(BigInteger.TEN)
                .setGasLimit(BigInteger.valueOf(21000))
                .setEIP1559Struct(
                        new EIP1559Struct(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN));
        // setExtension with non-empty triggers V2
        builder.setExtension(new byte[] {1});
        TransactionRequest request = builder.buildRequest(Collections.singletonList("p"));
        Assert.assertEquals(TransactionVersion.V2, request.getVersion());
        Assert.assertEquals(BigInteger.valueOf(99), request.getBlockLimit());
        Assert.assertEquals("nn", request.getNonce());
        Assert.assertEquals(BigInteger.ONE, request.getValue());
        Assert.assertEquals(BigInteger.TEN, request.getGasPrice());
        Assert.assertEquals(BigInteger.valueOf(21000), request.getGasLimit());
    }

    @Test
    public void testBuilderVersionForceAndNullBranches() throws ContractException {
        TransactionRequestBuilder builder = new TransactionRequestBuilder("abi", "m", "0xto");
        // null branches should not upgrade version
        builder.setValue(null).setGasPrice(null).setGasLimit(null).setEIP1559Struct(null);
        builder.setExtension(new byte[] {}); // empty extension does not bump to V2
        builder.setVersionForce(TransactionVersion.V0);
        Assert.assertEquals(TransactionVersion.V0, builder.buildRequest(
                Collections.singletonList("p")).getVersion());
        // setVersion only upgrades
        builder.setVersion(TransactionVersion.V1);
        Assert.assertEquals(TransactionVersion.V1, builder.buildRequest(
                Collections.singletonList("p")).getVersion());
        builder.setVersion(TransactionVersion.V0);
        Assert.assertEquals(TransactionVersion.V1, builder.buildRequest(
                Collections.singletonList("p")).getVersion());
    }
}
