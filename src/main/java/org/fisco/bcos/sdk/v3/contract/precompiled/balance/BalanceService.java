package org.fisco.bcos.sdk.v3.contract.precompiled.balance;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;

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

    public RetCode addBalance(String address, String amount, Convert.Unit unit)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt =
                balancePrecompiled.addBalance(address, weiValue.toBigIntegerExact());
        if (transactionReceipt.isStatusOK()) {
            RetCode codeSuccess = PrecompiledRetCode.CODE_SUCCESS;
            codeSuccess.setTransactionReceipt(transactionReceipt);
            return codeSuccess;
        } else {
            return ReceiptParser.parseTransactionReceipt(transactionReceipt, null);
        }
    }

    public void addBalanceAsync(
            String address, String amount, Convert.Unit unit, PrecompiledCallback callback)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        this.balancePrecompiled.addBalance(
                address, weiValue.toBigIntegerExact(), createTransactionCallback(callback));
    }

    public RetCode subBalance(String address, String amount, Convert.Unit unit)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt =
                balancePrecompiled.subBalance(address, weiValue.toBigIntegerExact());
        if (transactionReceipt.isStatusOK()) {
            RetCode codeSuccess = PrecompiledRetCode.CODE_SUCCESS;
            codeSuccess.setTransactionReceipt(transactionReceipt);
            return codeSuccess;
        } else {
            return ReceiptParser.parseTransactionReceipt(transactionReceipt, null);
        }
    }

    public void subBalanceAsync(
            String address, String amount, Convert.Unit unit, PrecompiledCallback callback)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        this.balancePrecompiled.subBalance(
                address, weiValue.toBigIntegerExact(), createTransactionCallback(callback));
    }

    public RetCode transfer(String from, String to, String amount, Convert.Unit unit)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt =
                balancePrecompiled.transfer(from, to, weiValue.toBigIntegerExact());
        if (transactionReceipt.isStatusOK()) {
            RetCode codeSuccess = PrecompiledRetCode.CODE_SUCCESS;
            codeSuccess.setTransactionReceipt(transactionReceipt);
            return codeSuccess;
        } else {
            return ReceiptParser.parseTransactionReceipt(transactionReceipt, null);
        }
    }

    public void transferAsync(
            String from, String to, String amount, Convert.Unit unit, PrecompiledCallback callback)
            throws ContractException {
        BigDecimal weiValue = Convert.toWei(amount, unit);
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        this.balancePrecompiled.transfer(
                from, to, weiValue.toBigIntegerExact(), createTransactionCallback(callback));
    }

    public RetCode registerCaller(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt receipt = balancePrecompiled.registerCaller(address);
        if (receipt.isStatusOK()) {
            RetCode codeSuccess = PrecompiledRetCode.CODE_SUCCESS;
            codeSuccess.setTransactionReceipt(receipt);
            return codeSuccess;
        } else {
            return ReceiptParser.parseTransactionReceipt(receipt, null);
        }
    }

    public void registerCallerAsync(String address, PrecompiledCallback callback)
            throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        this.balancePrecompiled.registerCaller(address, createTransactionCallback(callback));
    }

    public RetCode unregisterCaller(String address) throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        TransactionReceipt receipt = balancePrecompiled.unregisterCaller(address);
        if (receipt.isStatusOK()) {
            RetCode codeSuccess = PrecompiledRetCode.CODE_SUCCESS;
            codeSuccess.setTransactionReceipt(receipt);
            return codeSuccess;
        } else {
            return ReceiptParser.parseTransactionReceipt(receipt, null);
        }
    }

    public void unregisterCallerAsync(String address, PrecompiledCallback callback)
            throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        this.balancePrecompiled.unregisterCaller(address, createTransactionCallback(callback));
    }

    public List<String> listCaller() throws ContractException {
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        List<String> result = balancePrecompiled.listCaller();
        return result;
    }

    private TransactionCallback createTransactionCallback(PrecompiledCallback callback) {
        return new TransactionCallback() {
            @Override
            public void onResponse(TransactionReceipt receipt) {
                RetCode retCode;
                try {
                    retCode = getBalanceRetCode(receipt);
                } catch (ContractException e) {
                    retCode = new RetCode(e.getErrorCode(), e.getMessage());
                    retCode.setTransactionReceipt(receipt);
                }
                callback.onResponse(retCode);
            }
        };
    }

    private RetCode getBalanceRetCode(TransactionReceipt receipt) throws ContractException {
        int status = receipt.getStatus();
        if (status != 0) {
            ReceiptParser.getErrorStatus(receipt);
        }
        RetCode retCode = PrecompiledRetCode.CODE_SUCCESS;
        retCode.setTransactionReceipt(receipt);
        return retCode;
    }
}
