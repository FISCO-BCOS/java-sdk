package org.fisco.bcos.sdk.v3.client.protocol.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * Filter implementation as per <aFilterFilter
 * href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_newfilter">docs</a>.
 */
public class EthFilter extends Filter<EthFilter> {
    private DefaultBlockParameter fromBlock; // optional, params - defaults to latest for both
    private DefaultBlockParameter toBlock;
    private String blockHash; // optional, cannot be used together with fromBlock/toBlock
    private List<String> address; // spec. implies this can be single address as string or list

    public EthFilter() {
        super();
    }

    public EthFilter(DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock) {
        super();
        this.fromBlock = fromBlock;
        this.toBlock = toBlock;
    }

    public EthFilter(
            DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock, List<String> address) {
        super();
        this.fromBlock = fromBlock;
        this.toBlock = toBlock;
        this.address = address;
    }

    public EthFilter(
            DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock, String address) {
        this(fromBlock, toBlock, Collections.singletonList(address));
    }

    public EthFilter(String blockHash) {
        super();
        this.blockHash = blockHash;
    }

    public EthFilter(String blockHash, String address) {
        this(null, null, Collections.singletonList(address));
        this.blockHash = blockHash;
    }

    public EthFilter(List<String> address, List<FilterTopic> topics) {
        super(topics);
        this.address = address;
    }

    public EthFilter setFromBlock(BigInteger from) {
        fromBlock = DefaultBlockParameter.valueOf(from);
        return getThis();
    }

    public EthFilter setToBlock(BigInteger to) {
        toBlock = DefaultBlockParameter.valueOf(to);
        return getThis();
    }

    public DefaultBlockParameter getFromBlock() {
        return fromBlock;
    }

    public DefaultBlockParameter getToBlock() {
        return toBlock;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public List<String> getAddress() {
        return address;
    }

    @Override
    @JsonIgnore
    EthFilter getThis() {
        return this;
    }

    @Override
    public boolean checkParams() {
        BigInteger fromBlockI = BigInteger.valueOf(0);
        BigInteger toBlockI = BigInteger.valueOf(0);

        if (!super.checkParams()) {
            return false;
        }

        if (fromBlock.isLatest() && toBlock.isLatest()) {
            return true;
        }

        if (fromBlock instanceof DefaultBlockParameterNumber) {
            fromBlockI = ((DefaultBlockParameterNumber) fromBlock).getBlockNumber();
        }

        if (toBlock instanceof DefaultBlockParameterNumber) {
            toBlockI = ((DefaultBlockParameterNumber) toBlock).getBlockNumber();
        }

        if (fromBlockI.compareTo(BigInteger.ZERO) < 0 || toBlockI.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }

        if (!fromBlock.isLatest() && !toBlock.isLatest() && fromBlockI.compareTo(toBlockI) <= 0) {
            return true;
        }

        if (!fromBlock.isLatest() && toBlock.isLatest()) {
            return true;
        }

        return false;
    }
}
