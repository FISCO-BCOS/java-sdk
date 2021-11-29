package org.fisco.bcos.sdk.auth;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.fisco.bcos.sdk.BcosSDKTest;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.contract.auth.manager.AuthManager;
import org.fisco.bcos.sdk.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    public AtomicLong receiptCount = new AtomicLong();
    private static final String GROUP = "group";

    @Test
    public void test1GetCommitteeInfo() throws ConfigException, ContractException, JniException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();

        AuthManager authManager = new AuthManager(client, cryptoKeyPair);
        CommitteeInfo committeeInfo = authManager.getCommitteeInfo();
        System.out.println(committeeInfo);
        Assert.assertEquals(1, committeeInfo.getGovernorList().size());
    }

    @Test
    public void test2UpdateGovernor()
            throws ConfigException, ContractException, JniException, ABICodecException,
                    TransactionException, IOException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        AuthManager authManager = new AuthManager(client, cryptoKeyPair);
        BigInteger proposalId =
                authManager.updateGovernor(
                        "0xc49fd7d7648ef4f4774d642d905db2e66f5089a8", BigInteger.ONE);
        Assert.assertEquals(proposalId, BigInteger.ONE);
    }

    @Test
    public void test3RevokeProposal()
            throws ConfigException, ContractException, JniException, ABICodecException,
                    TransactionException, IOException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        AuthManager authManager = new AuthManager(client, cryptoKeyPair);
        BigInteger proposalId =
                authManager.updateGovernor(
                        "0xc49fd7d7648ef4f4774d642d905db2e66f5089a8", BigInteger.ONE);
        TransactionReceipt receipt = authManager.revokeProposal(BigInteger.valueOf(123321));
        Assert.assertEquals(proposalId, BigInteger.ONE);
    }

    @Test
    public void test4VoteProposal()
            throws ConfigException, ContractException, JniException, ABICodecException,
                    TransactionException, IOException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        AuthManager authManager = new AuthManager(client, cryptoKeyPair);
        BigInteger proposalId =
                authManager.updateGovernor(
                        "0xc49fd7d7648ef4f4774d642d905db2e66f5089a8", BigInteger.ONE);
        TransactionReceipt receipt = authManager.voteProposal(BigInteger.valueOf(123321), true);
        Assert.assertEquals(proposalId, BigInteger.ONE);
    }
}
