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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.transaction.model.bo.AbiInfo;
import org.fisco.bcos.sdk.v3.transaction.model.bo.BinInfo;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.tools.ContractLoader;
import org.junit.Test;

/**
 * Pure-Java unit-test coverage for {@link ContractLoader}. Exercises the single-contract constructor
 * (name, abi, bin), append/load helpers, the directory-based constructor (abiFilePath,
 * binaryFilePath) against the bundled ecdsa test resources, the getters, and the static
 * selectConstructor helper. No live node / client required.
 */
public class ContractLoaderUnitCoverageTest {

    private static final String HELLO_NAME = "HelloWorld";

    /** A small inline ABI with a constructor + one function so we avoid file dependencies. */
    private static final String HELLO_ABI =
            "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
                    + "{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},"
                    + "{\"inputs\":[{\"internalType\":\"string\",\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    /** ABI with no explicit constructor declared. */
    private static final String NO_CTOR_ABI =
            "[{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";

    private static final String HELLO_BIN = "608060405234801561001057600080fd5b50";

    // -------------------------------------------------------------------------
    // single-contract constructor + getters
    // -------------------------------------------------------------------------

    @Test
    public void testSingleContractConstructorAndGetters() throws Exception {
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);

        assertEquals(HELLO_ABI, loader.getABIByContractName(HELLO_NAME));
        assertEquals(HELLO_BIN, loader.getBinaryByContractName(HELLO_NAME));

        Pair<String, String> abiAndBin = loader.getABIAndBinaryByContractName(HELLO_NAME);
        assertEquals(HELLO_ABI, abiAndBin.getLeft());
        assertEquals(HELLO_BIN, abiAndBin.getRight());

        List<ABIDefinition> funcs = loader.getFunctionABIListByContractName(HELLO_NAME);
        assertNotNull(funcs);
        // constructor + get + set
        assertEquals(3, funcs.size());

        ABIDefinition constructor = loader.getConstructorABIByContractName(HELLO_NAME);
        assertNotNull(constructor);
        assertEquals("constructor", constructor.getType());
    }

    @Test
    public void testConstructorWithoutDeclaredConstructorReturnsNull() throws Exception {
        ContractLoader loader = new ContractLoader("NoCtor", NO_CTOR_ABI, HELLO_BIN);
        ABIDefinition constructor = loader.getConstructorABIByContractName("NoCtor");
        assertNull(constructor);
        // function list is still populated
        assertEquals(1, loader.getFunctionABIListByContractName("NoCtor").size());
    }

    // -------------------------------------------------------------------------
    // append / load helpers
    // -------------------------------------------------------------------------

    @Test
    public void testAppendContractAbiAndBinary() throws Exception {
        ContractLoader loader = new ContractLoader("First", HELLO_ABI, HELLO_BIN);

        // new contract -> both appends succeed
        assertTrue(loader.appendContractBinary("Second", HELLO_BIN));
        assertTrue(loader.appendContractAbi("Second", HELLO_ABI));
        assertEquals(HELLO_ABI, loader.getABIByContractName("Second"));
        assertEquals(HELLO_BIN, loader.getBinaryByContractName("Second"));
    }

    @Test
    public void testAppendDuplicateReturnsFalse() {
        ContractLoader loader = new ContractLoader("Dup", HELLO_ABI, HELLO_BIN);
        // abi already loaded for "Dup" -> appending abi again fails
        assertFalse(loader.appendContractAbi("Dup", HELLO_ABI));
        // binary append fails too because abiMap already has the entry (guard in loadBinary)
        assertFalse(loader.appendContractBinary("Dup", HELLO_BIN));
    }

    @Test
    public void testAppendEmptyOrNullBinaryReturnsFalse() {
        ContractLoader loader = new ContractLoader("OnlyAbi", NO_CTOR_ABI, null);
        // null bin in the constructor was simply skipped, no binary entry created.
        // appending null / empty binaries should not succeed.
        assertFalse(loader.appendContractBinary("BrandNew", null));
        assertFalse(loader.appendContractBinary("BrandNew", ""));
    }

    // -------------------------------------------------------------------------
    // not-found error paths
    // -------------------------------------------------------------------------

    @Test
    public void testGetAbiUnknownContractThrows() {
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);
        try {
            loader.getABIByContractName("Missing");
            fail("expected NoSuchTransactionFileException");
        } catch (NoSuchTransactionFileException expected) {
            // ok
        }
    }

    @Test
    public void testGetBinaryUnknownContractThrows() {
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);
        try {
            loader.getBinaryByContractName("Missing");
            fail("expected NoSuchTransactionFileException");
        } catch (NoSuchTransactionFileException expected) {
            // ok
        }
    }

    @Test
    public void testGetAbiAndBinaryMissingAbiThrows() {
        // Only binary present, abi missing -> getABIAndBinaryByContractName throws.
        ContractLoader loader = new ContractLoader("BinOnly", NO_CTOR_ABI, HELLO_BIN);
        loader.appendContractBinary("BinOnlyContract", HELLO_BIN);
        try {
            loader.getABIAndBinaryByContractName("BinOnlyContract");
            fail("expected NoSuchTransactionFileException for missing abi");
        } catch (NoSuchTransactionFileException expected) {
            // ok
        }
    }

    @Test
    public void testGetFunctionListUnknownContractThrows() {
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);
        try {
            loader.getFunctionABIListByContractName("Missing");
            fail("expected NoSuchTransactionFileException");
        } catch (NoSuchTransactionFileException expected) {
            // ok
        }
    }

    // -------------------------------------------------------------------------
    // static selectConstructor
    // -------------------------------------------------------------------------

    @Test
    public void testSelectConstructorStatic() {
        List<ABIDefinition> abiList = new ArrayList<>();
        ABIDefinition function =
                new ABIDefinition("foo", "function", false, false, false, "nonpayable");
        ABIDefinition constructor =
                new ABIDefinition(null, "constructor", false, false, false, "nonpayable");
        abiList.add(function);
        abiList.add(constructor);

        ABIDefinition selected = ContractLoader.selectConstructor(abiList);
        assertNotNull(selected);
        assertEquals("constructor", selected.getType());
    }

    @Test
    public void testSelectConstructorStaticNoneFound() {
        List<ABIDefinition> abiList = new ArrayList<>();
        abiList.add(new ABIDefinition("foo", "function", false, false, false, "nonpayable"));
        assertNull(ContractLoader.selectConstructor(abiList));
    }

    // -------------------------------------------------------------------------
    // directory-based constructor + abiInfo / binInfo against bundled resources
    // -------------------------------------------------------------------------

    @Test
    public void testDirectoryConstructorWithBundledResources() throws Exception {
        String abiDir = resourcePath("ecdsa/abi");
        String binDir = resourcePath("ecdsa/bin");

        ContractLoader loader = new ContractLoader(abiDir, binDir);
        // HelloWorld exists in both dirs.
        assertNotNull(loader.getABIByContractName(HELLO_NAME));
        assertNotNull(loader.getBinaryByContractName(HELLO_NAME));
        assertFalse(loader.getFunctionABIListByContractName(HELLO_NAME).isEmpty());
    }

    @Test
    public void testBinInfoAndAbiInfoReturnMaps() throws Exception {
        String abiDir = resourcePath("ecdsa/abi");
        String binDir = resourcePath("ecdsa/bin");

        ContractLoader loader = new ContractLoader(abiDir, binDir);

        BinInfo binInfo = loader.binInfo(binDir);
        assertNotNull(binInfo.getBin(HELLO_NAME));

        AbiInfo abiInfo = loader.abiInfo(abiDir);
        assertNotNull(abiInfo.findFuncAbis(HELLO_NAME));
    }

    @Test
    public void testBinInfoEmptyPathReturnsEmpty() throws Exception {
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);
        BinInfo binInfo = loader.binInfo("");
        assertNotNull(binInfo);
        assertNull(binInfo.getBin("anything"));
    }

    @Test
    public void testBinInfoNoBinFilesReturnsEmpty() throws Exception {
        // point at the abi directory which holds *.abi (not *.bin/*.wasm) -> empty BinInfo
        String abiDir = resourcePath("ecdsa/abi");
        ContractLoader loader = new ContractLoader(HELLO_NAME, HELLO_ABI, HELLO_BIN);
        BinInfo binInfo = loader.binInfo(abiDir);
        assertNotNull(binInfo);
        assertNull(binInfo.getBin(HELLO_NAME));
    }

    private String resourcePath(String name) throws IOException {
        java.net.URL url = getClass().getClassLoader().getResource(name);
        assertNotNull("missing test resource: " + name, url);
        return url.getPath();
    }
}
