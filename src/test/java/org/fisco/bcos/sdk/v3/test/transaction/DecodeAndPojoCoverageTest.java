/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.test.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.auth.po.AccessStatus;
import org.fisco.bcos.sdk.v3.contract.auth.po.AuthType;
import org.fisco.bcos.sdk.v3.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.GovernorInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalStatus;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalType;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.RevertMessageParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class DecodeAndPojoCoverageTest {

    /** A valid abi-encoded "Error(string)" revert output decoding to "test string". */
    private static final String REVERT_OUTPUT =
            "0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000b7465737420737472696e67000000000000000000000000000000000000000000";

    // ----------------------------------------------------------------------------------
    // auth.po enums / pojos
    // ----------------------------------------------------------------------------------

    @Test
    public void testAuthTypeValues() throws ContractException {
        Assert.assertEquals(BigInteger.valueOf(0), AuthType.NO_ACL.getValue());
        Assert.assertEquals(BigInteger.valueOf(1), AuthType.WHITE_LIST.getValue());
        Assert.assertEquals(BigInteger.valueOf(2), AuthType.BLACK_LIST.getValue());

        Assert.assertEquals(AuthType.NO_ACL, AuthType.valueOf(0));
        Assert.assertEquals(AuthType.WHITE_LIST, AuthType.valueOf(1));
        Assert.assertEquals(AuthType.BLACK_LIST, AuthType.valueOf(2));

        Assert.assertEquals("NO_ACL", AuthType.NO_ACL.toString());
        Assert.assertEquals("WHITE_LIST", AuthType.WHITE_LIST.toString());
        Assert.assertEquals("BLACK_LIST", AuthType.BLACK_LIST.toString());
    }

    @Test(expected = ContractException.class)
    public void testAuthTypeInvalidValueOf() throws ContractException {
        AuthType.valueOf(99);
    }

    @Test
    public void testAccessStatus() {
        Assert.assertEquals(0, AccessStatus.Normal.getStatus());
        Assert.assertEquals(1, AccessStatus.Freeze.getStatus());
        Assert.assertEquals(2, AccessStatus.Abolish.getStatus());
        Assert.assertEquals(-1, AccessStatus.Unknown.getStatus());

        Assert.assertEquals(BigInteger.valueOf(0), AccessStatus.Normal.getBigIntStatus());
        Assert.assertEquals(BigInteger.valueOf(-1), AccessStatus.Unknown.getBigIntStatus());

        Assert.assertEquals(AccessStatus.Normal, AccessStatus.getAccessStatus(0));
        Assert.assertEquals(AccessStatus.Freeze, AccessStatus.getAccessStatus(1));
        Assert.assertEquals(AccessStatus.Abolish, AccessStatus.getAccessStatus(2));
        Assert.assertEquals(AccessStatus.Unknown, AccessStatus.getAccessStatus(3));
        Assert.assertEquals(AccessStatus.Unknown, AccessStatus.getAccessStatus(-5));
    }

    @Test
    public void testProposalStatus() {
        Assert.assertEquals("notEnoughVotes", ProposalStatus.NOT_ENOUGH_VOTE.getValue());
        Assert.assertEquals("finished", ProposalStatus.FINISHED.getValue());
        Assert.assertEquals("failed", ProposalStatus.FAILED.getValue());
        Assert.assertEquals("revoke", ProposalStatus.REVOKE.getValue());
        Assert.assertEquals("outdated", ProposalStatus.OUTDATED.getValue());
        Assert.assertEquals("unknown", ProposalStatus.UNKNOWN.getValue());

        Assert.assertEquals(ProposalStatus.NOT_ENOUGH_VOTE, ProposalStatus.fromInt(1));
        Assert.assertEquals(ProposalStatus.FINISHED, ProposalStatus.fromInt(2));
        Assert.assertEquals(ProposalStatus.FAILED, ProposalStatus.fromInt(3));
        Assert.assertEquals(ProposalStatus.REVOKE, ProposalStatus.fromInt(4));
        Assert.assertEquals(ProposalStatus.OUTDATED, ProposalStatus.fromInt(5));
        Assert.assertEquals(ProposalStatus.UNKNOWN, ProposalStatus.fromInt(99));
    }

    @Test
    public void testProposalType() {
        Assert.assertEquals("setWeight", ProposalType.SET_WEIGHT.getValue());
        Assert.assertEquals("setRate", ProposalType.SET_RATE.getValue());
        Assert.assertEquals("upgradeVoteCalc", ProposalType.UPGRADE_VOTE_CALC.getValue());
        Assert.assertEquals("unknown", ProposalType.UNKNOWN.getValue());

        Assert.assertEquals(ProposalType.SET_WEIGHT, ProposalType.fromInt(11));
        Assert.assertEquals(ProposalType.SET_RATE, ProposalType.fromInt(12));
        Assert.assertEquals(ProposalType.UPGRADE_VOTE_CALC, ProposalType.fromInt(13));
        Assert.assertEquals(ProposalType.SET_DEPLOY_AUTH_TYPE, ProposalType.fromInt(21));
        Assert.assertEquals(ProposalType.MODIFY_DEPLOY_AUTH, ProposalType.fromInt(22));
        Assert.assertEquals(ProposalType.RESET_ADMIN, ProposalType.fromInt(31));
        Assert.assertEquals(ProposalType.SET_CONFIG, ProposalType.fromInt(41));
        Assert.assertEquals(ProposalType.SET_NODE_WEIGHT, ProposalType.fromInt(51));
        Assert.assertEquals(ProposalType.REMOVE_NODE, ProposalType.fromInt(52));
        Assert.assertEquals(ProposalType.SET_ACCOUNT_STATUS, ProposalType.fromInt(61));
        Assert.assertEquals(ProposalType.UNKNOWN, ProposalType.fromInt(999));
    }

    @Test
    public void testGovernorInfo() {
        GovernorInfo info =
                new GovernorInfo(
                        "0x1234567890123456789012345678901234567890", BigInteger.valueOf(5));
        Assert.assertEquals("0x1234567890123456789012345678901234567890", info.getGovernorAddress());
        Assert.assertEquals(BigInteger.valueOf(5), info.getWeight());
        Assert.assertTrue(info.toString().contains("0x1234567890123456789012345678901234567890"));
        Assert.assertTrue(info.toString().contains("5"));
    }

    @Test
    public void testCommitteeInfo() {
        CommitteeInfo committeeInfo = new CommitteeInfo();
        List<GovernorInfo> governors = new ArrayList<>();
        governors.add(
                new GovernorInfo(
                        "0x1234567890123456789012345678901234567890", BigInteger.valueOf(3)));
        committeeInfo.setGovernorList(governors);
        committeeInfo.setParticipatesRate(50);
        committeeInfo.setWinRate(60);

        Assert.assertEquals(governors, committeeInfo.getGovernorList());
        Assert.assertEquals(1, committeeInfo.getGovernorList().size());
        Assert.assertEquals(50, committeeInfo.getParticipatesRate());
        Assert.assertEquals(60, committeeInfo.getWinRate());
        Assert.assertTrue(committeeInfo.toString().contains("participatesRate=50"));
        Assert.assertTrue(committeeInfo.toString().contains("winRate=60"));
    }

    @Test
    public void testProposalInfoStringConstructor() {
        String addr = "0x1234567890123456789012345678901234567890";
        List<String> agree = Arrays.asList(addr);
        List<String> against = new ArrayList<>();
        ProposalInfo info =
                new ProposalInfo(
                        addr, addr, 11, BigInteger.valueOf(100), 2, agree, against);

        Assert.assertEquals(addr, info.getResourceId());
        Assert.assertEquals(addr, info.getProposer());
        Assert.assertEquals(11, info.getProposalType());
        Assert.assertEquals("setWeight", info.getProposalTypeString());
        Assert.assertEquals(BigInteger.valueOf(100), info.getBlockNumberInterval());
        Assert.assertEquals(2, info.getStatus());
        Assert.assertEquals("finished", info.getStatusString());
        Assert.assertEquals(agree, info.getAgreeVoters());
        Assert.assertEquals(against, info.getAgainstVoters());
        Assert.assertTrue(info.toString().contains("ProposalInfo"));
        Assert.assertTrue(info.toString().contains("setWeight"));
        Assert.assertTrue(info.toString().contains("finished"));
    }

    @Test
    public void testProposalInfoDefaultConstructorThrowsOnEmptyAddress() {
        // Characterization test: new ProposalInfo() builds new Address("") which calls
        // Numeric.toBigInt("") and throws NumberFormatException. Documents current behavior of the
        // no-arg constructor (a possible latent issue on the chain-decode path).
        try {
            new ProposalInfo();
            Assert.fail("expected NumberFormatException from empty Address");
        } catch (NumberFormatException expected) {
            // expected
        }
    }

    // ----------------------------------------------------------------------------------
    // TransactionReceiptStatus / RetCode lookups
    // ----------------------------------------------------------------------------------

    @Test
    public void testTransactionReceiptStatusKnownCode() {
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(0, "ignored");
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertEquals("Success", retCode.getMessage());

        RetCode outOfGas = TransactionReceiptStatus.getStatusMessage(12, "ignored");
        Assert.assertEquals(12, outOfGas.getCode());
        Assert.assertEquals(TransactionReceiptStatus.OutOfGas.getMessage(), outOfGas.getMessage());

        RetCode precompiled = TransactionReceiptStatus.getStatusMessage(15, "ignored");
        Assert.assertEquals(15, precompiled.getCode());
    }

    @Test
    public void testTransactionReceiptStatusUnknownCode() {
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(987654, "custom message");
        Assert.assertEquals(987654, retCode.getCode());
        Assert.assertEquals("custom message", retCode.getMessage());
    }

    // ----------------------------------------------------------------------------------
    // RevertMessageParser
    // ----------------------------------------------------------------------------------

    @Test
    public void testIsOutputStartWithRevertMethod() {
        Assert.assertTrue(RevertMessageParser.isOutputStartWithRevertMethod(REVERT_OUTPUT));
        Assert.assertTrue(
                RevertMessageParser.isOutputStartWithRevertMethod("0xc703cb1200000000"));
        Assert.assertFalse(RevertMessageParser.isOutputStartWithRevertMethod("0x1234"));
    }

    @Test
    public void testHasRevertMessage() {
        Assert.assertTrue(RevertMessageParser.hasRevertMessage(12, REVERT_OUTPUT));
        // status 0 -> no revert
        Assert.assertFalse(RevertMessageParser.hasRevertMessage(0, REVERT_OUTPUT));
        // empty output -> false
        Assert.assertFalse(RevertMessageParser.hasRevertMessage(12, ""));
        // not a revert selector
        Assert.assertFalse(RevertMessageParser.hasRevertMessage(12, "0x1234"));
    }

    @Test
    public void testTryResolveRevertMessageFromStatusAndOutput() {
        Tuple2<Boolean, String> resolved =
                RevertMessageParser.tryResolveRevertMessage(12, REVERT_OUTPUT);
        Assert.assertTrue(resolved.getValue1());
        Assert.assertEquals("test string", resolved.getValue2());
    }

    @Test
    public void testTryResolveRevertMessageNoRevert() {
        Tuple2<Boolean, String> resolved =
                RevertMessageParser.tryResolveRevertMessage(0, REVERT_OUTPUT);
        Assert.assertFalse(resolved.getValue1());
        Assert.assertNull(resolved.getValue2());
    }

    @Test
    public void testTryResolveRevertMessageFromReceipt() {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(12);
        receipt.setOutput(REVERT_OUTPUT);
        Tuple2<Boolean, String> resolved = RevertMessageParser.tryResolveRevertMessage(receipt);
        Assert.assertTrue(resolved.getValue1());
        Assert.assertEquals("test string", resolved.getValue2());
    }

    @Test
    public void testRevertMethodConstants() {
        Assert.assertEquals("08c379a0", RevertMessageParser.REVERT_METHOD);
        Assert.assertEquals("c703cb12", RevertMessageParser.SM_REVERT_METHOD);
    }

    // ----------------------------------------------------------------------------------
    // ReceiptParser
    // ----------------------------------------------------------------------------------

    @Test
    public void testParseTransactionReceiptSuccessNullCaller() throws ContractException {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(0);
        receipt.setOutput("0x");
        RetCode retCode = ReceiptParser.parseTransactionReceipt(receipt, null);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.code, retCode.getCode());
    }

    @Test
    public void testParseTransactionReceiptSuccessWithCaller() throws ContractException {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(0);
        receipt.setOutput(
                "0x000000000000000000000000000000000000000000000000000000000000007b");
        receipt.setMessage("");
        RetCode retCode =
                ReceiptParser.parseTransactionReceipt(receipt, r -> BigInteger.valueOf(0));
        Assert.assertNotNull(retCode);
        // resultCaller returns 0 -> success code path
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertEquals(receipt, retCode.getTransactionReceipt());
    }

    @Test
    public void testParseTransactionReceiptErrorThrows() {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(12);
        receipt.setOutput(REVERT_OUTPUT);
        receipt.setMessage("revert");
        Assert.assertThrows(
                ContractException.class,
                () -> ReceiptParser.parseTransactionReceipt(receipt, null));
    }

    @Test
    public void testParseTransactionReceiptErrorRevertMessage() {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(12);
        receipt.setOutput(REVERT_OUTPUT);
        receipt.setMessage("revert");
        try {
            ReceiptParser.parseTransactionReceipt(receipt, null);
            Assert.fail("expected ContractException");
        } catch (ContractException e) {
            Assert.assertEquals("test string", e.getMessage());
            Assert.assertEquals(12, e.getErrorCode());
            Assert.assertEquals(receipt, e.getReceipt());
        }
    }

    @Test
    public void testGetErrorStatusNoRevertMessage() {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(16);
        receipt.setOutput("0x");
        receipt.setMessage("");
        try {
            ReceiptParser.getErrorStatus(receipt);
            Assert.fail("expected ContractException");
        } catch (ContractException e) {
            // status 16 -> RevertInstruction in TransactionReceiptStatus
            Assert.assertEquals(16, e.getErrorCode());
            Assert.assertEquals(
                    TransactionReceiptStatus.RevertInstruction.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testParseCallOutputSuccess() {
        Call.CallOutput output = new Call.CallOutput();
        output.setStatus(0);
        output.setOutput("0x");
        RetCode retCode = ReceiptParser.parseCallOutput(output, "msg");
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.code, retCode.getCode());
    }

    @Test
    public void testParseCallOutputErrorWithRevertMessage() {
        Call.CallOutput output = new Call.CallOutput();
        output.setStatus(12);
        output.setOutput(REVERT_OUTPUT);
        RetCode retCode = ReceiptParser.parseCallOutput(output, "fallback");
        Assert.assertEquals(12, retCode.getCode());
        Assert.assertEquals("test string", retCode.getMessage());
    }

    @Test
    public void testParseCallOutputErrorNoRevertMessage() {
        Call.CallOutput output = new Call.CallOutput();
        output.setStatus(12);
        output.setOutput("0x");
        RetCode retCode = ReceiptParser.parseCallOutput(output, "fallback");
        // status 12 maps to OutOfGas message in TransactionReceiptStatus
        Assert.assertEquals(12, retCode.getCode());
        Assert.assertEquals(TransactionReceiptStatus.OutOfGas.getMessage(), retCode.getMessage());
    }

    @Test
    public void testParseExceptionCallWithNullOutput() {
        ContractException ce = new ContractException("plain", 5);
        ContractException result = ReceiptParser.parseExceptionCall(ce);
        // null responseOutput -> original returned
        Assert.assertSame(ce, result);
    }

    @Test
    public void testParseExceptionCallWithOutput() {
        Call.CallOutput output = new Call.CallOutput();
        output.setStatus(12);
        output.setOutput(REVERT_OUTPUT);
        ContractException ce = new ContractException("orig", output);
        ContractException result = ReceiptParser.parseExceptionCall(ce);
        Assert.assertEquals(12, result.getErrorCode());
        Assert.assertEquals("test string", result.getMessage());
    }

    // ----------------------------------------------------------------------------------
    // SignatureResult subclasses
    // ----------------------------------------------------------------------------------

    @Test
    public void testECDSASignatureResultFromBytes() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        for (int i = 0; i < 32; i++) {
            r[i] = (byte) (i + 1);
            s[i] = (byte) (i + 100);
        }
        byte v = 1;
        ECDSASignatureResult result = new ECDSASignatureResult(v, r, s);
        Assert.assertArrayEquals(r, result.getR());
        Assert.assertArrayEquals(s, result.getS());
        Assert.assertEquals(v, result.getV());

        String str = result.convertToString();
        // 65 bytes -> 130 hex chars
        Assert.assertEquals(130, str.length());
        Assert.assertEquals(str, result.toString());

        byte[] encoded = result.encode();
        Assert.assertEquals(65, encoded.length);
        Assert.assertArrayEquals(r, Arrays.copyOfRange(encoded, 0, 32));
        Assert.assertArrayEquals(s, Arrays.copyOfRange(encoded, 32, 64));
        Assert.assertEquals(v, encoded[64]);

        result.setV((byte) 2);
        Assert.assertEquals((byte) 2, result.getV());
    }

    @Test
    public void testECDSASignatureResultFromString() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        for (int i = 0; i < 32; i++) {
            r[i] = (byte) (i + 1);
            s[i] = (byte) (i + 50);
        }
        byte v = 1;
        ECDSASignatureResult source = new ECDSASignatureResult(v, r, s);
        String signatureStr = source.convertToString();

        ECDSASignatureResult parsed = new ECDSASignatureResult(signatureStr);
        Assert.assertArrayEquals(r, parsed.getR());
        Assert.assertArrayEquals(s, parsed.getS());
        Assert.assertEquals(v, parsed.getV());
        Assert.assertEquals(signatureStr, parsed.convertToString());
        Assert.assertNotNull(parsed.getSignatureBytes());
        Assert.assertEquals(65, parsed.getSignatureBytes().length);
    }

    @Test
    public void testSM2SignatureResultFromBytes() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        byte[] pub = new byte[64];
        for (int i = 0; i < 32; i++) {
            r[i] = (byte) (i + 1);
            s[i] = (byte) (i + 2);
        }
        for (int i = 0; i < 64; i++) {
            pub[i] = (byte) (i + 3);
        }
        SM2SignatureResult result = new SM2SignatureResult(pub, r, s);
        Assert.assertArrayEquals(r, result.getR());
        Assert.assertArrayEquals(s, result.getS());
        Assert.assertArrayEquals(pub, result.getPub());

        String str = result.convertToString();
        // 64 bytes [r,s] -> 128 hex chars
        Assert.assertEquals(128, str.length());
        Assert.assertEquals(str, result.toString());

        byte[] encoded = result.encode();
        // [r,s,pub] = 32 + 32 + 64
        Assert.assertEquals(128, encoded.length);
        Assert.assertArrayEquals(r, Arrays.copyOfRange(encoded, 0, 32));
        Assert.assertArrayEquals(s, Arrays.copyOfRange(encoded, 32, 64));
        Assert.assertArrayEquals(pub, Arrays.copyOfRange(encoded, 64, 128));

        byte[] newPub = new byte[64];
        Arrays.fill(newPub, (byte) 7);
        result.setPub(newPub);
        Assert.assertArrayEquals(newPub, result.getPub());
    }

    @Test
    public void testSignatureResultSetters() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        ECDSASignatureResult result = new ECDSASignatureResult((byte) 0, r, s);
        byte[] newR = new byte[32];
        byte[] newS = new byte[32];
        Arrays.fill(newR, (byte) 9);
        Arrays.fill(newS, (byte) 8);
        result.setR(newR);
        result.setS(newS);
        Assert.assertArrayEquals(newR, result.getR());
        Assert.assertArrayEquals(newS, result.getS());

        byte[] sigBytes = new byte[65];
        result.setSignatureBytes(sigBytes);
        Assert.assertArrayEquals(sigBytes, result.getSignatureBytes());
    }

    // ----------------------------------------------------------------------------------
    // Hash classes
    // ----------------------------------------------------------------------------------

    @Test
    public void testKeccak256KnownVectors() {
        Hash hasher = new Keccak256();
        Assert.assertEquals(
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb",
                hasher.hash("abcde"));
        Assert.assertEquals(
                "1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8",
                hasher.hash("hello"));
        Assert.assertEquals(
                "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470",
                hasher.hash(""));

        byte[] bytesHash = hasher.hash("abcde".getBytes());
        Assert.assertEquals(32, bytesHash.length);
        Assert.assertEquals(
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb",
                Hex.toHexString(bytesHash));
        Assert.assertEquals(
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb",
                hasher.hashBytes("abcde".getBytes()));

        Assert.assertEquals(
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb",
                Keccak256.calculateHash("abcde".getBytes()));
    }

    @Test
    public void testSM3KnownVectors() {
        Hash hasher = new SM3Hash();
        Assert.assertEquals(
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628",
                hasher.hash("abcde"));
        Assert.assertEquals(
                "becbbfaae6548b8bf0cfcad5a27183cd1be6093b1cceccc303d9c61d0a645268",
                hasher.hash("hello"));
        Assert.assertEquals(
                "1ab21d8355cfa17f8e61194831e81a8f22bec8c728fefb747ed035eb5082aa2b",
                hasher.hash(""));

        byte[] bytesHash = hasher.hash("abcde".getBytes());
        Assert.assertEquals(32, bytesHash.length);
        Assert.assertEquals(
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628",
                Hex.toHexString(bytesHash));
        Assert.assertEquals(
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628",
                hasher.hashBytes("abcde".getBytes()));

        Assert.assertEquals(
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628",
                SM3Hash.calculateHash("abcde".getBytes()));
    }
}
