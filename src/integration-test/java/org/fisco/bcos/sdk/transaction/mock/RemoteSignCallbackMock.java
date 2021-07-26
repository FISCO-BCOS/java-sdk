package org.fisco.bcos.sdk.transaction.mock;

import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.client.protocol.model.TransactionData;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;

import java.util.concurrent.ExecutionException;

public class RemoteSignCallbackMock implements RemoteSignCallbackInterface {

    protected AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor;
    protected TransactionData rawTransaction;

    public RemoteSignCallbackMock(
            AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor,
            TransactionData rawTransaction) {
        this.assembleTransactionWithRemoteSignProcessor =
                assembleTransactionWithRemoteSignProcessor;
        this.rawTransaction = rawTransaction;
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
        // 完成了交易签名后，将其发送出去
        TransactionReceipt tr = null;
        try {
            tr = assembleTransactionWithRemoteSignProcessor.signAndPush(
                    rawTransaction, signatureStr.getSignatureBytes()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(
                "handleSignedTransaction transactionReceipt is: " + JsonUtils.toJson(tr));
        return 0;
    }
}
