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
package org.fisco.bcos.sdk.v3.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.codegen.exceptions.CodeGenException;

/** Java wrapper source code generator for Solidity ABI format. */
public class ContractGenerator {
    public static final String COMMAND_SOLIDITY = "solidity";
    public static final String COMMAND_GENERATE = "generate";
    public static final String COMMAND_PREFIX = COMMAND_SOLIDITY + " " + COMMAND_GENERATE;

    /*
     * Usage: solidity generate [-hV] [-jt] [-st] -a=<abiFile> [-b=<binFile>]
     * -o=<destinationFileDir> -p=<packageName>
     * -h, --help                 Show this help message and exit.
     * -V, --version              Print version information and exit.
     * -a, --abiFile=<abiFile>    abi file with contract definition.
     * -b, --binFile=<binFile>    bin file with contract compiled code in order to
     * generate deploy methods.
     * -s, --smBinFile=<binFile>  sm bin file with contract compiled code in order to
     * generate deploy methods.
     * -o, --outputDir=<destinationFileDir>
     * destination base directory.
     * -p, --package=<packageName>
     * base package name.
     * -jt, --javaTypes       use native java types.
     * Default: true
     * -st, --solidityTypes   use solidity types.
     */

    private final File binFile;
    private final File smBinFile;
    private final File abiFile;
    private final File destinationDir;
    private String basePackageName;

    public ContractGenerator(
            File binFile,
            File smBinFile,
            File abiFile,
            File destinationDir,
            String basePackageName) {
        this.binFile = binFile;
        this.smBinFile = smBinFile;
        this.abiFile = abiFile;
        this.destinationDir = destinationDir;
        this.basePackageName = basePackageName;
    }

    public void generateJavaFiles() throws CodeGenException, IOException, ClassNotFoundException {
        // get binary
        byte[] binary = CodeGenUtils.readBytes(this.binFile);
        // get binary for sm
        byte[] smBinary = CodeGenUtils.readBytes(this.smBinFile);
        // load abi
        byte[] abiBytes = CodeGenUtils.readBytes(this.abiFile);
        // get contractName
        String contractName = CodeGenUtils.getFileNameNoExtension(this.abiFile.getName());
        // differentiate type of contract
        byte[] prefix = Arrays.copyOf(binary, 4);
        byte[] wasmMagic = {0, 'a', 's', 'm'};
        boolean isWasm = Arrays.equals(prefix, wasmMagic);
        if (isWasm) {
            binary = calculateWasmBytes(binary);
            smBinary = calculateWasmBytes(smBinary);
        }
        new ContractWrapper(isWasm)
                .generateJavaFiles(
                        contractName,
                        new String(binary),
                        new String(smBinary),
                        new String(abiBytes),
                        destinationDir.toString(),
                        basePackageName);
    }

    private byte[] calculateWasmBytes(byte[] binary) throws IOException {
        ByteArrayOutputStream builder = new ByteArrayOutputStream();
        for (byte b : binary) {
            int singleByte = b & 0xff;
            byte[] bytes = Integer.toHexString(singleByte).getBytes(StandardCharsets.UTF_8);
            if (bytes.length == 1) {
                builder.write(Integer.toHexString((byte) 0).getBytes(StandardCharsets.UTF_8));
            }
            builder.write(bytes);
        }
        return builder.toByteArray();
    }
}
