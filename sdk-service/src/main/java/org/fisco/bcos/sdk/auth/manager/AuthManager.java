package org.fisco.bcos.sdk.auth.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.auth.contracts.Committee;
import org.fisco.bcos.sdk.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.auth.contracts.ProposalManager;
import org.fisco.bcos.sdk.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;

public class AuthManager {

    private String committeeManagerAddress;
    private CommitteeManager committeeManager;
    private Client client;
    private CryptoKeyPair credential;
    private TransactionDecoderInterface decoder;
    // default blocknumber interval. after current block number, it will be outdated. Default value
    // is about a week.
    private BigInteger DEFAULT_BLOCKNUMBER_INTERVAL = BigInteger.valueOf(3600 * 24 * 7);

    public AuthManager(String committeeManagerAddress, Client client, CryptoKeyPair credential) {
        this.committeeManagerAddress = committeeManagerAddress;
        this.committeeManager = CommitteeManager.load(committeeManagerAddress, client, credential);
        this.decoder = new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());
    }

    public AuthManager(
            String committeeManagerAddress,
            Client client,
            CryptoKeyPair credential,
            BigInteger blockNumberInterval) {
        this(committeeManagerAddress, client, credential);
        this.DEFAULT_BLOCKNUMBER_INTERVAL = blockNumberInterval;
    }

    public String getCommitteeAddress() throws ContractException {
        return committeeManager._committee();
    }

    public String getProposalManagerAddress() throws ContractException {
        return committeeManager._proposalMgr();
    }

    public String getContractAuthPrecompiledAddress() throws ContractException {
        return committeeManager._contractPrecompiled();
    }

    public BigInteger updateGovernor(String account, BigInteger weight)
            throws ABICodecException, TransactionException, IOException {
        TransactionReceipt tr = createUpdateGovernorProposal(account, weight);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        tr);
        return voteProposal(transactionResponse);
    }

    public BigInteger setRate(BigInteger participatesRate, BigInteger winRate)
            throws ABICodecException, TransactionException, IOException {
        TransactionReceipt tr = createSetRateProposal(participatesRate, winRate);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(), CommitteeManager.FUNC_CREATESETRATEPROPOSAL, tr);
        return voteProposal(transactionResponse);
    }

    public BigInteger setDeployAuthType(BigInteger deployAuthType)
            throws ABICodecException, TransactionException, IOException {
        TransactionReceipt tr = createSetDeployAuthTypeProposal(deployAuthType);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        tr);
        return voteProposal(transactionResponse);
    }

    public BigInteger modifyDeployAuth(String account, Boolean openFlag)
            throws ABICodecException, TransactionException, IOException {
        TransactionReceipt tr = createModifyDeployAuthProposal(account, openFlag);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        tr);
        return voteProposal(transactionResponse);
    }

    public BigInteger resetAdmin(String newAdmin, String contractAddr)
            throws ABICodecException, TransactionException, IOException {
        TransactionReceipt tr = createResetAdminProposal(newAdmin, contractAddr);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        tr);
        return voteProposal(transactionResponse);
    }

    /*
     * apply for update governor
     * @param external account
     * @param weight, 0-delete, >0-update or insert
     */
    public TransactionReceipt createUpdateGovernorProposal(String account, BigInteger weight) {
        return committeeManager.createUpdateGovernorProposal(
                account, weight, DEFAULT_BLOCKNUMBER_INTERVAL);
    }

    /*
     * apply set participate rate and win rate.
     * @param paricipate rate, [0,100]. if 0, always succeed.
     * @param win rate, [0,100].
     */
    public TransactionReceipt createSetRateProposal(
            BigInteger participatesRate, BigInteger winRate) {
        return committeeManager.createSetRateProposal(
                participatesRate, winRate, DEFAULT_BLOCKNUMBER_INTERVAL);
    }

    /*
     * submit an proposal of setting deploy contract auth type
     * @param deployAuthType: 1- whitelist; 2-blacklist
     */
    public TransactionReceipt createSetDeployAuthTypeProposal(BigInteger deployAuthType) {
        return committeeManager.createSetDeployAuthTypeProposal(
                deployAuthType, DEFAULT_BLOCKNUMBER_INTERVAL);
    }

    /*
     * submit an proposal of adding deploy contract auth for account
     * @param account
     * @param openFlag: true-open; false-close
     */
    public TransactionReceipt createModifyDeployAuthProposal(String account, Boolean openFlag) {
        return committeeManager.createModifyDeployAuthProposal(
                account, openFlag, DEFAULT_BLOCKNUMBER_INTERVAL);
    }

    /*
     * submit an propsal of resetting contract admin
     * @param newAdmin
     * @param contractAddr the address of contract which will propose to reset admin
     */
    public TransactionReceipt createResetAdminProposal(String newAdmin, String contractAddr) {
        return committeeManager.createResetAdminProposal(
                newAdmin, contractAddr, DEFAULT_BLOCKNUMBER_INTERVAL);
    }

    /*
     * revoke proposal
     * @param proposal id
     */
    public TransactionReceipt revokeProposal(BigInteger proposalId) {
        return committeeManager.revokeProposal(proposalId);
    }

    /*
     * unified vote
     * @param proposal id
     * @param true or false
     */
    public TransactionReceipt voteProposal(BigInteger proposalId, Boolean agree) {
        return committeeManager.voteProposal(proposalId, agree);
    }

    public BigInteger voteProposal(TransactionResponse transactionResponse)
            throws TransactionException {
        if (transactionResponse == null) {
            throw new TransactionException("Decode transaction response error");
        }
        List<Object> valuesList = transactionResponse.getValuesList();
        if (valuesList == null || valuesList.size() == 0) {
            throw new TransactionException("Decode transaction response error");
        }
        BigInteger proposalId = BigInteger.valueOf((long) valuesList.get(0));
        voteProposal(proposalId, true);
        return proposalId;
    }

    /*
     * get proposal info
     * @param proposal id
     * @result
     *   id,
     *   proposer,
     *   proposalType,
     *   blockNumberInterval,
     *   status,
     *   address[] agreeVoters,
     *   address[] againstVoters
     */
    public ProposalInfo getProposalInfo(BigInteger proposalId) throws ContractException {
        ProposalManager proposalManager =
                ProposalManager.load(getProposalManagerAddress(), client, credential);
        return new ProposalInfo().fromTuple(proposalManager.getProposalInfo(proposalId));
    }

    public CommitteeInfo getCommitteeInfo() throws ContractException {
        Committee committee = Committee.load(getCommitteeAddress(), client, credential);
        return new CommitteeInfo().fromTuple(committee.getCommitteeInfo());
    }

    public String getCommitteeManagerAddress() {
        return committeeManagerAddress;
    }

    public void setCommitteeManagerAddress(String committeeManagerAddress) {
        this.committeeManagerAddress = committeeManagerAddress;
    }

    public Boolean hasDeployAuth(String account) throws ContractException {
        ContractAuthPrecompiled contractAuthPrecompiled =
                ContractAuthPrecompiled.load(
                        getContractAuthPrecompiledAddress(), client, credential);
        return contractAuthPrecompiled.hasDeployAuth(account);
    }

    public Boolean checkMethodAuth(String contractAddr, byte[] func, String account)
            throws ContractException {
        ContractAuthPrecompiled contractAuthPrecompiled =
                ContractAuthPrecompiled.load(
                        getContractAuthPrecompiledAddress(), client, credential);
        return contractAuthPrecompiled.checkMethodAuth(contractAddr, func, account);
    }

    public String getAdmin(String contractAddress) throws ContractException {
        ContractAuthPrecompiled contractAuthPrecompiled =
                ContractAuthPrecompiled.load(
                        getContractAuthPrecompiledAddress(), client, credential);
        return contractAuthPrecompiled.getAdmin(contractAddress);
    }
}
