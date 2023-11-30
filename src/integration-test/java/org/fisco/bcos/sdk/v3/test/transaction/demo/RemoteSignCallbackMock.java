package org.fisco.bcos.sdk.v3.test.transaction.demo;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;

public class RemoteSignCallbackMock implements RemoteSignCallbackInterface {

    protected AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor;
    protected long transactionData;
    protected int txAttribute;

    public RemoteSignCallbackMock(
            AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor,
            long transactionData,
            int txAttribute) {
        this.assembleTransactionWithRemoteSignProcessor =
                assembleTransactionWithRemoteSignProcessor;
        this.transactionData = transactionData;
        this.txAttribute = txAttribute;
    }

    /**
     * 签名结果回调的实现
     *
     * @param signatureStr 签名服务回调返回的签名结果串
     * @return *
     */
    @Override
    public int handleSignedTransaction(SignatureResult signatureStr) {
        System.out.println(System.currentTimeMillis() + " SignatureResult: " + signatureStr);
        // send the transaction after sign it
        TransactionReceipt tr = null;
        try {
            tr =
                    assembleTransactionWithRemoteSignProcessor.encodeAndPush(
                            transactionData, signatureStr.convertToString(), this.txAttribute);
        } catch (JniException e) {
            e.printStackTrace();
        }
        System.out.println(
                "handleSignedTransaction transactionReceipt is: " + JsonUtils.toJson(tr));
        return 0;
    }
}
