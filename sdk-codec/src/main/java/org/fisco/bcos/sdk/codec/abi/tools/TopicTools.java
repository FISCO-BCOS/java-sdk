package org.fisco.bcos.sdk.codec.abi.tools;

import java.math.BigInteger;
import java.util.Objects;

import org.fisco.bcos.sdk.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.AddressUtils;
import org.fisco.bcos.sdk.utils.Numeric;

public class TopicTools {

    public static final int MAX_NUM_TOPIC_EVENT_LOG = 4;
    public static final int TOPIC_LENGTH = 64;

    private final CryptoSuite cryptoSuite;

    public TopicTools(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public String integerToTopic(BigInteger i) {
        return Numeric.toHexStringWithPrefixZeroPadded(i, 64);
    }

    public static boolean validTopic(String topic) {
        if (Objects.isNull(topic)) {
            return false;
        }

        if (topic.startsWith("0x") || topic.startsWith("0X")) {
            return topic.length() == (TOPIC_LENGTH + 2);
        }

        return topic.length() == TOPIC_LENGTH;
    }


    public String boolToTopic(boolean b) {
        if (b) {
            return Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ONE, 64);
        } else {
            return Numeric.toHexStringWithPrefixZeroPadded(BigInteger.ZERO, 64);
        }
    }

    public String addressToTopic(String s) {
        if (!AddressUtils.isValidAddress(s)) {
            throw new IllegalArgumentException("invalid address");
        }

        return "0x000000000000000000000000" + Numeric.cleanHexPrefix(s);
    }

    public byte[] stringToTopic(String s) {
        return this.cryptoSuite.hash(s.getBytes());
    }

    public byte[] bytesToTopic(byte[] b) {
        return this.cryptoSuite.hash(b);
    }

    public byte[] byteNToTopic(byte[] b) {
        // byte[] can't be more than 32 byte
        if (b.length > 32) {
            throw new IllegalArgumentException("byteN can't be more than 32 byte");
        }
        Bytes bs = new Bytes(b.length, b);
        return TypeEncoder.encode(bs);
    }
}
