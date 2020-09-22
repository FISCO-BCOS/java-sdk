package org.fisco.bcos.sdk.abi.wrapper;

import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABIDefinitionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ABIDefinitionFactory.class);

    private CryptoInterface cryptoInterface;

    public ABIDefinitionFactory(CryptoInterface cryptoInterface) {
        this.cryptoInterface = cryptoInterface;
    }

    /**
     * load ABI and construct ContractABIDefinition.
     *
     * @param abi
     * @return the contract definition
     */
    public ContractABIDefinition loadABI(String abi) {
        try {
            ABIDefinition[] abiDefinitions =
                    ObjectMapperFactory.getObjectMapper().readValue(abi, ABIDefinition[].class);

            ContractABIDefinition contractABIDefinition =
                    new ContractABIDefinition(cryptoInterface);
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

                if (logger.isInfoEnabled()) {
                    logger.info(" abiDefinition: {}", abiDefinition);
                }
            }
            if (contractABIDefinition.getConstructor() == null) {
                contractABIDefinition.setConstructor(
                        ABIDefinition.createDefaultConstructorABIDefinition());
            }
            logger.info(" contractABIDefinition {} ", contractABIDefinition);

            return contractABIDefinition;

        } catch (Exception e) {
            logger.error(" e: ", e);
            return null;
        }
    }
}
