package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class CommitteeManager extends Contract {
    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"initGovernors\",\"type\":\"address[]\"},{\"internalType\":\"uint32[]\",\"name\":\"weights\",\"type\":\"uint32[]\"},{\"internalType\":\"uint8\",\"name\":\"participatesRate\",\"type\":\"uint8\"},{\"internalType\":\"uint8\",\"name\":\"winRate\",\"type\":\"uint8\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"_committee\",\"outputs\":[{\"internalType\":\"contract Committee\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[408688007,2076726550],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"_proposalMgr\",\"outputs\":[{\"internalType\":\"contract ProposalManager\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[4134927786,1757083837],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"openFlag\",\"type\":\"bool\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createModifyDeployAuthProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[1805941004,212904153],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"newAdmin\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createResetAdminProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[1953886223,2452322566],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"node\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createRmNodeProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[1694573954,530010165],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"node\",\"type\":\"string\"},{\"internalType\":\"uint32\",\"name\":\"weight\",\"type\":\"uint32\"},{\"internalType\":\"bool\",\"name\":\"addFlag\",\"type\":\"bool\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createSetConsensusWeightProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[2654949198,1568829248],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"deployAuthType\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createSetDeployAuthTypeProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[1631729139,3724555094],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"participatesRate\",\"type\":\"uint8\"},{\"internalType\":\"uint8\",\"name\":\"winRate\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createSetRateProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[66163033,3419039597],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createSetSysConfigProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[2049250093,3487605356],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"uint32\",\"name\":\"weight\",\"type\":\"uint32\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createUpdateGovernorProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[842330342,3532932153],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"newAddr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"blockNumberInterval\",\"type\":\"uint256\"}],\"name\":\"createUpgradeVoteComputerProposal\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"currentproposalId\",\"type\":\"uint256\"}],\"selector\":[1914877096,1149847126],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"getProposalType\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[3648585658,2257791149],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isGovernor\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3828711864,3052548661],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"}],\"name\":\"revokeProposal\",\"outputs\":[],\"selector\":[2242289809,3123570540],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"proposalId\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"agree\",\"type\":\"bool\"}],\"name\":\"voteProposal\",\"outputs\":[],\"selector\":[3170605921,1532806966],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC__COMMITTEE = "_committee";

    public static final String FUNC__PROPOSALMGR = "_proposalMgr";

    public static final String FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL =
            "createModifyDeployAuthProposal";

    public static final String FUNC_CREATERESETADMINPROPOSAL = "createResetAdminProposal";

    public static final String FUNC_CREATERMNODEPROPOSAL = "createRmNodeProposal";

    public static final String FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL =
            "createSetConsensusWeightProposal";

    public static final String FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL =
            "createSetDeployAuthTypeProposal";

    public static final String FUNC_CREATESETRATEPROPOSAL = "createSetRateProposal";

    public static final String FUNC_CREATESETSYSCONFIGPROPOSAL = "createSetSysConfigProposal";

    public static final String FUNC_CREATEUPDATEGOVERNORPROPOSAL = "createUpdateGovernorProposal";

    public static final String FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL =
            "createUpgradeVoteComputerProposal";

    public static final String FUNC_GETPROPOSALTYPE = "getProposalType";

    public static final String FUNC_ISGOVERNOR = "isGovernor";

    public static final String FUNC_REVOKEPROPOSAL = "revokeProposal";

    public static final String FUNC_VOTEPROPOSAL = "voteProposal";

    private Committee committee = null;

    private ProposalManager proposalManager = null;

    protected CommitteeManager(String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public String _committee() throws ContractException {
        final Function function =
                new Function(
                        FUNC__COMMITTEE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String _proposalMgr() throws ContractException {
        final Function function =
                new Function(
                        FUNC__PROPOSALMGR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Committee getCommittee() throws ContractException {
        if (committee == null)
            committee =
                    Committee.load(
                            _committee(), client, client.getCryptoSuite().getCryptoKeyPair());
        return committee;
    }

    public ProposalManager getProposalManager() throws ContractException {
        if (proposalManager == null)
            proposalManager =
                    ProposalManager.load(
                            _proposalMgr(), client, client.getCryptoSuite().getCryptoKeyPair());
        return proposalManager;
    }

    public TransactionReceipt createModifyDeployAuthProposal(
            String account, Boolean openFlag, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Bool(openFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createModifyDeployAuthProposal(
            String account,
            Boolean openFlag,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Bool(openFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateModifyDeployAuthProposal(
            String account, Boolean openFlag, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Bool(openFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, Boolean, BigInteger> getCreateModifyDeployAuthProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bool>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, Boolean, BigInteger>(
                (String) results.get(0).getValue(),
                (Boolean) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateModifyDeployAuthProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createResetAdminProposal(
            String newAdmin, String contractAddr, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATERESETADMINPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(newAdmin),
                                new Address(contractAddr),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createResetAdminProposal(
            String newAdmin,
            String contractAddr,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATERESETADMINPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(newAdmin),
                                new Address(contractAddr),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateResetAdminProposal(
            String newAdmin, String contractAddr, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATERESETADMINPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(newAdmin),
                                new Address(contractAddr),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, BigInteger> getCreateResetAdminProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATERESETADMINPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateResetAdminProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATERESETADMINPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createRmNodeProposal(String node, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATERMNODEPROPOSAL,
                        Arrays.<Type>asList(new Utf8String(node), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createRmNodeProposal(
            String node, BigInteger blockNumberInterval, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATERMNODEPROPOSAL,
                        Arrays.<Type>asList(new Utf8String(node), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateRmNodeProposal(
            String node, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATERMNODEPROPOSAL,
                        Arrays.<Type>asList(new Utf8String(node), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getCreateRmNodeProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATERMNODEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getCreateRmNodeProposalOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATERMNODEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createSetConsensusWeightProposal(
            String node, BigInteger weight, Boolean addFlag, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(node),
                                new Uint32(weight),
                                new Bool(addFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createSetConsensusWeightProposal(
            String node,
            BigInteger weight,
            Boolean addFlag,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(node),
                                new Uint32(weight),
                                new Bool(addFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateSetConsensusWeightProposal(
            String node, BigInteger weight, Boolean addFlag, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(node),
                                new Uint32(weight),
                                new Bool(addFlag),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, BigInteger, Boolean, BigInteger> getCreateSetConsensusWeightProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint32>() {},
                                new TypeReference<Bool>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, BigInteger, Boolean, BigInteger>(
                (String) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (Boolean) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getCreateSetConsensusWeightProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createSetDeployAuthTypeProposal(
            BigInteger deployAuthType, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(deployAuthType), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createSetDeployAuthTypeProposal(
            BigInteger deployAuthType,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(deployAuthType), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateSetDeployAuthTypeProposal(
            BigInteger deployAuthType, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(deployAuthType), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, BigInteger> getCreateSetDeployAuthTypeProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getCreateSetDeployAuthTypeProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createSetRateProposal(
            BigInteger participatesRate, BigInteger winRate, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETRATEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(participatesRate),
                                new Uint8(winRate),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createSetRateProposal(
            BigInteger participatesRate,
            BigInteger winRate,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATESETRATEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(participatesRate),
                                new Uint8(winRate),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateSetRateProposal(
            BigInteger participatesRate, BigInteger winRate, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETRATEPROPOSAL,
                        Arrays.<Type>asList(
                                new Uint8(participatesRate),
                                new Uint8(winRate),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getCreateSetRateProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATESETRATEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateSetRateProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATESETRATEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createSetSysConfigProposal(
            String key, String value, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETSYSCONFIGPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new Utf8String(value),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createSetSysConfigProposal(
            String key,
            String value,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATESETSYSCONFIGPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new Utf8String(value),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateSetSysConfigProposal(
            String key, String value, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATESETSYSCONFIGPROPOSAL,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new Utf8String(value),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, BigInteger> getCreateSetSysConfigProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATESETSYSCONFIGPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateSetSysConfigProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATESETSYSCONFIGPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createUpdateGovernorProposal(
            String account, BigInteger weight, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Uint32(weight),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String createUpdateGovernorProposal(
            String account,
            BigInteger weight,
            BigInteger blockNumberInterval,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Uint32(weight),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateUpdateGovernorProposal(
            String account, BigInteger weight, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        Arrays.<Type>asList(
                                new Address(account),
                                new Uint32(weight),
                                new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, BigInteger, BigInteger> getCreateUpdateGovernorProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Uint32>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, BigInteger, BigInteger>(
                (String) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateUpdateGovernorProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATEUPDATEGOVERNORPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createUpgradeVoteComputerProposal(
            String newAddr, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
                        Arrays.<Type>asList(new Address(newAddr), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String createUpgradeVoteComputerProposal(
            String newAddr, BigInteger blockNumberInterval, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
                        Arrays.<Type>asList(new Address(newAddr), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateUpgradeVoteComputerProposal(
            String newAddr, BigInteger blockNumberInterval) {
        final Function function =
                new Function(
                        FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
                        Arrays.<Type>asList(new Address(newAddr), new Uint256(blockNumberInterval)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getCreateUpgradeVoteComputerProposalInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getCreateUpgradeVoteComputerProposalOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public BigInteger getProposalType(BigInteger proposalId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETPROPOSALTYPE,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Boolean isGovernor(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_ISGOVERNOR,
                        Arrays.<Type>asList(new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt revokeProposal(BigInteger proposalId) {
        final Function function =
                new Function(
                        FUNC_REVOKEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String revokeProposal(BigInteger proposalId, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevokeProposal(BigInteger proposalId) {
        final Function function =
                new Function(
                        FUNC_REVOKEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getRevokeProposalInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt voteProposal(BigInteger proposalId, Boolean agree) {
        final Function function =
                new Function(
                        FUNC_VOTEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId), new Bool(agree)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String voteProposal(BigInteger proposalId, Boolean agree, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_VOTEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId), new Bool(agree)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForVoteProposal(BigInteger proposalId, Boolean agree) {
        final Function function =
                new Function(
                        FUNC_VOTEPROPOSAL,
                        Arrays.<Type>asList(new Uint256(proposalId), new Bool(agree)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, Boolean> getVoteProposalInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_VOTEPROPOSAL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, Boolean>(
                (BigInteger) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public static CommitteeManager load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new CommitteeManager(contractAddress, client, credential);
    }
}
