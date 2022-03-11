package org.fisco.bcos.sdk.v3.codec;

import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventEncoderTest {
    private final EventEncoder eventEncoder = new EventEncoder(new CryptoSuite(0));

    @Test
    public void testBuildEventSignature() {
        assertEquals(
                eventEncoder.buildEventSignature("Deposit(address,hash256,uint256)"),
                ("0x50cb9fe53daa9737b786ab3646f04d0150dc50ef4e75f59509d83667ad5adb20"));

        assertEquals(
                eventEncoder.buildEventSignature("Notify(uint256,uint256)"),
                ("0x71e71a8458267085d5ab16980fd5f114d2d37f232479c245d523ce8d23ca40ed"));
    }

    @Test
    public void testEncode() {
        Event event =
                new Event(
                        "Notify",
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {
                                }, new TypeReference<Uint256>() {
                                }));

        assertEquals(
                eventEncoder.encode(event),
                "0x71e71a8458267085d5ab16980fd5f114d2d37f232479c245d523ce8d23ca40ed");
    }

    @Test
    public void testBuildMethodSignature() {
        List<TypeReference<?>> parameters =
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        });

        assertEquals(
                eventEncoder.buildMethodSignature("Notify", Utils.convert(parameters)),
                "Notify(uint256,uint256)");
    }
}
