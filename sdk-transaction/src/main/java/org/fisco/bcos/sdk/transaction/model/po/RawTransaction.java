package org.fisco.bcos.sdk.transaction.model.po;

import java.io.Serializable;
import java.math.BigInteger;
import org.fisco.bcos.sdk.utils.Numeric;

/**
 * Transaction class used for signing transactions locally.<br>
 * For the specification, refer to p4 of the <a href="http://gavwood.com/paper.pdf">yellow
 * paper</a>.
 */
public class RawTransaction implements Serializable {

    private static final long serialVersionUID = -5580814755985097996L;
    private BigInteger randomid;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private BigInteger blockLimit;
    private String to;
    private BigInteger value;
    private String data;
    private BigInteger fiscoChainId;
    private BigInteger groupId;
    private String extraData;
    // private BigInteger version = TransactionConstant.version;
    // TODO
    private BigInteger version;

    protected RawTransaction(
            BigInteger randomid,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String to,
            BigInteger value,
            String data,
            BigInteger fiscoChainId,
            BigInteger groupId,
            String extraData) {
        this.randomid = randomid;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.blockLimit = blockLimit;
        this.fiscoChainId = fiscoChainId;
        this.groupId = groupId;
        this.extraData = extraData;
        this.to = to;
        this.value = value;
        if (data != null) {
            this.data = Numeric.cleanHexPrefix(data);
        }
    }

    public static RawTransaction createContractTransaction(
            BigInteger randomid,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            BigInteger value,
            String data,
            BigInteger chainId,
            BigInteger groupId,
            String extraData) {

        return new RawTransaction(
                randomid,
                gasPrice,
                gasLimit,
                blockLimit,
                "",
                value,
                data,
                chainId,
                groupId,
                extraData);
    }

    public static RawTransaction createTransaction(
            BigInteger randomid,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String to,
            BigInteger value,
            String data,
            BigInteger chainId,
            BigInteger groupId,
            String extraData) {

        return new RawTransaction(
                randomid,
                gasPrice,
                gasLimit,
                blockLimit,
                to,
                value,
                data,
                chainId,
                groupId,
                extraData);
    }

    public BigInteger getRandomid() {
        return randomid;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public BigInteger getBlockLimit() {
        return blockLimit;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public BigInteger getVersion() {
        return version;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public BigInteger getFiscoChainId() {
        return fiscoChainId;
    }

    public void setFiscoChainId(BigInteger fiscoChainId) {
        this.fiscoChainId = fiscoChainId;
    }

    @Override
    public String toString() {
        return "RawTransaction{"
                + "randomid="
                + randomid
                + ", gasPrice="
                + gasPrice
                + ", gasLimit="
                + gasLimit
                + ", blockLimit="
                + blockLimit
                + ", to='"
                + to
                + '\''
                + ", value="
                + value
                + ", data='"
                + data
                + '\''
                + ", fiscoChainId="
                + fiscoChainId
                + ", groupId="
                + groupId
                + ", extraData='"
                + extraData
                + '\''
                + ", version="
                + version
                + '}';
    }
}
