package org.fisco.bcos.sdk.abi.wrapper;

import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABIDefinitionFactory {
    private static final Logger logger = LoggerFactory.getLogger(ABIDefinitionFactory.class);

    private CryptoSuite cryptoSuite;

    public ABIDefinitionFactory(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
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

            ContractABIDefinition contractABIDefinition = new ContractABIDefinition(cryptoSuite);
            for (ABIDefinition abiDefinition : abiDefinitions) {
                if (abiDefinition.getType().equals(ABIDefinition.CONSTRUCTOR_TYPE)) {
                    contractABIDefinition.setConstructor(abiDefinition);
                } else if (abiDefinition.getType().equals(ABIDefinition.FUNCTION_TYPE)) {
                    contractABIDefinition.addFunction(abiDefinition.getName(), abiDefinition);
                } else if (abiDefinition.getType().equals(ABIDefinition.EVENT_TYPE)) {
                    contractABIDefinition.addEvent(abiDefinition.getName(), abiDefinition);
                } else if (abiDefinition.getType().equals(ABIDefinition.FALLBACK_TYPE)) {
                    if (contractABIDefinition.hasFallbackFunction()) {
                        throw new ABICodecException("only single fallback is allowed");
                    }
                    contractABIDefinition.setFallbackFunction(abiDefinition);
                } else if (abiDefinition.getType().equals(ABIDefinition.RECEIVE_TYPE)) {
                    if (contractABIDefinition.hasReceiveFunction()) {
                        throw new ABICodecException("only single receive is allowed");
                    }
                    if (abiDefinition.getStateMutability().equals("payable") == false
                            && abiDefinition.isPayable() == false) {
                        throw new ABICodecException(
                                "the statemutability of receive can only be payable");
                    }
                    contractABIDefinition.setReceiveFunction(abiDefinition);
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
