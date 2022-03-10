package org.fisco.bcos.sdk.contract.precompiled.gaschargemgr;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;

public class GasChargeManageService {
    private final GasChargeManagePrecompiled gasChargeManagePrecompiled;
    private final TransactionDecoderInterface transactionDecoder;

    public GasChargeManageService(Client client, CryptoKeyPair cryptoKeyPair)
            throws ContractException {
        PrecompiledVersionCheck.GAS_CHARGE_MANAGE_PRECOMPILED_VERSION.checkVersion(
                client.getClientNodeVersion().getNodeVersion().getSupportedVersion());
        this.gasChargeManagePrecompiled =
                GasChargeManagePrecompiled.load(
                        PrecompiledAddress.GAS_CHARGE_MANAGE_PRECOMPILED_ADDRESS,
                        client,
                        cryptoKeyPair);
        this.transactionDecoder = new TransactionDecoderService(client.getCryptoSuite());
    }

    public TransactionResponse decodeReceipt(
            TransactionReceipt receipt, String functionName, String errorMessage)
            throws ContractException {
        try {
            TransactionResponse transactionResponse =
                    transactionDecoder.decodeReceiptWithValues(
                            gasChargeManagePrecompiled.ABI, functionName, receipt);
            if (transactionResponse.getReturnObject() == null
                    || transactionResponse.getReturnObject().isEmpty()) {
                return transactionResponse;
            }
            List<Object> returnObject = transactionResponse.getReturnObject();
            BigInteger returnCode = (BigInteger) (returnObject.get(0));
            transactionResponse.setReturnCode(returnCode.intValue());
            if (returnCode.intValue() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {

                return transactionResponse;
            }
            RetCode retCode =
                    PrecompiledRetCode.getPrecompiledResponse(
                            returnCode.intValue(), receipt.getMessage());
            transactionResponse.setReturnMessage(retCode.getMessage());
            return transactionResponse;
        } catch (TransactionException | ABICodecException | IOException e) {
            throw new ContractException(errorMessage, e);
        }
    }

    public TransactionResponse charge(String userAccount, BigInteger gasValue)
            throws ContractException {
        return decodeReceipt(
                gasChargeManagePrecompiled.charge(userAccount, gasValue),
                gasChargeManagePrecompiled.FUNC_CHARGE,
                "GasChargeManageService: failed to call charge");
    }

    public TransactionResponse deduct(String userAccount, BigInteger gasValue)
            throws ContractException {
        return decodeReceipt(
                gasChargeManagePrecompiled.deduct(userAccount, gasValue),
                gasChargeManagePrecompiled.FUNC_DEDUCT,
                "GasChargeManageService: failed to call deduct");
    }

    public BigInteger queryRemainGas(String userAccount) throws ContractException {
        Tuple2<BigInteger, BigInteger> result =
                this.gasChargeManagePrecompiled.queryRemainGas(userAccount);
        if (result.getValue1().intValue() != PrecompiledRetCode.CODE_SUCCESS.getCode()) {
            RetCode retCode =
                    PrecompiledRetCode.getPrecompiledResponse(result.getValue1().intValue(), "");
            throw new ContractException(retCode.getMessage(), retCode.getCode());
        }
        return result.getValue2();
    }

    public RetCode grantCharger(String chargerAccount) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                gasChargeManagePrecompiled.grantCharger(chargerAccount));
    }

    public RetCode revokeCharger(String chargerAccount) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                gasChargeManagePrecompiled.revokeCharger(chargerAccount));
    }

    public List<String> listChargers() throws ContractException {
        try {
            List<String> chargerList = new ArrayList<>();
            chargerList = this.gasChargeManagePrecompiled.listChargers();
            return chargerList;
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }
}
