package org.fisco.bcos.sdk.transaction.mock;

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;

public class RemoteSignProviderMock implements RemoteSignProviderInterface {
    private CryptoSuite cryptoSuite;

    public RemoteSignProviderMock(CryptoSuite cryptoSuite) {
        setCryptoSuite(cryptoSuite);
    }

    public CryptoSuite getCryptoSuite() {
        return cryptoSuite;
    }

    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    @Override
    public SignatureResult requestForSign(byte[] rawTxHash, int cryptoType) {
        System.out.println("request for sign sync, and crypto type is " + cryptoType);
        try {
            // sleep for test
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CryptoKeyPair cryptoKeyPair = cryptoSuite.getCryptoKeyPair();
        SignatureResult signatureResult = cryptoSuite.sign(rawTxHash, cryptoKeyPair);
        System.out.println(
                System.currentTimeMillis()
                        + " crypto type:"
                        + cryptoType
                        + ",signData -> signature:"
                        + signatureResult.convertToString());
        return signatureResult;
    }

    /*模拟异步调用，demo代码比较简单，就本地直接同步回调了，可以改成启动一个签名线程*/
    @Override
    public void requestForSignAsync(
            byte[] dataToSign, int cryptoType, RemoteSignCallbackInterface callback) {
        System.out.println("request for sign async, and crypto type is " + cryptoType);
        try {
            // sleep for test
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 模拟同时异步调用两个签名服务，此处以同步调用服务为例
        CompletableFuture<SignatureResult> f1 =
                CompletableFuture.supplyAsync(() -> requestForSign(dataToSign, cryptoType));
        CompletableFuture<SignatureResult> f2 =
                CompletableFuture.supplyAsync(() -> requestForSign(dataToSign, cryptoType));
        CompletableFuture<Object> f = CompletableFuture.anyOf(f1, f2);
        f.thenApplyAsync(
                s -> {
                    System.out.println(
                            System.currentTimeMillis()
                                    + " crypto type:"
                                    + cryptoType
                                    + ",async either signData -> signature:"
                                    + s);
                    if (callback != null) {
                        callback.handleSignedTransaction((SignatureResult) s);
                    }
                    return s;
                });
    }
}
