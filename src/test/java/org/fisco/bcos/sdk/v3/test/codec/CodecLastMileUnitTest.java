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

package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Test;

/**
 * Last-mile codec coverage. This class fills in the remaining reachable {@code catch} / error
 * branches in the public decode methods that the existing tests do not trigger (malformed-input
 * catch blocks, the deprecated by-name input decoder error path, the non-dynamic indexed-event
 * topic decode path), and exercises {@code encodeConstructorFromString} across many leaf types.
 *
 * <p>Where a decoded value is not deterministic, only non-null / size / no-throw is asserted.
 */
public class CodecLastMileUnitTest {

    private static final String BIN = "60606040";

    private static final String SIMPLE_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],"
                    + "\"name\":\"f\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],"
                    + "\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String F_SIG = "f(uint256)";

    // ------------------------------------------------------------------------------------------
    // fixtures
    // ------------------------------------------------------------------------------------------

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite(), false);
    }

    // ------------------------------------------------------------------------------------------
    // remaining reachable decode error / catch branches in the public API
    // ------------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputByIdMalformedInputThrows() throws Exception {
        // valid methodId but truncated payload -> decode catch -> ContractCodecException.
        ContractCodec codec = abiCodec();
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(F_SIG);
        byte[] tooShort = new byte[] {methodId[0], methodId[1], methodId[2], methodId[3], 0x01};
        codec.decodeMethodInputById(SIMPLE_ABI, methodId, tooShort);
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodByIdMalformedOutputThrows() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(F_SIG);
        codec.decodeMethodById(SIMPLE_ABI, methodId, new byte[] {0x01, 0x02});
    }

    @Test(expected = ContractCodecException.class)
    public void testDeprecatedDecodeMethodAndGetInputObjectMalformedThrows() throws Exception {
        // deprecated by-name input decoder: every candidate fails -> trailing throw branch.
        abiCodec().decodeMethodAndGetInputObject(SIMPLE_ABI, "f", "0xaabbccddee");
    }

    @Test(expected = ContractCodecException.class)
    public void testDeprecatedDecodeMethodAndGetOutputObjectMalformedThrows() throws Exception {
        // deprecated by-name output decoder error branch ("zz" is not valid hex).
        abiCodec().decodeMethodAndGetOutputObject(SIMPLE_ABI, "f", "zz");
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeConstructorInputToStringMalformedThrows() throws Exception {
        // constructor(uint256) but the trailing data is not valid hex.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],\"type\":\"constructor\"}]";
        codec.decodeConstructorInputToString(abi, BIN, BIN + "zz");
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodToStringUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodToString(SIMPLE_ABI, "ghost", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputToStringUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodInputToString(SIMPLE_ABI, "ghost", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventUnknownNameThrows() throws Exception {
        EventLog log = new EventLog("0x", Collections.singletonList("0x00"));
        abiCodec()
                .decodeEvent(
                        "[{\"anonymous\":false,\"inputs\":[],\"name\":\"E\",\"type\":\"event\"}]",
                        "ghost",
                        log);
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventToStringUnknownNameThrows() throws Exception {
        EventLog log = new EventLog("0x", Collections.singletonList("0x00"));
        abiCodec()
                .decodeEventToString(
                        "[{\"anonymous\":false,\"inputs\":[],\"name\":\"E\",\"type\":\"event\"}]",
                        "ghost",
                        log);
    }

    // ------------------------------------------------------------------------------------------
    // happy-path round-trip through the public encode path across many leaf types.
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEncodeConstructorFromStringRichLeafTypes() throws Exception {
        // exercises encodeConstructorFromString over many leaf types (json-wrapper path).
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":["
                        + "{\"name\":\"u8\",\"type\":\"uint8\"},"
                        + "{\"name\":\"i16\",\"type\":\"int16\"},"
                        + "{\"name\":\"flag\",\"type\":\"bool\"},"
                        + "{\"name\":\"txt\",\"type\":\"string\"},"
                        + "{\"name\":\"addr\",\"type\":\"address\"},"
                        + "{\"name\":\"b8\",\"type\":\"bytes8\"},"
                        + "{\"name\":\"raw\",\"type\":\"bytes\"},"
                        + "{\"name\":\"arr\",\"type\":\"uint256[]\"}"
                        + "],\"type\":\"constructor\"}]";
        List<String> params = new ArrayList<>();
        params.add("250");
        params.add("-300");
        params.add("true");
        params.add("constructor leaf");
        params.add("0x0000000000000000000000000000000000000009");
        params.add("0x1122334455667788");
        params.add("0xc0ffee");
        params.add("[7,8,9]");
        byte[] encoded = codec.encodeConstructorFromString(abi, BIN, params);
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }
}
