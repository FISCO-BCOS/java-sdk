package org.fisco.bcos.sdk.transaction.mock;

import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;

public class RemoteSignCallbackMock implements RemoteSignCallbackInterface {

    protected AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor;
    protected RawTransaction rawTransaction;

    public RemoteSignCallbackMock(
            AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor,
            RawTransaction rawTransaction) {
        this.assembleTransactionWithRemoteSignProcessor =
                assembleTransactionWithRemoteSignProcessor;
        this.rawTransaction = rawTransaction;
    }

    /**
     * callback of sign transaction
     *
     * @param signatureStr 签名服务回调返回的签名结果串
     * @return *
     */
    @Override
    public int handleSignedTransaction(SignatureResult signatureStr) {
        System.out.println(System.currentTimeMillis() + " SignatureResult: " + signatureStr);
        // after sign, send it
        TransactionReceipt tr =
                assembleTransactionWithRemoteSignProcessor.signAndPush(
                        rawTransaction, signatureStr.convertToString());
        System.out.println(
                "handleSignedTransaction transactionReceipt is: " + JsonUtils.toJson(tr));
        return 0;
    }
}
