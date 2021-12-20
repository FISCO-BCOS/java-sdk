package org.fisco.bcos.sdk.transaction.decoder;

import com.google.common.collect.Lists;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.contract.solidity.CodecTest;
import org.fisco.bcos.sdk.contract.solidity.ComplexCodecTest;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContractCodecTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private static CodecTest codecTest;
    private static ComplexCodecTest complexCodecTest;
    private static String ComplexCodecName = "ComplexCodecTest";
    private static String CodecTestName = "CodecTest";

    @Test
    public void testComplexCodec() throws IOException, ABICodecException, TransactionBaseException, TransactionException {

        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient("group");
        TransactionDecoderInterface decoder =
                new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());
        ContractLoader contractLoader = new ContractLoader(abiFile, binFile);
        String abi = contractLoader.getABIByContractName(ComplexCodecName);
        AssembleTransactionProcessor manager =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, client.getCryptoSuite().getCryptoKeyPair(), abiFile, binFile);

        TransactionResponse response = manager.deployByContractLoader(ComplexCodecName, Lists.newArrayList());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();

        // setBytesArraryArray
        {
            List<Object> params = Lists.newArrayList();
            byte[] b = "12356789012345678901234567890123".getBytes();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);
            TransactionReceipt transactionReceipt =
                    manager.sendTransactionAndGetReceiptByContractLoader(
                            ComplexCodecName,
                            contractAddress,
                            ComplexCodecTest.FUNC_SETBYTESARRARYARRAY,
                            params);
            TransactionResponse transactionResponse = decoder.
                    decodeReceiptWithValues(abi, ComplexCodecTest.FUNC_SETBYTESARRARYARRAY, transactionReceipt);
            System.out.println(JsonUtils.toJson(transactionResponse));
        }
    }
}
