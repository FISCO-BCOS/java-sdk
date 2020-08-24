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
package org.fisco.bcos.sdk.demo.codegen;

import static org.fisco.solc.compiler.SolidityCompiler.Options.ABI;
import static org.fisco.solc.compiler.SolidityCompiler.Options.BIN;
import static org.fisco.solc.compiler.SolidityCompiler.Options.INTERFACE;
import static org.fisco.solc.compiler.SolidityCompiler.Options.METADATA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.fisco.bcos.sdk.codegen.CodeGenMain;
import org.fisco.solc.compiler.CompilationResult;
import org.fisco.solc.compiler.SolidityCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoSolcToJava {

    private static final Logger logger = LoggerFactory.getLogger(DemoSolcToJava.class);

    public static final String SOLIDITY_PATH = "contracts/solidity/";
    public static final String JAVA_PATH = "contracts/sdk/java/";
    public static final String ABI_PATH = "contracts/sdk/abi/";
    public static final String BIN_PATH = "contracts/sdk/bin/";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a package name.");
            return;
        }

        File solFileList = new File(SOLIDITY_PATH);
        File javaPath = new File(JAVA_PATH);
        if (!javaPath.exists()) {
            javaPath.mkdirs();
        }
        File abiPath = new File(ABI_PATH + File.separator + "sm");
        if (!abiPath.exists()) {
            abiPath.mkdirs();
        }
        File binPath = new File(BIN_PATH + File.separator + "sm");
        if (!binPath.exists()) {
            binPath.mkdirs();
        }
        String tempDirPath = javaPath.getAbsolutePath();
        try {
            compileSolToJava("*", tempDirPath, args[0], solFileList, ABI_PATH, BIN_PATH);
            System.out.println(
                    "\nCompile solidity contract files to java contract files successfully!");
        } catch (IOException e) {
            System.out.print(e.getMessage());
            logger.error(" message: {}, e: {}", e.getMessage(), e);
        }
    }

    private static void writeStringToFile(String destFile, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(destFile);
        fos.write(content.getBytes());
        fos.close();
    }

    public static void compileSolToJava(
            String solName,
            String tempDirPath,
            String packageName,
            File solFileList,
            String abiDir,
            String binDir)
            throws IOException {
        File[] solFiles = solFileList.listFiles();
        if (solFiles.length == 0) {
            System.out.println("The contracts directory is empty.");
            return;
        }
        for (File solFile : solFiles) {
            if (!solFile.getName().endsWith(".sol")) {
                continue;
            }
            if (!"*".equals(solName)) {
                if (!solFile.getName().equals(solName)) {
                    continue;
                }
                if (solFile.getName().startsWith("Lib")) {
                    throw new IOException("Don't deploy the library: " + solFile.getName());
                }
            } else {
                if (solFile.getName().startsWith("Lib")) {
                    continue;
                }
            }

            String contractName = solFile.getName().split("\\.")[0];

            /** ecdsa compile */
            SolidityCompiler.Result res =
                    SolidityCompiler.compile(solFile, false, true, ABI, BIN, INTERFACE, METADATA);
            logger.debug(
                    " solidity compiler result, success: {}, output: {}, error: {}",
                    !res.isFailed(),
                    res.getOutput(),
                    res.getErrors());
            if (res.isFailed() || "".equals(res.getOutput())) {
                throw new CompileSolidityException(" Compile error: " + res.getErrors());
            }

            /** sm compile */
            SolidityCompiler.Result smRes =
                    SolidityCompiler.compile(solFile, true, true, ABI, BIN, INTERFACE, METADATA);
            logger.debug(
                    " sm solidity compiler result, success: {}, output: {}, error: {}",
                    !smRes.isFailed(),
                    smRes.getOutput(),
                    smRes.getErrors());
            if (smRes.isFailed() || "".equals(smRes.getOutput())) {
                throw new CompileSolidityException(" Compile SM error: " + res.getErrors());
            }

            CompilationResult result = CompilationResult.parse(res.getOutput());
            CompilationResult smResult = CompilationResult.parse(smRes.getOutput());

            CompilationResult.ContractMetadata meta = result.getContract(contractName);
            CompilationResult.ContractMetadata smMeta = smResult.getContract(contractName);

            writeStringToFile(new File(abiDir + contractName + ".abi").getAbsolutePath(), meta.abi);
            writeStringToFile(new File(binDir + contractName + ".bin").getAbsolutePath(), meta.bin);

            writeStringToFile(
                    new File(abiDir + "/sm/" + contractName + ".abi").getAbsolutePath(),
                    smMeta.abi);
            writeStringToFile(
                    new File(binDir + "/sm/" + contractName + ".bin").getAbsolutePath(),
                    smMeta.bin);

            String binFile;
            String abiFile;
            String smBinFile;
            String filename = contractName;
            abiFile = abiDir + filename + ".abi";
            binFile = binDir + filename + ".bin";
            smBinFile = binDir + "/sm/" + filename + ".bin";
            CodeGenMain.main(
                    Arrays.asList(
                                    "-a", abiFile,
                                    "-b", binFile,
                                    "-s", smBinFile,
                                    "-p", packageName,
                                    "-o", tempDirPath)
                            .toArray(new String[0]));
        }
    }
}
