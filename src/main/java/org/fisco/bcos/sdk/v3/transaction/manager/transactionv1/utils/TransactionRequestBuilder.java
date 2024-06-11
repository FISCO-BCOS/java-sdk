package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class TransactionRequestBuilder {

    private TransactionVersion version = TransactionVersion.V1;
    private String abi;
    private String method;
    private String to;
    private BigInteger blockLimit;
    private String nonce;
    private BigInteger value;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String bin;
    private EIP1559Struct eip1559Struct;
    private byte[] extension = null;

    public TransactionRequestBuilder() {}

    public TransactionRequestBuilder(String abi, String method, String to) {
        this.abi = abi;
        this.method = method;
        this.to = to;
    }

    public TransactionRequestBuilder(String abi, String bin) {
        this.abi = abi;
        this.bin = bin;
    }

    public TransactionRequestBuilder setVersion(TransactionVersion version) {
        if (this.version.getValue() < version.getValue()) {
            this.version = version;
        }
        return this;
    }

    public TransactionRequestBuilder setVersionForce(TransactionVersion version) {
        this.version = version;
        return this;
    }

    public TransactionRequestBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public TransactionRequestBuilder setAbi(String abi) {
        this.abi = abi;
        return this;
    }

    public TransactionRequestBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public TransactionRequestBuilder setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
        return this;
    }

    public TransactionRequestBuilder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public TransactionRequestBuilder setValue(BigInteger value) {
        this.value = value;
        if (value != null) {
            return setVersion(TransactionVersion.V1);
        }
        return this;
    }

    public TransactionRequestBuilder setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
        if (gasPrice != null) {
            return setVersion(TransactionVersion.V1);
        }
        return this;
    }

    public TransactionRequestBuilder setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
        if (gasLimit != null) {
            return setVersion(TransactionVersion.V1);
        }
        return this;
    }

    public TransactionRequestBuilder setEIP1559Struct(EIP1559Struct eip1559Struct) {
        this.eip1559Struct = eip1559Struct;
        if (eip1559Struct != null) {
            setVersion(TransactionVersion.V1);
        }
        return this;
    }

    public TransactionRequestBuilder setExtension(byte[] extension) {
        this.extension = extension;
        if (extension != null && extension.length > 0) {
            setVersion(TransactionVersion.V2);
        }
        return this;
    }

    public TransactionRequestBuilder setBin(String bin) {
        this.bin = bin;
        return this;
    }

    public TransactionRequest buildRequest(List<Object> params) throws ContractException {
        if (params == null) {
            throw new ContractException("SendTransaction params is null, please set it manually.");
        }
        TransactionRequest sendTransactionRequest =
                new TransactionRequest(
                        this.version,
                        this.abi,
                        this.method,
                        this.to,
                        this.blockLimit,
                        this.nonce,
                        this.value,
                        this.gasPrice,
                        this.gasLimit,
                        this.eip1559Struct,
                        this.extension);
        sendTransactionRequest.setParams(params);
        return sendTransactionRequest;
    }

    public TransactionRequestWithStringParams buildStringParamsRequest(List<String> stringParams)
            throws ContractException {
        if (stringParams == null) {
            throw new ContractException("SendTransaction params is null, please set it manually.");
        }
        TransactionRequestWithStringParams request =
                new TransactionRequestWithStringParams(
                        this.version,
                        this.abi,
                        this.method,
                        this.to,
                        this.blockLimit,
                        this.nonce,
                        this.value,
                        this.gasPrice,
                        this.gasLimit,
                        this.eip1559Struct,
                        this.extension);
        request.setStringParams(stringParams);
        return request;
    }

    public DeployTransactionRequest buildDeployRequest(List<Object> params)
            throws ContractException {
        if (params == null) {
            throw new ContractException("SendTransaction params is null, please set it manually.");
        }
        if (bin == null || bin.isEmpty()) {
            throw new ContractException(
                    "Deploy contract bin is null or empty, please set it manually.");
        }
        DeployTransactionRequest request =
                new DeployTransactionRequest(
                        this.version,
                        this.abi,
                        this.bin,
                        this.blockLimit,
                        this.nonce,
                        this.value,
                        this.gasPrice,
                        this.gasLimit,
                        this.eip1559Struct,
                        this.extension);
        if (to != null) {
            request.setTo(to);
        }
        request.setParams(params);
        return request;
    }

    public DeployTransactionRequestWithStringParams buildDeployStringParamsRequest(
            List<String> stringParams) throws ContractException {
        if (stringParams == null) {
            throw new ContractException("SendTransaction params is null, please set it manually.");
        }
        if (bin == null || bin.isEmpty()) {
            throw new ContractException(
                    "Deploy contract bin is null or empty, please set it manually.");
        }
        DeployTransactionRequestWithStringParams request =
                new DeployTransactionRequestWithStringParams(
                        this.version,
                        this.abi,
                        this.bin,
                        this.blockLimit,
                        this.nonce,
                        this.value,
                        this.gasPrice,
                        this.gasLimit,
                        this.eip1559Struct,
                        this.extension);
        if (to != null) {
            request.setTo(to);
        }
        request.setStringParams(stringParams);
        return request;
    }

    public AbiEncodedRequest buildAbiEncodedRequest(byte[] encodedParams) throws ContractException {
        if (encodedParams == null) {
            throw new ContractException("SendTransaction params is null, please set it manually.");
        }
        AbiEncodedRequest abiEncodedDeployRequest =
                new AbiEncodedRequest(
                        this.version,
                        this.abi,
                        this.to,
                        this.blockLimit,
                        this.nonce,
                        this.value,
                        this.gasPrice,
                        this.gasLimit,
                        this.eip1559Struct,
                        this.extension);
        abiEncodedDeployRequest.setEncodedData(encodedParams);
        return abiEncodedDeployRequest;
    }
}
