package org.fisco.bcos.sdk.v3.contract.auth.po;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommitteeInfo {
    private final Logger logger = LoggerFactory.getLogger(CommitteeInfo.class);

    private List<GovernorInfo> governorList = new ArrayList<>();
    private int participatesRate;
    private int winRate;

    public CommitteeInfo fromTuple(
            Tuple4<BigInteger, BigInteger, List<String>, List<BigInteger>> tuple) {
        List<String> governorNameList = tuple.getValue3();
        List<BigInteger> weightList = tuple.getValue4();
        try {
            for (int i = 0; i < governorNameList.size(); i++) {
                this.governorList.add(new GovernorInfo(governorNameList.get(i), weightList.get(i)));
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error(
                    "Governor list size not fit with weight list, you should check committee info. e:",
                    e);
        }
        this.participatesRate = tuple.getValue1().intValue();
        this.winRate = tuple.getValue2().intValue();
        return this;
    }

    public List<GovernorInfo> getGovernorList() {
        return governorList;
    }

    public void setGovernorList(List<GovernorInfo> governorList) {
        this.governorList = governorList;
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

    @Override
    public String toString() {
        return "CommitteeInfo{"
                + "governorList="
                + governorList
                + ", participatesRate="
                + participatesRate
                + ", winRate="
                + winRate
                + '}';
    }
}
