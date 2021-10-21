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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.codegen.exceptions.CodeGenException;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

public final class CodeGenUtils {
    public static String parsePositionalArg(String[] args, int idx) {
        if (args != null && args.length > idx) {
            return args[idx];
        } else {
            return "";
        }
    }

    public static String parseParameterArgument(String[] args, String... parameters) {
        for (String parameter : parameters) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals(parameter) && i + 1 < args.length) {
                    String parameterValue = args[i + 1];
                    if (!parameterValue.startsWith("-")) {
                        return parameterValue;
                    }
                }
            }
        }
        return "";
    }

    public static String getFileNameNoExtension(String fileName) {
        String[] splitName = fileName.split("\\.(?=[^.]*$)");
        return splitName[0];
    }

    // load abi from the abi file
    public static List<ABIDefinition> loadContractAbiDefinition(File abiFile)
            throws CodeGenException {
        try {
            return ObjectMapperFactory.getObjectMapper()
                    .readValue(abiFile, new TypeReference<List<ABIDefinition>>() {});
        } catch (IOException e) {
            throw new CodeGenException(
                    "loadContractAbiDefinition for "
                            + abiFile.getName().toString()
                            + " failed, error info: "
                            + e.getLocalizedMessage(),
                    e);
        }
    }

    public static List<ABIDefinition> loadContractAbiDefinition(String abi) throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        ABIDefinition[] abiDefinition = objectMapper.readValue(abi, ABIDefinition[].class);
        return Arrays.asList(abiDefinition);
    }

    public static byte[] readBytes(File file) throws CodeGenException, IOException {
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
        } catch (IOException e) {
            throw new CodeGenException(
                    "read data from " + file + " failed, error information: " + e.getMessage(), e);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return bytes;
    }

    public static void exitError(Throwable throwable) {
        System.err.println(throwable.getMessage());
        System.exit(1);
    }
}
