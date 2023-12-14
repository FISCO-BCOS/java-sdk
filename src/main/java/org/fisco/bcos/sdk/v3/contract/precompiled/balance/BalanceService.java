package org.fisco.bcos.sdk.v3.contract.precompiled.balance;

import java.awt.*;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class BalanceService {
    private final BalancePrecompiled balancePrecompiled;
    private EnumNodeVersion.Version currentVersion;
    private final Client client;

    public BalanceService(Client client, CryptoKeyPair credential) {
        this.balancePrecompiled =
                BalancePrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.BALANCE_PRECOMPILED_NAME
                                : PrecompiledAddress.BALANCE_PRECOMPILED_ADDRESS,
                        client,
                        credential);
        this.currentVersion = client.getChainCompatibilityVersion();
        this.client = client;
    }

    public EnumNodeVersion.Version getCurrentVersion() {
        return currentVersion;
    }

    public BalancePrecompiled getBalancePrecompiled() {
        return balancePrecompiled;
    }

    public Tuple2<BigInteger, String> getBalance(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        BigInteger balance = balancePrecompiled.getBalance(address);
        if (balance != null) {
            String result = "success";
            return new Tuple2<>(balance, result);
        } else {
            String result = "Please check the address whether exist or has balance.";
            return new Tuple2<>(BigInteger.ZERO, result);
        }
    }

    public RetCode addBalance(String address, BigInteger amount) throws ContractException {
        TransactionReceipt transactionReceipt = balancePrecompiled.addBalance(address, amount);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> balancePrecompiled.getAddBalanceInput(tr).getValue2());
    }

    public RetCode subBalance(String address, BigInteger amount) throws ContractException {
        TransactionReceipt transactionReceipt = balancePrecompiled.subBalance(address, amount);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> balancePrecompiled.getSubBalanceInput(tr).getValue2());
    }

    public RetCode registerCaller(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt receipt = balancePrecompiled.registerCaller(address);
        if (receipt.isStatusOK()) {
            RetCode retCode = PrecompiledRetCode.CODE_SUCCESS;
            return retCode;
        } else {
            if (receipt.getMessage() == "caller already exist") {
                RetCode retCode = PrecompiledRetCode.CODE_CALLER_ALREADY_REGISTERED;
                return retCode;
            }
            RetCode retCode = PrecompiledRetCode.CODE_REGISTER_CALLER_FAILED;
            return retCode;
        }
    }

    public RetCode unregisterCaller(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt receipt = balancePrecompiled.unregisterCaller(address);
        if (receipt.isStatusOK()) {
            RetCode retCode = PrecompiledRetCode.CODE_SUCCESS;
            return retCode;
        } else {
            RetCode retCode = PrecompiledRetCode.CODE_UNREGISTER_CALLER_FAILED;
            return retCode;
        }
    }
}
