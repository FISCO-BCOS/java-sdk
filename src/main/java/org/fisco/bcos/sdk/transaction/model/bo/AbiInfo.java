package org.fisco.bcos.sdk.transaction.model.bo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.abi.AbiDefinition;

public class AbiInfo {

    private Map<String, List<AbiDefinition>> contractFuncAbis;
    private Map<String, AbiDefinition> contractConstructAbi;

    /**
     * @param contractFuncAbis
     * @param contractConstructAbi
     */
    public AbiInfo(
            Map<String, List<AbiDefinition>> contractFuncAbis,
            Map<String, AbiDefinition> contractConstructAbi) {
        super();
        this.contractFuncAbis = contractFuncAbis;
        this.contractConstructAbi = contractConstructAbi;
    }

    public List<AbiDefinition> findFuncAbis(String contractName) {
        List<AbiDefinition> abis = contractFuncAbis.get(contractName);
        if (abis == null) {
            throw new RuntimeException("No such contract abi " + contractName);
        }
        return Collections.unmodifiableList(abis);
    }

    public AbiDefinition findConstructor(String contractName) {
        return contractConstructAbi.get(contractName);
    }
}
