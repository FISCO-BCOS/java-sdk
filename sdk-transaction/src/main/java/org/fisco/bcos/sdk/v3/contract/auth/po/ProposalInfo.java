package org.fisco.bcos.sdk.v3.contract.auth.po;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;

public class ProposalInfo {
    private String resourceId;
    private String proposer;
    private int proposalType;
    private long blockNumberInterval;
    private int status;
    private List<String> agreeVoters;
    private List<String> againstVoters;

    public ProposalInfo fromTuple(
            Tuple7<String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>>
                    tuple7) {
        this.resourceId = tuple7.getValue1();
        this.proposer = tuple7.getValue2();
        this.proposalType = tuple7.getValue3().intValue();
        this.blockNumberInterval = tuple7.getValue4().longValue();
        this.status = tuple7.getValue5().intValue();
        this.agreeVoters = tuple7.getValue6();
        this.againstVoters = tuple7.getValue7();
        return this;
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

    public long getBlockNumberInterval() {
        return blockNumberInterval;
    }

    public void setBlockNumberInterval(long blockNumberInterval) {
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
