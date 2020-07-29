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
package org.fisco.bcos.sdk.codegen;

import static org.fisco.bcos.sdk.utils.Collection.tail;

import java.io.File;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public class CodeGenMain {
    public static final String COMMAND_SOLIDITY = "solidity";
    public static final String COMMAND_GENERATE = "generate";
    public static final String COMMAND_PREFIX = COMMAND_SOLIDITY + " " + COMMAND_GENERATE;

    public static void main(String[] args) {
        if (args.length > 0
                && (args[0].equals(COMMAND_SOLIDITY) || args[0].equals(COMMAND_GENERATE))) {
            args = tail(args);
        }
        CommandLine.run(new PicocliRunner(), args);
    }

    @Command(
            name = COMMAND_PREFIX,
            mixinStandardHelpOptions = true,
            version = "4.0",
            sortOptions = false)
    static class PicocliRunner implements Runnable {
        @Option(
                names = {"-a", "--abiFile"},
                description = "abi file with contract definition.",
                required = true)
        private File abiFile;

        @Option(
                names = {"-b", "--binFile"},
                description =
                        "bin file with contract compiled code "
                                + "in order to generate deploy methods.",
                required = true)
        private File binFile;

        @Option(
                names = {"-s", "--smBinFile"},
                description =
                        "sm bin file with contract compiled code "
                                + "in order to generate deploy methods.",
                required = true)
        private File smBinFile;

        @Option(
                names = {"-o", "--outputDir"},
                description = "destination base directory.",
                required = true)
        private File destinationFileDir;

        @Option(
                names = {"-p", "--package"},
                description = "base package name.",
                required = true)
        private String packageName;

        @Override
        public void run() {
            try {
                new SolidityContractGenerator(
                                binFile, smBinFile, abiFile, destinationFileDir, packageName)
                        .generateJavaFiles();
            } catch (Exception e) {
                CodeGenUtils.exitError(e);
            }
        }
    }
}
