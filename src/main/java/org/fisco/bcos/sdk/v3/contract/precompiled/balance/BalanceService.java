package org.fisco.bcos.sdk.v3.contract.precompiled.balance;

import java.awt.*;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
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

    public BigInteger getBalance(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        BigInteger balance = balancePrecompiled.getBalance(address);
        return balance;
    }

    public RetCode addBalance(String address, BigInteger amount) throws ContractException {
        TransactionReceipt transactionReceipt = balancePrecompiled.addBalance(address, amount);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        if (transactionReceipt.isStatusOK()) {
            return PrecompiledRetCode.CODE_SUCCESS;
        } else {
            return PrecompiledRetCode.CODE_ADD_BALANCE_FAILED;
        }
    }

    public RetCode subBalance(String address, BigInteger amount) throws ContractException {
        TransactionReceipt transactionReceipt = balancePrecompiled.subBalance(address, amount);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        if (transactionReceipt.isStatusOK()) {
            return PrecompiledRetCode.CODE_SUCCESS;
        } else {
            return PrecompiledRetCode.CODE_SUB_BALANCE_FAILED;
        }
    }

    public RetCode registerCaller(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt receipt = balancePrecompiled.registerCaller(address);
        if (receipt.isStatusOK()) {
            RetCode retCode = PrecompiledRetCode.CODE_SUCCESS;
            return retCode;
        } else {
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
