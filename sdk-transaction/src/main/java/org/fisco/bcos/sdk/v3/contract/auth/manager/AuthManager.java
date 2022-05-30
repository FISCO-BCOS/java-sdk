package org.fisco.bcos.sdk.v3.contract.auth.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.NumericType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.v3.contract.auth.po.AuthType;
import org.fisco.bcos.sdk.v3.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.v3.utils.AddressUtils;

public class AuthManager {

    private final Client client;
    private final CommitteeManager committeeManager;
    private final ContractAuthPrecompiled contractAuthPrecompiled;
    private final TransactionDecoderInterface decoder;
    // default block number interval. after current block number, it will be outdated. Default value
    // is about a week.
    private BigInteger DEFAULT_BLOCK_NUMBER_INTERVAL = BigInteger.valueOf(3600 * 24 * 7L);

    public AuthManager(Client client, CryptoKeyPair credential) {
        this.client = client;
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
            throws ContractCodecException, TransactionException, IOException, ContractException {
        if (weight.compareTo(BigInteger.ZERO) < 0) {
            throw new ContractException("Error input weight: " + weight);
        }
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
            throws ContractCodecException, TransactionException, IOException, ContractException {
        if (participatesRate.compareTo(BigInteger.ZERO) < 0
                || participatesRate.compareTo(BigInteger.valueOf(100)) >= 0) {
            throw new ContractException("Error input participatesRate: " + participatesRate);
        }
        if (winRate.compareTo(BigInteger.ZERO) < 0
                || winRate.compareTo(BigInteger.valueOf(100)) >= 0) {
            throw new ContractException("Error input winRate: " + winRate);
        }
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
            throws ContractCodecException, TransactionException, IOException, ContractException {
        if (!AddressUtils.isValidAddress(contractAddr)) {
            throw new ContractException("Invalid address : " + contractAddr);
        }
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
     * submit a proposal of remove consensus node, only governor can call it
     *
     * @param node node ID
     * @return proposal ID
     */
    public BigInteger createRmNodeProposal(String node)
            throws TransactionException, ContractCodecException, IOException, ContractException {
        // check the nodeId exists in the nodeList or not
        if (!existsInNodeList(node)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        TransactionReceipt rmNodeProposal =
                committeeManager.createRmNodeProposal(node, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATERMNODEPROPOSAL,
                        rmNodeProposal);
        return getProposal(transactionResponse);
    }

    /**
     * submit a proposal of set consensus node weight, only governor can call it
     *
     * @param node node ID
     * @param weight consensus weight: weigh > 0, sealer; weight = 0, observer
     * @param addFlag flag to distinguish add a node or set node's weight,
     * @return proposal ID
     */
    public BigInteger createSetConsensusWeightProposal(
            String node, BigInteger weight, boolean addFlag)
            throws TransactionException, ContractCodecException, IOException, ContractException {
        // check the nodeId exists in the nodeList or not
        if (!existsInNodeList(node)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        weight = weight.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ZERO : weight;

        if (addFlag) {
            if (weight.compareTo(BigInteger.ZERO) > 0) {
                // check the node exists in the sealerList or not
                List<SealerList.Sealer> sealerList = client.getSealerList().getResult();
                if (sealerList.stream().anyMatch(sealer -> sealer.getNodeID().equals(node))) {
                    throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST);
                }
            } else {
                List<String> observerList = client.getObserverList().getResult();
                if (observerList.contains(node)) {
                    throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST);
                }
            }
        }

        TransactionReceipt tr =
                committeeManager.createSetConsensusWeightProposal(
                        node, weight, addFlag, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * submit a proposal of set system config, only governor can call it
     *
     * @param key system config key, only support
     *     (tx_gas_limit,tx_count_limit,consensus_leader_period)
     * @param value system config value, notice that tx_gas_limit > 100,000, tx_count_limit > 1,
     *     consensus_leader_period > 1
     * @return proposal ID
     */
    public BigInteger createSetSysConfigProposal(String key, BigInteger value)
            throws TransactionException, ContractCodecException, IOException, ContractException {
        if (SystemConfigService.checkSysValueValidation(key, value)) {
            throw new ContractException(
                    "Invalid value \" " + value + " \" for " + key + ", please check valid range.");
        }
        TransactionReceipt tr =
                committeeManager.createSetSysConfigProposal(
                        key, value.toString(), DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATESETSYSCONFIGPROPOSAL,
                        tr);
        return getProposal(transactionResponse);
    }

    /**
     * submit a proposal of upgrade vote computer logic contract, only governor can call it
     *
     * @param address vote computer address
     * @return proposal ID
     */
    public BigInteger createUpgradeVoteComputerProposal(String address)
            throws TransactionException, ContractCodecException, IOException {
        if (!AddressUtils.isValidAddress(address)) {
            throw new TransactionException("Invalid address : " + address);
        }
        TransactionReceipt tr =
                committeeManager.createUpgradeVoteComputerProposal(
                        address, DEFAULT_BLOCK_NUMBER_INTERVAL);
        TransactionResponse transactionResponse =
                decoder.decodeReceiptWithValues(
                        CommitteeManager.getABI(),
                        CommitteeManager.FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
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
        return new ProposalInfo(committeeManager.getProposalManager().getProposalInfo(proposalId));
    }

    /**
     * get proposal info list, range in [from,to]
     *
     * @param from begin proposal id
     * @param to end proposal id
     * @return return ProposalInfo list {id, proposer, proposalType, blockNumberInterval, status,
     *     address[] agreeVoters, address[] againstVoters }[]
     */
    public List<ProposalInfo> getProposalInfoList(BigInteger from, BigInteger to)
            throws ContractException {
        return committeeManager.getProposalManager().getProposalInfoList(from, to);
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
     * @param contractAddress the contractAddress
     * @param func interface func selector of contract, 4 bytes
     * @param account the account to check
     * @return true or false
     */
    public Boolean checkMethodAuth(String contractAddress, byte[] func, String account)
            throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        return contractAuthPrecompiled.checkMethodAuth(contractAddress, func, account);
    }

    /**
     * get auth of specific contract's method func
     *
     * @param contractAddress contract address
     * @param func method interface func selector, contains 4 bytes: hash(selector()).getBytes(0,4)
     * @return get a tuple of values: method auth type, access account list, block account list
     * @throws ContractException AuthType.valueOf will throw this exception
     */
    public Tuple3<AuthType, List<String>, List<String>> getMethodAuth(
            String contractAddress, byte[] func) throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        Tuple3<BigInteger, List<String>, List<String>> methodAuth =
                contractAuthPrecompiled.getMethodAuth(contractAddress, func);
        AuthType authType = AuthType.valueOf(methodAuth.getValue1().intValue());
        return new Tuple3<>(authType, methodAuth.getValue2(), methodAuth.getValue3());
    }

    /**
     * get a specific contract admin
     *
     * @param contractAddress the contract to get admin
     * @return admin address
     */
    public String getAdmin(String contractAddress) throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        return contractAuthPrecompiled.getAdmin(contractAddress);
    }

    /**
     * set a specific contract's method auth type, only contract admin can call it
     *
     * @param contractAddress the contract address to set auth
     * @param func interface func selector of contract, 4 bytes
     * @param authType white_list or black_list
     * @return set result, 0 is success
     */
    public RetCode setMethodAuthType(String contractAddress, byte[] func, AuthType authType)
            throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setMethodAuthType(
                        contractAddress, func, authType.getValue());
        return ReceiptParser.parseTransactionReceipt(transactionReceipt);
    }

    /**
     * set a specific contract's method ACL, only contract admin can call it
     *
     * @param contractAddress the contract address to set acl
     * @param func interface func selector of contract, 4 bytes
     * @param account the account to set
     * @param isOpen if open, then white_list type is true, black_list is false; if close, then
     *     white_list type is false, black_list is true;
     * @return set result, 0 is success
     */
    public RetCode setMethodAuth(
            String contractAddress, byte[] func, String account, boolean isOpen)
            throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        TransactionReceipt receipt =
                isOpen
                        ? contractAuthPrecompiled.openMethodAuth(contractAddress, func, account)
                        : contractAuthPrecompiled.closeMethodAuth(contractAddress, func, account);
        return ReceiptParser.parseTransactionReceipt(receipt);
    }

    /**
     * set a contract status, freeze or normal, only contract manager can call
     *
     * @param contractAddress contract address
     * @param isFreeze is freeze or normal
     * @return 0 is success, otherwise is error
     */
    public RetCode setContractStatus(String contractAddress, boolean isFreeze)
            throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setContractStatus(contractAddress, isFreeze);
        return ReceiptParser.parseTransactionReceipt(transactionReceipt);
    }

    /**
     * check contract is available, if normal then return true
     *
     * @param contractAddress contract address
     * @return if true, then this contract can be called
     */
    public Boolean contractAvailable(String contractAddress) throws ContractException {
        if (AddressUtils.isValidAddress(contractAddress)) {
            throw new ContractException("Invalid address: " + contractAddress);
        }
        return contractAuthPrecompiled.contractAvailable(contractAddress);
    }

    /**
     * get proposal count
     *
     * @return count
     */
    public BigInteger proposalCount() throws ContractException {
        return committeeManager.getProposalManager()._proposalCount();
    }

    private boolean existsInNodeList(String nodeId) {
        List<String> nodeIdList = client.getGroupPeers().getGroupPeers();
        return nodeIdList.contains(nodeId);
    }
}
