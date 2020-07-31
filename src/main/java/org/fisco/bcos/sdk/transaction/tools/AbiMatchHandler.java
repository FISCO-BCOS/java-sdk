package org.fisco.bcos.sdk.transaction.tools;

import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.transaction.model.CommonConstant;

public class AbiMatchHandler {

    public static Stream<AbiDefinition> matchPossibleDefinitions(
            List<AbiDefinition> abiDefinitions, String functionName, List<Object> args) {
        return abiDefinitions
                .stream()
                .filter(abi -> matchByArgLength(abi, args.size()))
                .filter(abi -> matchByFuncName(abi, functionName))
                .filter(abi -> ensureAbiType(abi));
    }

    private static boolean ensureAbiType(AbiDefinition abiDefinition) {
        return abiDefinition.getType().equals(CommonConstant.ABI_FUNCTION);
    }

    private static boolean matchByArgLength(AbiDefinition abiDefinition, int expectedLength) {
        return abiDefinition.getInputs().size() == expectedLength;
    }

    private static boolean matchByFuncName(AbiDefinition abiDefinition, String expectedName) {
        return StringUtils.equals(abiDefinition.getName(), expectedName);
    }
}
