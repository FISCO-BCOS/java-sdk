package org.fisco.bcos.sdk.v3.contract.auth.manager;

import static org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService.COMPATIBILITY_VERSION;
import static org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService.checkCompatibilityVersion;
import static org.fisco.bcos.sdk.v3.model.PrecompiledConstant.SYNC_KEEP_UP_THRESHOLD;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.AccountManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.v3.contract.auth.po.AccessStatus;
import org.fisco.bcos.sdk.v3.contract.auth.po.AuthType;
import org.fisco.bcos.sdk.v3.contract.auth.po.CommitteeInfo;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.RevertMessageParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class AuthManager {

    private final Client client;
    private final CommitteeManager committeeManager;
    private final ContractAuthPrecompiled contractAuthPrecompiled;
    private final AccountManager accountManager;
    // default block number interval. after current block number, it will be outdated. Default value
    // is about a week.
    private BigInteger DEFAULT_BLOCK_NUMBER_INTERVAL = BigInteger.valueOf(3600 * 24 * 7L);

    public CommitteeManager getCommitteeManager() {
        return committeeManager;
    }

    public ContractAuthPrecompiled getContractAuthPrecompiled() {
        return contractAuthPrecompiled;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public AuthManager(Client client, CryptoKeyPair credential) {
        this.client = client;
        this.committeeManager =
                CommitteeManager.load(
                        PrecompiledAddress.COMMITTEE_MANAGER_ADDRESS, client, credential);
        this.contractAuthPrecompiled =
                ContractAuthPrecompiled.load(
                        PrecompiledAddress.CONTRACT_AUTH_ADDRESS, client, credential);
        this.accountManager = AccountManager.load(client, credential);
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
     * @param weight 0 == delete, bigger than 0 == update or insert
     * @return proposalId
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger updateGovernor(String account, BigInteger weight) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createUpdateGovernorProposal(
                        account, weight, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        return committeeManager.getCreateUpdateGovernorProposalOutput(tr).getValue1();
    }

    /**
     * apply set participate rate and win rate. only governor can call it
     *
     * @param participatesRate [0,100]. if 0, always succeed.
     * @param winRate [0,100].
     * @return proposalId
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger setRate(BigInteger participatesRate, BigInteger winRate)
            throws ContractException {
        TransactionReceipt tr =
                committeeManager.createSetRateProposal(
                        participatesRate, winRate, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        return committeeManager.getCreateSetRateProposalOutput(tr).getValue1();
    }

    /**
     * submit a proposal of setting deploy contract auth type, only governor can call it
     *
     * @param deployAuthType 1-whitelist; 2-blacklist
     * @return proposalId
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger setDeployAuthType(AuthType deployAuthType) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createSetDeployAuthTypeProposal(
                        deployAuthType.getValue(), DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId =
                committeeManager.getCreateSetDeployAuthTypeProposalOutput(tr).getValue1();
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    /**
     * get global deploy auth type
     *
     * @return deployAuthType
     * @throws ContractException throw when contract exec exception
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
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger modifyDeployAuth(String account, Boolean openFlag) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createModifyDeployAuthProposal(
                        account, openFlag, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId =
                committeeManager.getCreateModifyDeployAuthProposalOutput(tr).getValue1();
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    /**
     * submit a proposal of resetting contract admin, only governor can call it
     *
     * @param newAdmin admin address
     * @param contractAddr the address of contract which will propose to reset admin
     * @return proposalId
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger resetAdmin(String newAdmin, String contractAddr) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createResetAdminProposal(
                        newAdmin, contractAddr, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId = committeeManager.getCreateResetAdminProposalOutput(tr).getValue1();
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    /**
     * submit a proposal of remove consensus node, only governor can call it
     *
     * @param node node ID
     * @return proposal ID
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger createRmNodeProposal(String node) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createRmNodeProposal(node, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId = committeeManager.getCreateRmNodeProposalOutput(tr).getValue1();
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    /**
     * submit a proposal of set consensus node weight, only governor can call it
     *
     * @param node node ID
     * @param weight consensus weight: weigh bigger than 0, sealer; weight = 0, observer
     * @param addFlag flag to distinguish add a node or set node's weight,
     * @return proposal ID
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger createSetConsensusWeightProposal(
            String node, BigInteger weight, boolean addFlag) throws ContractException {
        weight = weight.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ZERO : weight;

        checkSetConsensusWeightParams(node, weight, addFlag);

        TransactionReceipt tr =
                committeeManager.createSetConsensusWeightProposal(
                        node, weight, addFlag, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId =
                committeeManager.getCreateSetConsensusWeightProposalOutput(tr).getValue1();
        // if setWeight to observer, it will cause precompiled error instead of returning retCode
        if (!tr.getLogEntries().isEmpty()
                && RevertMessageParser.isOutputStartWithRevertMethod(
                        tr.getLogEntries().get(0).getData())) {
            throw new ContractException(
                    "Exec proposal finished with error occurs, proposalId: "
                            + proposalId
                            + ", exec error msg: Cannot set weight to observer.");
        }
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    private void checkSetConsensusWeightParams(String node, BigInteger weight, boolean addFlag)
            throws ContractException {
        if (!addFlag) {
            if (weight.compareTo(BigInteger.ZERO) <= 0) {
                throw new ContractException(PrecompiledRetCode.CODE_INVALID_WEIGHT.getMessage());
            }
            return;
        }
        /// add node
        // check the nodeId exists in the nodeList or not
        boolean isAddSealer = weight.compareTo(BigInteger.ZERO) > 0;
        if (isAddSealer && !existsInNodeList(node)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        boolean existence =
                (isAddSealer)
                        ? client.getSealerList().getResult().stream()
                                .anyMatch(sealer -> sealer.getNodeID().equals(node))
                        : client.getObserverList().getResult().contains(node);
        if (existence) {
            throw new ContractException(
                    (isAddSealer)
                            ? PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST
                            : PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST);
        }
        if (isAddSealer) {
            List<String> observerList = client.getObserverList().getObserverList();
            if (observerList != null && !observerList.contains(node)) {
                throw new ContractException(
                        PrecompiledRetCode.CODE_ADD_SEALER_SHOULD_IN_OBSERVER.getMessage(),
                        PrecompiledRetCode.CODE_ADD_SEALER_SHOULD_IN_OBSERVER.getCode());
            }
            SyncStatus syncStatus = client.getSyncStatus();
            BigInteger highestNumber =
                    BigInteger.valueOf(syncStatus.getSyncStatus().getKnownHighestNumber());
            boolean anyMatch;
            if (syncStatus.getSyncStatus().getNodeId().equals(node)) {
                // sdk connect observer to be added to sealerList
                anyMatch =
                        syncStatus.getSyncStatus().getBlockNumber()
                                >= highestNumber.longValue() - SYNC_KEEP_UP_THRESHOLD;
            } else {
                anyMatch =
                        syncStatus.getSyncStatus().getPeers().stream()
                                .anyMatch(
                                        peersInfo ->
                                                peersInfo.getNodeId().equals(node)
                                                        && peersInfo.getBlockNumber()
                                                                >= (highestNumber.longValue()
                                                                        - SYNC_KEEP_UP_THRESHOLD));
            }
            if (!anyMatch) {
                throw new ContractException(
                        "Observer should keep up the block number sync threshold: "
                                + SYNC_KEEP_UP_THRESHOLD);
            }
        }
    }

    /**
     * submit a proposal of set system config, only governor can call it
     *
     * @param key system config key, only support
     *     (tx_gas_limit,tx_count_limit,consensus_leader_period)
     * @param value system config value, notice that tx_gas_limit bigger than 100,000,
     *     tx_count_limit bigger than 1, consensus_leader_period bigger than 1
     * @return proposal ID
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger createSetSysConfigProposal(String key, String value)
            throws ContractException {
        if (!SystemConfigService.checkSysNumberValueValidation(key, value)) {
            throw new ContractException(
                    "Invalid value \"" + value + "\" for " + key + ", please check valid range.");
        }
        if (COMPATIBILITY_VERSION.equals(key) && !checkCompatibilityVersion(client, value)) {
            String nodeVersionString =
                    client.getGroupInfo().getResult().getNodeList().stream()
                            .map(node -> node.getIniConfig().getBinaryInfo().getVersion())
                            .collect(Collectors.joining(","));
            throw new ContractException(
                    "The compatibility version "
                            + value
                            + " is not supported, please check the version of the chain. (The version of the chain is "
                            + nodeVersionString
                            + ")");
        }
        TransactionReceipt tr =
                committeeManager.createSetSysConfigProposal(
                        key, value, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        BigInteger proposalId =
                committeeManager.getCreateSetSysConfigProposalOutput(tr).getValue1();
        getExecEvent(tr, proposalId);
        return proposalId;
    }

    /**
     * submit a proposal of upgrade vote computer logic contract, only governor can call it
     *
     * @param address vote computer address
     * @return proposal ID
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger createUpgradeVoteComputerProposal(String address) throws ContractException {
        TransactionReceipt tr =
                committeeManager.createUpgradeVoteComputerProposal(
                        address, DEFAULT_BLOCK_NUMBER_INTERVAL);
        if (tr.getStatus() != TransactionReceiptStatus.Success.code) {
            if (tr.getStatus() == TransactionReceiptStatus.CallAddressError.code) {
                throw new ContractException(
                        "Call address error, maybe CommitteeManager is uninitialized or even not exist.",
                        tr.getStatus(),
                        tr);
            }
            ReceiptParser.getErrorStatus(tr);
        }
        return committeeManager.getCreateUpgradeVoteComputerProposalOutput(tr).getValue1();
    }

    /**
     * revoke proposal, only governor can call it
     *
     * @param proposalId id
     * @return transaction receipt
     */
    public TransactionReceipt revokeProposal(BigInteger proposalId) {
        return committeeManager.revokeProposal(proposalId);
    }

    /**
     * async revoke proposal, only governor can call it
     *
     * @param proposalId id
     * @param callback callback when get receipt
     */
    public void asyncRevokeProposal(BigInteger proposalId, TransactionCallback callback) {
        committeeManager.revokeProposal(proposalId, callback);
    }

    /**
     * unified vote, only governor can call it
     *
     * @param proposalId id
     * @param agree true or false
     * @return transaction receipt
     */
    public TransactionReceipt voteProposal(BigInteger proposalId, Boolean agree) {
        return committeeManager.voteProposal(proposalId, agree);
    }

    /**
     * async unified vote, only governor can call it
     *
     * @param proposalId id
     * @param agree true or false
     * @param callback callback when get receipt
     */
    public void asyncVoteProposal(
            BigInteger proposalId, Boolean agree, TransactionCallback callback) {
        committeeManager.voteProposal(proposalId, agree, callback);
    }

    /**
     * get proposal info
     *
     * @param proposalId proposal id
     * @return return ProposalInfo {id, proposer, proposalType, blockNumberInterval, status,
     *     address[] agreeVoters, address[] againstVoters }
     * @throws ContractException throw when contract exec exception
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
     * @throws ContractException throw when contract exec exception
     */
    public List<ProposalInfo> getProposalInfoList(BigInteger from, BigInteger to)
            throws ContractException {
        return committeeManager.getProposalManager().getProposalInfoList(from, to);
    }

    /**
     * get Committee info
     *
     * @return CommitteeInfo
     * @throws ContractException throw when contract exec exception
     */
    public CommitteeInfo getCommitteeInfo() throws ContractException {
        return new CommitteeInfo().fromTuple(committeeManager.getCommittee().getCommitteeInfo());
    }

    /**
     * check the account whether this account can deploy contract
     *
     * @param account the account to check
     * @return true or false
     * @throws ContractException throw when contract exec exception
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
     * @throws ContractException throw when contract exec exception
     */
    public Boolean checkMethodAuth(String contractAddress, byte[] func, String account)
            throws ContractException {
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
     * @throws ContractException throw when contract exec exception
     */
    public String getAdmin(String contractAddress) throws ContractException {
        return contractAuthPrecompiled.getAdmin(contractAddress);
    }

    /**
     * set a specific contract's method auth type, only contract admin can call it
     *
     * @param contractAddress the contract address to set auth
     * @param func interface func selector of contract, 4 bytes
     * @param authType white_list or black_list
     * @return set result, 0 is success
     * @throws ContractException throw when contract exec exception
     */
    public RetCode setMethodAuthType(String contractAddress, byte[] func, AuthType authType)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setMethodAuthType(
                        contractAddress, func, authType.getValue());
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt,
                tr -> contractAuthPrecompiled.getSetMethodAuthTypeOutput(tr).getValue1());
    }

    /**
     * async set a specific contract's method auth type, only contract admin can call it
     *
     * @param contractAddress the contract address to set auth
     * @param func interface func selector of contract, 4 bytes
     * @param authType white_list or black_list
     * @param callback callback when get receipt
     */
    public void asyncSetMethodAuthType(
            String contractAddress, byte[] func, AuthType authType, PrecompiledCallback callback) {
        contractAuthPrecompiled.setMethodAuthType(
                contractAddress,
                func,
                authType.getValue(),
                createTransactionCallback(
                        callback,
                        tr -> contractAuthPrecompiled.getSetMethodAuthTypeOutput(tr).getValue1()));
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
     * @throws ContractException throw when contract exec exception
     */
    public RetCode setMethodAuth(
            String contractAddress, byte[] func, String account, boolean isOpen)
            throws ContractException {
        TransactionReceipt receipt =
                isOpen
                        ? contractAuthPrecompiled.openMethodAuth(contractAddress, func, account)
                        : contractAuthPrecompiled.closeMethodAuth(contractAddress, func, account);
        return ReceiptParser.parseTransactionReceipt(
                receipt,
                tr ->
                        isOpen
                                ? contractAuthPrecompiled.getOpenMethodAuthOutput(tr).getValue1()
                                : contractAuthPrecompiled.getCloseMethodAuthOutput(tr).getValue1());
    }

    /**
     * async set a specific contract's method ACL, only contract admin can call it
     *
     * @param contractAddress the contract address to set acl
     * @param func interface func selector of contract, 4 bytes
     * @param account the account to set
     * @param isOpen if open, then white_list type is true, black_list is false; if close, then
     *     white_list type is false, black_list is true;
     * @param callback callback when get receipt
     */
    public void asyncSetMethodAuth(
            String contractAddress,
            byte[] func,
            String account,
            boolean isOpen,
            PrecompiledCallback callback) {
        if (isOpen) {
            contractAuthPrecompiled.openMethodAuth(
                    contractAddress,
                    func,
                    account,
                    createTransactionCallback(
                            callback,
                            tr -> contractAuthPrecompiled.getOpenMethodAuthOutput(tr).getValue1()));
        } else {
            contractAuthPrecompiled.closeMethodAuth(
                    contractAddress,
                    func,
                    account,
                    createTransactionCallback(
                            callback,
                            tr ->
                                    contractAuthPrecompiled
                                            .getCloseMethodAuthOutput(tr)
                                            .getValue1()));
        }
    }

    /**
     * set a contract status, freeze or normal, only contract manager can call
     *
     * @param contractAddress contract address
     * @param isFreeze is freeze or normal
     * @return 0 is success, otherwise is error
     * @throws ContractException throw when contract exec exception
     */
    public RetCode setContractStatus(String contractAddress, boolean isFreeze)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setContractStatus(contractAddress, isFreeze);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt,
                tr ->
                        contractAuthPrecompiled
                                .getSetContractStatusAddressBoolOutput(tr)
                                .getValue1());
    }

    /**
     * async set a contract status, freeze or normal, only contract manager can call
     *
     * @param contractAddress contract address
     * @param isFreeze is freeze or normal
     * @param callback callback when get receipt
     */
    public void asyncSetContractStatus(
            String contractAddress, boolean isFreeze, PrecompiledCallback callback) {
        contractAuthPrecompiled.setContractStatus(
                contractAddress,
                isFreeze,
                createTransactionCallback(
                        callback,
                        tr ->
                                contractAuthPrecompiled
                                        .getSetContractStatusAddressBoolOutput(tr)
                                        .getValue1()));
    }

    /**
     * set a contract status, only contract manager can call
     *
     * @param contractAddress contract address
     * @param status 0 - normal, 1 - freeze, 2 - abolish
     * @return 0 is success, otherwise is error
     * @throws ContractException throw when contract exec exception
     */
    public RetCode setContractStatus(String contractAddress, AccessStatus status)
            throws ContractException {
        long compatibilityVersion =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getProtocol()
                        .getCompatibilityVersion();
        PrecompiledVersionCheck.SET_CONTRACT_STATUS_VERSION.checkVersion(compatibilityVersion);
        TransactionReceipt transactionReceipt =
                contractAuthPrecompiled.setContractStatus(
                        contractAddress, status.getBigIntStatus());
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt,
                tr ->
                        contractAuthPrecompiled
                                .getSetContractStatusAddressUint8Output(tr)
                                .getValue1());
    }

    /**
     * async set a contract status, only contract manager can call
     *
     * @param contractAddress contract address
     * @param status 0 - normal, 1 - freeze, 2 - abolish
     * @param callback callback when get receipt
     */
    public void asyncSetContractStatus(
            String contractAddress, AccessStatus status, PrecompiledCallback callback) {
        client.getGroupInfoAsync(
                new RespCallback<BcosGroupInfo>() {
                    @Override
                    public void onResponse(BcosGroupInfo bcosGroupInfo) {
                        long compatibilityVersion =
                                bcosGroupInfo
                                        .getResult()
                                        .getNodeList()
                                        .get(0)
                                        .getProtocol()
                                        .getCompatibilityVersion();
                        try {
                            PrecompiledVersionCheck.SET_CONTRACT_STATUS_VERSION.checkVersion(
                                    compatibilityVersion);
                            contractAuthPrecompiled.setContractStatus(
                                    contractAddress,
                                    status.getBigIntStatus(),
                                    createTransactionCallback(
                                            callback,
                                            tr ->
                                                    contractAuthPrecompiled
                                                            .getSetContractStatusAddressBoolOutput(
                                                                    tr)
                                                            .getValue1()));
                        } catch (ContractException e) {
                            callback.onResponse(new RetCode(e.getErrorCode(), e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onResponse(
                                new RetCode(
                                        errorResponse.getErrorCode(),
                                        errorResponse.getErrorMessage()));
                    }
                });
    }

    /**
     * check contract is available, if normal then return true
     *
     * @param contractAddress contract address
     * @return if true, then this contract can be called
     * @throws ContractException throw when contract exec exception
     */
    public Boolean contractAvailable(String contractAddress) throws ContractException {
        return contractAuthPrecompiled.contractAvailable(contractAddress);
    }

    /**
     * Set account status, only governor can call it. And account to be set should not in governor
     * list, if account not exist in chain, it will create it by default
     *
     * @param account account address
     * @param status account status
     * @return proposal ID
     * @throws ContractException throw when contract exec exception
     */
    public RetCode setAccountStatus(String account, AccessStatus status) throws ContractException {
        TransactionReceipt receipt =
                accountManager.setAccountStatus(account, status.getBigIntStatus());
        if (receipt.getStatus() != TransactionReceiptStatus.Success.code) {
            ReceiptParser.getErrorStatus(receipt);
        }
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> accountManager.getSetAccountStatusOutput(tr).getValue1());
    }

    /**
     * async set account status, only governor can call it. And account to be set should not in
     * governor list, if account not exist in chain, it will create it by default
     *
     * @param account account address
     * @param status account status
     */
    public void asyncSetAccountStatus(
            String account, AccessStatus status, PrecompiledCallback callback) {
        accountManager.setAccountStatus(
                account,
                status.getBigIntStatus(),
                createTransactionCallback(
                        callback, tr -> accountManager.getSetAccountStatusOutput(tr).getValue1()));
    }

    /**
     * check account is available, if normal then return true
     *
     * @param accountAddress account address
     * @return if true, then this account can be used
     * @throws ContractException throw when contract exec exception
     */
    public Boolean accountAvailable(String accountAddress) throws ContractException {
        BigInteger accountStatus = accountManager.getAccountStatus(accountAddress);
        return AccessStatus.getAccessStatus(accountStatus.intValue()) == AccessStatus.Normal;
    }

    /**
     * get proposal count
     *
     * @return count
     * @throws ContractException throw when contract exec exception
     */
    public BigInteger proposalCount() throws ContractException {
        return committeeManager.getProposalManager()._proposalCount();
    }

    /**
     * init committee system for old version chain which not open auth check NOTE: this method only
     * can be used when chain version >= 3.3.0
     *
     * @param admin committee first admin
     * @return return code
     * @throws ContractException throw when check failed or contract exec exception
     */
    public RetCode initAuth(String admin) throws ContractException {
        long compatibilityVersion =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getProtocol()
                        .getCompatibilityVersion();
        PrecompiledVersionCheck.INIT_AUTH_VERSION.checkVersion(compatibilityVersion);
        TransactionReceipt receipt = contractAuthPrecompiled.initAuth(admin);
        if (receipt.getStatus() != TransactionReceiptStatus.Success.code) {
            ReceiptParser.getErrorStatus(receipt);
        }
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> contractAuthPrecompiled.getInitAuthOutput(tr).getValue1());
    }

    private boolean existsInNodeList(String nodeId) {
        List<String> nodeIdList = client.getGroupPeers().getGroupPeers();
        return nodeIdList.contains(nodeId);
    }

    private TransactionCallback createTransactionCallback(
            PrecompiledCallback callback, Function<TransactionReceipt, BigInteger> resultCaller) {
        return new TransactionCallback() {
            @Override
            public void onResponse(TransactionReceipt receipt) {
                RetCode retCode;
                try {
                    retCode = getRetCode(receipt, resultCaller);
                } catch (ContractException e) {
                    retCode = new RetCode(e.getErrorCode(), e.getMessage());
                    retCode.setTransactionReceipt(receipt);
                }
                callback.onResponse(retCode);
            }
        };
    }

    private RetCode getRetCode(
            TransactionReceipt transactionReceipt,
            Function<TransactionReceipt, BigInteger> resultCaller)
            throws ContractException {
        int status = transactionReceipt.getStatus();
        if (status != 0) {
            ReceiptParser.getErrorStatus(transactionReceipt);
        }
        BigInteger result = resultCaller.apply(transactionReceipt);
        return PrecompiledRetCode.getPrecompiledResponse(
                result.intValue(), transactionReceipt.getMessage());
    }

    private void getExecEvent(TransactionReceipt tr, BigInteger proposalId)
            throws ContractException {
        List<CommitteeManager.ExecResultEventResponse> execResultEvents =
                committeeManager.getExecResultEvents(tr);
        if (!execResultEvents.isEmpty()) {
            BigInteger execResultParam0 = execResultEvents.get(0).execResultParam0;
            if (!BigInteger.ZERO.equals(execResultParam0)) {
                RetCode precompiledResponse =
                        PrecompiledRetCode.getPrecompiledResponse(execResultParam0.intValue(), "");
                throw new ContractException(
                        "Exec proposal finished with error occurs, proposalId: "
                                + proposalId
                                + ", exec error msg: "
                                + precompiledResponse.getMessage(),
                        precompiledResponse.getCode());
            }
        }
    }
}
