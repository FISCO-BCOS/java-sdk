package org.fisco.bcos.sdk.transaction.model.bo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;

public class AbiInfo {

    private Map<String, List<ABIDefinition>> contractFuncAbis;
    private Map<String, ABIDefinition> contractConstructAbi;

    /**
     * @param contractFuncAbis
     * @param contractConstructAbi
     */
    public AbiInfo(
            Map<String, List<ABIDefinition>> contractFuncAbis,
            Map<String, ABIDefinition> contractConstructAbi) {
        super();
        this.contractFuncAbis = contractFuncAbis;
        this.contractConstructAbi = contractConstructAbi;
    }

    public List<ABIDefinition> findFuncAbis(String contractName) {
        List<ABIDefinition> abis = contractFuncAbis.get(contractName);
        if (abis == null) {
            throw new RuntimeException("No such contract abi " + contractName);
        }
        return Collections.unmodifiableList(abis);
    }

    public ABIDefinition findConstructor(String contractName) {
        return contractConstructAbi.get(contractName);
    }
}
