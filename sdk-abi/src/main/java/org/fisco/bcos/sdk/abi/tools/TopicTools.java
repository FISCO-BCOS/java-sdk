package org.fisco.bcos.sdk.abi.tools;

import java.math.BigInteger;
import org.fisco.bcos.sdk.abi.TypeEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Bytes;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.AddressUtils;
import org.fisco.bcos.sdk.utils.Numeric;

public class TopicTools {

    public static final int MAX_NUM_TOPIC_EVENT_LOG = 4;
    public static final String LATEST = "latest";

    private CryptoSuite cryptoSuite;

    public TopicTools(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public String integerToTopic(BigInteger i) {
        return Numeric.toHexStringWithPrefixZeroPadded(i, 64);
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

    public String stringToTopic(String s) {
        byte[] hash = cryptoSuite.hash(s.getBytes());
        return Numeric.toHexString(hash);
    }

    public String bytesToTopic(byte[] b) {
        byte[] hash = cryptoSuite.hash(b);
        return Numeric.toHexString(hash);
    }

    public String byteNToTopic(byte[] b) {
        // byte[] can't be more than 32 byte
        if (b.length > 32) {
            throw new IllegalArgumentException("byteN can't be more than 32 byte");
        }

        Bytes bs = new Bytes(b.length, b);
        return Numeric.prependHexPrefix(TypeEncoder.encode(bs));
    }
}
