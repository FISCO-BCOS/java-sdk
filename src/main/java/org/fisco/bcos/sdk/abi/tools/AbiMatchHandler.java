package org.fisco.bcos.sdk.abi.tools;

import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.transaction.model.CommonConstant;

public class AbiMatchHandler {

    public static Stream<ABIDefinition> matchPossibleDefinitions(
            List<ABIDefinition> ABIDefinitions, String functionName, List<Object> args) {
        return ABIDefinitions.stream()
                .filter(abi -> matchByArgLength(abi, args.size()))
                .filter(abi -> matchByFuncName(abi, functionName))
                .filter(abi -> ensureAbiType(abi));
    }

    private static boolean ensureAbiType(ABIDefinition ABIDefinition) {
        return ABIDefinition.getType().equals(CommonConstant.ABI_FUNCTION);
    }

    private static boolean matchByArgLength(ABIDefinition ABIDefinition, int expectedLength) {
        return ABIDefinition.getInputs().size() == expectedLength;
    }

    private static boolean matchByFuncName(ABIDefinition ABIDefinition, String expectedName) {
        return StringUtils.equals(ABIDefinition.getName(), expectedName);
    }
}
