package org.fisco.bcos.sdk.v3.contract.precompiled.sharding;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class ShardingService {

    private final ShardingPrecompiled shardingPrecompiled;
    private final long currentVersion;

    public ShardingService(Client client, CryptoKeyPair credential) {
        this.shardingPrecompiled =
                ShardingPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.SHARDING_PRECOMPILED_NAME
                                : PrecompiledAddress.SHARDING_PRECOMPILED_ADDRESS,
                        client,
                        credential);
        this.currentVersion =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getProtocol()
                        .getCompatibilityVersion();
    }

    public long getCurrentVersion() {
        return currentVersion;
    }

    public String getContractShard(String contractAddress) throws ContractException {
        PrecompiledVersionCheck.SHARDING_MIN_SUPPORT_VERSION.checkVersion(currentVersion);

        Tuple2<BigInteger, String> output = shardingPrecompiled.getContractShard(contractAddress);
        if (!output.getValue1().equals(BigInteger.ZERO)) {
            RetCode precompiledResponse =
                    PrecompiledRetCode.getPrecompiledResponse(output.getValue1().intValue(), "");
            throw new ContractException(
                    "ShardingService: list return error code: "
                            + output.getValue1()
                            + ", error msg: "
                            + precompiledResponse.getMessage(),
                    precompiledResponse.getCode());
        }
        return output.getValue2();
    }

    public RetCode makeShard(String shardName) throws ContractException {
        PrecompiledVersionCheck.SHARDING_MIN_SUPPORT_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt = shardingPrecompiled.makeShard(shardName);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> shardingPrecompiled.getMakeShardOutput(tr).getValue1());
    }

    public RetCode linkShard(String shardName, String address) throws ContractException {
        PrecompiledVersionCheck.SHARDING_MIN_SUPPORT_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt = shardingPrecompiled.linkShard(shardName, address);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> shardingPrecompiled.getLinkShardOutput(tr).getValue1());
    }
}
