package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class AbiEncodedRequest extends BasicRequest {
    // required
    protected byte[] encodedData;
    protected boolean isCreate = false;

    public AbiEncodedRequest(
            String abi,
            String to,
            BigInteger blockLimit,
            String nonce,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct,
            byte[] extension) {
        super(abi, "", to, blockLimit, nonce, value, gasPrice, gasLimit, eip1559Struct, extension);
    }

    public AbiEncodedRequest(BasicRequest request) {
        super(
                request.getAbi(),
                "",
                request.getTo(),
                request.getBlockLimit(),
                request.getNonce(),
                request.getValue(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getEip1559Struct(),
                request.getExtension());
    }

    @Override
    public boolean isTransactionEssentialSatisfy() {
        return encodedData != null && to != null;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public byte[] getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(byte[] encodedData) {
        this.encodedData = encodedData;
    }
}
