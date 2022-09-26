package org.fisco.bcos.sdk.v3.contract.auth.po;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;

public class ProposalInfo extends DynamicStruct {
    private String resourceId;
    private String proposer;
    private int proposalType;
    private BigInteger blockNumberInterval;
    private int status;
    private List<String> agreeVoters;
    private List<String> againstVoters;

    public ProposalInfo() {
        super(
                new Address(""),
                new Uint8(0),
                new Uint256(0),
                new Uint8(0),
                new DynamicArray<>(Address.class),
                new DynamicArray<>(Address.class));
    }

    public ProposalInfo(
            Address resourceId,
            Address proposer,
            Uint8 proposalType,
            Uint256 blockNumberInterval,
            Uint8 status,
            DynamicArray<Address> agreeVoters,
            DynamicArray<Address> againstVoters) {
        super(
                resourceId,
                proposer,
                proposalType,
                blockNumberInterval,
                status,
                agreeVoters,
                againstVoters);
        this.resourceId = resourceId.getValue();
        this.proposer = proposer.getValue();
        this.proposalType = proposalType.getValue().intValue();
        this.blockNumberInterval = blockNumberInterval.getValue();
        this.status = status.getValue().intValue();
        this.agreeVoters =
                agreeVoters.getValue().stream().map(Address::getValue).collect(Collectors.toList());
        this.againstVoters =
                againstVoters.getValue().stream()
                        .map(Address::getValue)
                        .collect(Collectors.toList());
    }

    public ProposalInfo(
            String resourceId,
            String proposer,
            int proposalType,
            BigInteger blockNumberInterval,
            int status,
            List<String> agreeVoters,
            List<String> againstVoters) {
        super(
                new Address(resourceId),
                new Address(proposer),
                new Uint8(proposalType),
                new Uint256(blockNumberInterval),
                new Uint8(status),
                new DynamicArray<>(
                        Address.class,
                        agreeVoters.stream().map(Address::new).collect(Collectors.toList())),
                new DynamicArray<>(
                        Address.class,
                        againstVoters.stream().map(Address::new).collect(Collectors.toList())));
        this.resourceId = resourceId;
        this.proposer = proposer;
        this.proposalType = proposalType;
        this.blockNumberInterval = blockNumberInterval;
        this.status = status;
        this.agreeVoters = agreeVoters;
        this.againstVoters = againstVoters;
    }

    public ProposalInfo(
            Tuple7<String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>>
                    tuple7) {
        super(
                new Address(tuple7.getValue1()),
                new Address(tuple7.getValue2()),
                new Uint8(tuple7.getValue3()),
                new Uint256(tuple7.getValue4()),
                new Uint8(tuple7.getValue5()),
                new DynamicArray<>(
                        Address.class,
                        tuple7.getValue6().stream().map(Address::new).collect(Collectors.toList())),
                new DynamicArray<>(
                        Address.class,
                        tuple7.getValue7().stream()
                                .map(Address::new)
                                .collect(Collectors.toList())));

        this.resourceId = tuple7.getValue1();
        this.proposer = tuple7.getValue2();
        this.proposalType = tuple7.getValue3().intValue();
        this.blockNumberInterval = tuple7.getValue4();
        this.status = tuple7.getValue5().intValue();
        this.agreeVoters = tuple7.getValue6();
        this.againstVoters = tuple7.getValue7();
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public int getProposalType() {
        return proposalType;
    }

    public String getProposalTypeString() {
        return ProposalType.fromInt(proposalType).getValue();
    }

    public void setProposalType(int proposalType) {
        this.proposalType = proposalType;
    }

    public BigInteger getBlockNumberInterval() {
        return blockNumberInterval;
    }

    public void setBlockNumberInterval(BigInteger blockNumberInterval) {
        this.blockNumberInterval = blockNumberInterval;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusString() {
        return ProposalStatus.fromInt(status).getValue();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getAgreeVoters() {
        return agreeVoters;
    }

    public void setAgreeVoters(List<String> agreeVoters) {
        this.agreeVoters = agreeVoters;
    }

    public List<String> getAgainstVoters() {
        return againstVoters;
    }

    public void setAgainstVoters(List<String> againstVoters) {
        this.againstVoters = againstVoters;
    }

    @Override
    public String toString() {
        return "ProposalInfo{"
                + "resourceId='"
                + resourceId
                + '\''
                + ", proposer='"
                + proposer
                + '\''
                + ", proposalType="
                + ProposalType.fromInt(proposalType).getValue()
                + ", blockNumberInterval="
                + blockNumberInterval
                + ", status="
                + ProposalStatus.fromInt(status).getValue()
                + ", agreeVoters="
                + agreeVoters
                + ", againstVoters="
                + againstVoters
                + '}';
    }
}
