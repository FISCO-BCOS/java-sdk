package org.fisco.bcos.sdk.v3.codec.wrapper;

import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABIDefinitionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ABIDefinitionFactory.class);

    @Deprecated private CryptoSuite cryptoSuite;
    private Hash hashIpml;

    @Deprecated
    public ABIDefinitionFactory(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
        this.hashIpml = cryptoSuite.getHashImpl();
    }

    public ABIDefinitionFactory(Hash hashImpl) {
        this.hashIpml = hashImpl;
    }

    /**
     * load ABI and construct ContractABIDefinition.
     *
     * @param abi the abi need to be loaded
     * @return the contract definition
     */
    public ContractABIDefinition loadABI(String abi) {
        try {
            ABIDefinition[] abiDefinitions =
                    ObjectMapperFactory.getObjectMapper().readValue(abi, ABIDefinition[].class);

            ContractABIDefinition contractABIDefinition = new ContractABIDefinition(hashIpml);
            for (ABIDefinition abiDefinition : abiDefinitions) {
                if (abiDefinition.getType().equals("constructor")) {
                    contractABIDefinition.setConstructor(abiDefinition);
                } else if (abiDefinition.getType().equals("function")) {
                    contractABIDefinition.addFunction(abiDefinition.getName(), abiDefinition);
                } else if (abiDefinition.getType().equals("event")) {
                    contractABIDefinition.addEvent(abiDefinition.getName(), abiDefinition);
                } else {
                    // skip and do nothing
                }
            }
            if (contractABIDefinition.getConstructor() == null) {
                contractABIDefinition.setConstructor(
                        ABIDefinition.createDefaultConstructorABIDefinition());
            }
            if (logger.isTraceEnabled()) {
                logger.trace(" contractABIDefinition {} ", contractABIDefinition);
            }

            return contractABIDefinition;

        } catch (Exception e) {
            logger.error(" e: ", e);
            return null;
        }
    }
}
