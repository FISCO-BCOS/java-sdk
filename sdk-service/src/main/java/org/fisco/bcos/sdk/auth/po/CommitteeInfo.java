package org.fisco.bcos.sdk.auth.po;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple4;

public class CommitteeInfo {
    private List<String> governorList;
    private List<BigInteger> weightList;
    private int participatesRate;
    private int winRate;

    public CommitteeInfo fromTuple(
            Tuple4<BigInteger, BigInteger, List<String>, List<BigInteger>> tuple) {
        this.governorList = tuple.getValue3();
        this.weightList = tuple.getValue4();
        this.participatesRate = tuple.getValue1().intValue();
        this.winRate = tuple.getValue2().intValue();
        return this;
    }

    public List<String> getGovernorList() {
        return governorList;
    }

    public void setGovernorList(List<String> governorList) {
        this.governorList = governorList;
    }

    public List<BigInteger> getWeightList() {
        return weightList;
    }

    public void setWeightList(List<BigInteger> weightList) {
        this.weightList = weightList;
    }

    public int getParticipatesRate() {
        return participatesRate;
    }

    public void setParticipatesRate(int participatesRate) {
        this.participatesRate = participatesRate;
    }

    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }
}
