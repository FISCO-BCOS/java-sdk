package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class ProposalManager extends Contract {

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"committeeMgrAddress\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"committeeAddress\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"_owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2998794875,686363785],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"_proposalCount\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1864959180,1659366480],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":4,\"value\":[0]},{\"kind\":3,\"slot\":4,\"value\":[1]}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"_proposalIndex\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[482368700,3565048514],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_proposals\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"resourceId\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"proposer\",\"type\":\"address\"},{\"internalType\":\"uint8\",\"name\":\"proposalType\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"},{\"internalType\":\"uint8\",\"name\":\"status\",\"type\":\"uint8\"}],\"selector\":[172574784,44902092],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"_voteComputer\",\"outputs\":[{\"internalType\":\"contract VoteComputer\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[4244118548,3233112258],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3445432600,1845720788],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[2]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"proposer\",\"type\":\"address\"},{\"internalType\":\"uint8\",\"name\":\"proposalType\",\"type\":\"uint8\"},{\"internalType\":\"address\",\"name\":\"resourceId\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"create\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[741955326,960952134],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"proposalType\",\"type\":\"uint8\"},{\"internalType\":\"address\",\"name\":\"resourceId\",\"type\":\"address\"}],\"name\":\"getIdByTypeAndResourceId\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1831062872,2256801033],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalInfo\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"resourceId\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"proposer\",\"type\":\"address\"},{\"internalType\":\"uint8\",\"name\":\"proposalType\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"},{\"internalType\":\"uint8\",\"name\":\"status\",\"type\":\"uint8\"},{\"internalType\":\"address[]\",\"name\":\"agreeVoters\",\"type\":\"address[]\"},{\"internalType\":\"address[]\",\"name\":\"againstVoters\",\"type\":\"address[]\"}],\"selector\":[3163569336,3968098372],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"from\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"to\",\"type\":\"uint256\"}],\"name\":\"getProposalInfoList\",\"outputs\":[{\"components\":[{\"internalType\":\"address\",\"name\":\"resourceId\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"proposer\",\"type\":\"address\"},{\"internalType\":\"uint8\",\"name\":\"proposalType\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"},{\"internalType\":\"uint8\",\"name\":\"status\",\"type\":\"uint8\"},{\"internalType\":\"address[]\",\"name\":\"agreeVoters\",\"type\":\"address[]\"},{\"internalType\":\"address[]\",\"name\":\"againstVoters\",\"type\":\"address[]\"}],\"internalType\":\"struct ProposalManager.ProposalInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[3722594531,2551552949],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalStatus\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[1075336119,2313929655],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"refreshProposalStatus\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[433901694,3852368692],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"voterAddress\",\"type\":\"address\"}],\"name\":\"revoke\",\"outputs\":[],\"selector\":[550589658,2497247343],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"selector\":[330252341,86518896],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"setVoteComputer\",\"outputs\":[],\"selector\":[688637847,3971917500],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"agree\",\"type\":\"bool\"},{\"internalType\":\"address\",\"name\":\"voterAddress\",\"type\":\"address\"}],\"name\":\"vote\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[828171746,1756838345],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC__COMMITTEE = "_committee";

    public static final String FUNC__OWNER = "_owner";

    public static final String FUNC__PROPOSALCOUNT = "_proposalCount";

    public static final String FUNC__PROPOSALINDEX = "_proposalIndex";

    public static final String FUNC__PROPOSALS = "_proposals";

    public static final String FUNC__VOTECOMPUTER = "_voteComputer";

    public static final String FUNC_AUTH = "auth";

    public static final String FUNC_CREATE = "create";

    public static final String FUNC_GETIDBYTYPEANDRESOURCEID = "getIdByTypeAndResourceId";

    public static final String FUNC_GETPROPOSALINFO = "getProposalInfo";

    public static final String FUNC_GETPROPOSALINFOLIST = "getProposalInfoList";

    public static final String FUNC_GETPROPOSALSTATUS = "getProposalStatus";

    public static final String FUNC_REFRESHPROPOSALSTATUS = "refreshProposalStatus";

    public static final String FUNC_REVOKE = "revoke";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final String FUNC_SETVOTECOMPUTER = "setVoteComputer";

    public static final String FUNC_VOTE = "vote";

    protected ProposalManager(String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public String _owner() throws ContractException {
        final Function function =
                new Function(
                        FUNC__OWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public BigInteger _proposalCount() throws ContractException {
        final Function function =
                new Function(
                        FUNC__PROPOSALCOUNT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger _proposalIndex(BigInteger param0, String param1) throws ContractException {
        final Function function =
                new Function(
                        FUNC__PROPOSALINDEX,
                        Arrays.<Type>asList(new Uint8(param0), new Address(param1)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple5<String, String, BigInteger, BigInteger, BigInteger> _proposals(BigInteger param0)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC__PROPOSALS,
                        Arrays.<Type>asList(new Uint256(param0)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<Uint8>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<String, String, BigInteger, BigInteger, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public String _voteComputer() throws ContractException {
        final Function function =
                new Function(
                        FUNC__VOTECOMPUTER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Boolean auth(String src) throws ContractException {
        final Function function =
                new Function(
                        FUNC_AUTH,
                        Arrays.<Type>asList(new Address(src)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt create(
            String proposer,
            BigInteger proposalType,
            String resourceId,
            BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(
                                new Address(proposer),
                                new Uint8(proposalType),
                                new Address(resourceId),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String create(
            String proposer,
            BigInteger proposalType,
            String resourceId,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(
                                new Address(proposer),
                                new Uint8(proposalType),
                                new Address(resourceId),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreate(
            String proposer,
            BigInteger proposalType,
            String resourceId,
            BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(
                                new Address(proposer),
                                new Uint8(proposalType),
                                new Address(resourceId),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, BigInteger, String, BigInteger> getCreateInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, BigInteger, String, BigInteger>(
                (String) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getCreateOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public BigInteger getIdByTypeAndResourceId(BigInteger proposalType, String resourceId)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETIDBYTYPEANDRESOURCEID,
                        Arrays.<Type>asList(new Uint8(proposalType), new Address(resourceId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple7<String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>>
            getProposalInfo(BigInteger proposalId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETPROPOSALINFO,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<DynamicArray<Address>>() {},
                                new TypeReference<DynamicArray<Address>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple7<
                String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue(),
                convertToNative((List<Address>) results.get(5).getValue()),
                convertToNative((List<Address>) results.get(6).getValue()));
    }

    public List<ProposalInfo> getProposalInfoList(BigInteger from, BigInteger to)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETPROPOSALINFOLIST,
                        Arrays.<Type>asList(new Uint256(from), new Uint256(to)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<ProposalInfo>>() {}));
        return executeCallWithSingleValueReturn(function, List.class);
    }

    public BigInteger getProposalStatus(BigInteger proposalId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETPROPOSALSTATUS,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public TransactionReceipt refreshProposalStatus(BigInteger proposalId) {
        final Function function =
                new Function(
                        FUNC_REFRESHPROPOSALSTATUS,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String refreshProposalStatus(BigInteger proposalId, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REFRESHPROPOSALSTATUS,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRefreshProposalStatus(BigInteger proposalId) {
        final Function function =
                new Function(
                        FUNC_REFRESHPROPOSALSTATUS,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getRefreshProposalStatusInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REFRESHPROPOSALSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRefreshProposalStatusOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REFRESHPROPOSALSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt revoke(BigInteger proposalId, String voterAddress) {
        final Function function =
                new Function(
                        FUNC_REVOKE,
                        Arrays.<Type>asList(new Uint256(proposalId), new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String revoke(BigInteger proposalId, String voterAddress, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKE,
                        Arrays.<Type>asList(new Uint256(proposalId), new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevoke(BigInteger proposalId, String voterAddress) {
        final Function function =
                new Function(
                        FUNC_REVOKE,
                        Arrays.<Type>asList(new Uint256(proposalId), new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, String> getRevokeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, String>(
                (BigInteger) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public TransactionReceipt setOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setOwner(String owner, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getSetOwnerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt setVoteComputer(String addr) {
        final Function function =
                new Function(
                        FUNC_SETVOTECOMPUTER,
                        Arrays.<Type>asList(new Address(addr)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String setVoteComputer(String addr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETVOTECOMPUTER,
                        Arrays.<Type>asList(new Address(addr)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetVoteComputer(String addr) {
        final Function function =
                new Function(
                        FUNC_SETVOTECOMPUTER,
                        Arrays.<Type>asList(new Address(addr)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<String> getSetVoteComputerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETVOTECOMPUTER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt vote(BigInteger proposalId, Boolean agree, String voterAddress) {
        final Function function =
                new Function(
                        FUNC_VOTE,
                        Arrays.<Type>asList(
                                new Uint256(proposalId),
                                new Bool(agree),
                                new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String vote(
            BigInteger proposalId,
            Boolean agree,
            String voterAddress,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_VOTE,
                        Arrays.<Type>asList(
                                new Uint256(proposalId),
                                new Bool(agree),
                                new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForVote(
            BigInteger proposalId, Boolean agree, String voterAddress) {
        final Function function =
                new Function(
                        FUNC_VOTE,
                        Arrays.<Type>asList(
                                new Uint256(proposalId),
                                new Bool(agree),
                                new Address(voterAddress)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, Boolean, String> getVoteInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_VOTE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {},
                                new TypeReference<Bool>() {},
                                new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, Boolean, String>(
                (BigInteger) results.get(0).getValue(),
                (Boolean) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getVoteOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_VOTE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ProposalManager load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ProposalManager(contractAddress, client, credential);
    }
}
