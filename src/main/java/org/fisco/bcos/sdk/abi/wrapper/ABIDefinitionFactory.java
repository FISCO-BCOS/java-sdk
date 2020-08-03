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
     * @return
     */
    public ContractABIDefinition loadABI(String abi) {
        try {
            ABIDefinition[] abiDefinitions =
                    ObjectMapperFactory.getObjectMapper().readValue(abi, ABIDefinition[].class);

            ContractABIDefinition contractABIDefinition =
                    new ContractABIDefinition(cryptoInterface);
            for (ABIDefinition ABIDefinition : abiDefinitions) {
                if (ABIDefinition.getType().equals("constructor")) {
                    contractABIDefinition.setConstructor(ABIDefinition);
                } else if (ABIDefinition.getType().equals("function")) {
                    contractABIDefinition.addFunction(ABIDefinition.getName(), ABIDefinition);
                } else if (ABIDefinition.getType().equals("event")) {
                    contractABIDefinition.addEvent(ABIDefinition.getName(), ABIDefinition);
                } else {
                    // skip and do nothing
                }

                if (logger.isInfoEnabled()) {
                    logger.info(" abiDefinitions: {}", ABIDefinition);
                }
            }

            logger.info(" contractABIDefinition {} ", contractABIDefinition);

            return contractABIDefinition;

        } catch (Exception e) {
            logger.error(" e: ", e);
            return null;
        }
    }
}
