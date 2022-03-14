package org.fisco.bcos.sdk.v3.contract.auth.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.NumericType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.v3.contract.auth.po.AuthType;
import org.fisco.bcos.sdk.v3.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionException;

public class AuthManager {

    private final CommitteeManager committeeManager;
    private final ContractAuthPrecompiled contractAuthPrecompiled;
    private final TransactionDecoderInterface decoder;
    // default block number interval. after current block number, it will be outdated. Default value
    // is about a week.
    private BigInteger DEFAULT_BLOCK_NUMBER_INTERVAL = BigInteger.valueOf(3600 * 24 * 7);

    public AuthManager(Client client, CryptoKeyPair credential) {
        this.committeeManager =
                CommitteeManager.load(
                        PrecompiledAddress.COMMITTEE_MANAGER_ADDRESS, client, credential);
        this.contractAuthPrecompiled =
                ContractAuthPrecompiled.load(
                        PrecompiledAddress.CONTRACT_AUTH_ADDRESS, client, credential);
        this.decoder = new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());
    }

    public AuthManager(Client client, CryptoKeyPair credential, BigInteger blockNumberInterval) {
        this(client, credential);
        this.DEFAULT_BLOCK_NUMBER_INTERVAL = blockNumberInterval;
    }

    public String getCommitteeAddress() throws ContractException {
        return committeeManager._committee();
    }

    public String getProposalManagerAddress() throws ContractException {
        return committeeManager._proposalMgr();
    }

    /**
     * apply for update governor, only governor can call it
     *
     * @param account new governor address
     * @param weight 0-delete, >0-update or insert
     * @return proposalId
     */
    public BigInteger updateGovernor(String account, BigInteger weight)
            throws ContractCodecException, TransactionException, IOException {
        TransactionReceipt tr =
                committeeManager.createUpdateGovernorProposal(
                        account, weight, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * apply set participate rate and win rate. only governor can call it
     *
     * @param participatesRate [0,100]. if 0, always succeed.
     * @param winRate [0,100].
     * @return proposalId
     */
    public BigInteger setRate(BigInteger participatesRate, BigInteger winRate)
            throws ContractCodecException, TransactionException, IOException {
        TransactionReceipt tr =
                committeeManager.createSetRateProposal(
                        participatesRate, winRate, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(), CommitteeManager.FUNC_CREATESETRATEPROPOSAL, tr);
        return getProposal(transactionResponse);
    }

    /**
     * submit a proposal of setting deploy contract auth type, only governor can call it
     *
     * @param deployAuthType 1-whitelist; 2-blacklist
     * @return proposalId
     */
    public BigInteger setDeployAuthType(AuthType deployAuthType)
            throws ContractCodecException, TransactionException, IOException {
        TransactionReceipt tr =
                committeeManager.createSetDeployAuthTypeProposal(
                        deployAuthType.getValue(), DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * get global deploy auth type
     *
     * @return deployAuthType
     */
    public BigInteger getDeployAuthType() throws ContractException {
        return this.contractAuthPrecompiled.deployType();
    }

    /**
     * submit a proposal of adding deploy contract auth for account, only governor can call it
     *
     * @param account account address string
     * @param openFlag true-open; false-close
     * @return proposalId
     */
    public BigInteger modifyDeployAuth(String account, Boolean openFlag)
            throws ContractCodecException, TransactionException, IOException {
        TransactionReceipt tr =
                committeeManager.createModifyDeployAuthProposal(
                        account, openFlag, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * submit a proposal of resetting contract admin, only governor can call it
     *
     * @param newAdmin admin address
     * @param contractAddr the address of contract which will propose to reset admin
     * @return proposalId
     */
    public BigInteger resetAdmin(String newAdmin, String contractAddr)
            throws ContractCodecException, TransactionException, IOException {
        TransactionReceipt tr =
                committeeManager.createResetAdminProposal(
                        newAdmin, contractAddr, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * revoke proposal, only governor can call it
     *
     * @param proposalId id
     */
    public TransactionReceipt revokeProposal(BigInteger proposalId) {
        return committeeManager.revokeProposal(proposalId);
    }

    /**
     * unified vote, only governor can call it
     *
     * @param proposalId id
     * @param agree true or false
     */
    public TransactionReceipt voteProposal(BigInteger proposalId, Boolean agree) {
        return committeeManager.voteProposal(proposalId, agree);
    }

    /**
     * getProposal return value in transactionResponse
     *
     * @param transactionResponse which get proposal from
     * @return proposal id
     */
    private BigInteger getProposal(TransactionResponse transactionResponse)
            throws TransactionException {
        if (transactionResponse == null) {
            throw new TransactionException("Decode transaction response error");
        }
        if (transactionResponse.getTransactionReceipt().getStatus() != 0) {
            throw new TransactionException(
                    transactionResponse.getReceiptMessages(),
                    transactionResponse.getTransactionReceipt().getStatus());
        }
        List<Type> valuesList = transactionResponse.getResults();
        if (valuesList == null || valuesList.isEmpty()) {
            throw new TransactionException("Decode transaction response error");
        }
        NumericType value = (NumericType) valuesList.get(0);
        return value.getValue();
    }

    /**
     * get proposal info
     *
     * @param proposalId proposal id
     * @return return ProposalInfo {id, proposer, proposalType, blockNumberInterval, status,
     *     address[] agreeVoters, address[] againstVoters }
     */
    public ProposalInfo getProposalInfo(BigInteger proposalId) throws ContractException {
        return new ProposalInfo()
                .fromTuple(committeeManager.getProposalManager().getProposalInfo(proposalId));
    }

    /**
     * get Committee info
     *
     * @return CommitteeInfo
     */
    public CommitteeInfo getCommitteeInfo() throws ContractException {
        return new CommitteeInfo().fromTuple(committeeManager.getCommittee().getCommitteeInfo());
    }

    /**
     * check the account whether this account can deploy contract
     *
     * @param account the account to check
     * @return true or false
     */
    public Boolean checkDeployAuth(String account) throws ContractException {
        return contractAuthPrecompiled.hasDeployAuth(account);
    }

    /**
     * check the contract interface func whether this account can call
     *
     * @param contractAddr the contractAddress
     * @param func interface func selector of contract, 4 bytes
     * @param account the account to check
     * @return true or false
     */
    public Boolean checkMethodAuth(String contractAddr, byte[] func, String account)
            throws ContractException {
        return contractAuthPrecompiled.checkMethodAuth(contractAddr, func, account);
    }

    /**
     * get a specific contract admin
     *
     * @param contractAddress the contract to get admin
     * @return admin address
     */
    public String getAdmin(String contractAddress) throws ContractException {
        return contractAuthPrecompiled.getAdmin(contractAddress);
    }

    /**
     * set a specific contract's method auth type, only contract admin can call it
     *
     * @param contractAddr the contract address to set auth
     * @param func interface func selector of contract, 4 bytes
     * @param authType white_list or black_list
     * @return set result, 0 is success
     */
    public BigInteger setMethodAuthType(String contractAddr, byte[] func, AuthType authType) {
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setMethodAuthType(contractAddr, func, authType.getValue());
        return contractAuthPrecompiled.getSetMethodAuthTypeOutput(transactionReceipt).getValue1();
    }

    /**
     * set a specific contract's method ACL, only contract admin can call it
     *
     * @param contractAddr the contract address to set acl
     * @param func interface func selector of contract, 4 bytes
     * @param account the account to set
     * @param isOpen if open, then white_list type is true, black_list is false; if close, then
     *     white_list type is false, black_list is true;
     * @return set result, 0 is success
     */
    public BigInteger setMethodAuth(
            String contractAddr, byte[] func, String account, boolean isOpen) {
        TransactionReceipt receipt;
        if (isOpen) {
            receipt = contractAuthPrecompiled.openMethodAuth(contractAddr, func, account);
            return contractAuthPrecompiled.getOpenMethodAuthOutput(receipt).getValue1();
        } else {
            receipt = contractAuthPrecompiled.closeMethodAuth(contractAddr, func, account);
            return contractAuthPrecompiled.getCloseMethodAuthOutput(receipt).getValue1();
        }
    }

    /**
     * get proposal count
     *
     * @return count
     */
    public BigInteger proposalCount() throws ContractException {
        return committeeManager.getProposalManager()._proposalCount();
    }
}
