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
                switch (abiDefinition.getType()) {
                    case ABIDefinition.CONSTRUCTOR_TYPE:
                        contractABIDefinition.setConstructor(abiDefinition);
                        break;
                    case ABIDefinition.FUNCTION_TYPE:
                        contractABIDefinition.addFunction(abiDefinition.getName(), abiDefinition);
                        break;
                    case ABIDefinition.EVENT_TYPE:
                        contractABIDefinition.addEvent(abiDefinition.getName(), abiDefinition);
                        break;
                    case ABIDefinition.FALLBACK_TYPE:
                        if (contractABIDefinition.hasFallbackFunction()) {
                            throw new ABICodecException("only single fallback is allowed");
                        }
                        contractABIDefinition.setFallbackFunction(abiDefinition);
                        break;
                    case ABIDefinition.RECEIVE_TYPE:
                        if (contractABIDefinition.hasReceiveFunction()) {
                            throw new ABICodecException("only single receive is allowed");
                        }
                        if (!"payable".equals(abiDefinition.getStateMutability())
                                && !abiDefinition.isPayable()) {
                            throw new ABICodecException(
                                    "the statemutability of receive can only be payable");
                        }
                        contractABIDefinition.setReceiveFunction(abiDefinition);
                        break;
                    default:
                        // skip and do nothing
                        break;
                }

                if (logger.isTraceEnabled()) {
                    logger.trace(" abiDefinition: {}", abiDefinition);
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
