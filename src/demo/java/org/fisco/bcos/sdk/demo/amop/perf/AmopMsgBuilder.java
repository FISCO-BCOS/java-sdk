package org.fisco.bcos.sdk.demo.amop.perf;

import java.util.Random;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Response;

public class AmopMsgBuilder {

    public void sendMsg(
            AmopMsgCollector collector, Amop sender, String topic, TopicType type, int contentLen) {
        AmopMsgOut out = new AmopMsgOut();
        out.setTopic(topic);
        out.setType(type);
        out.setTimeout(5000);
        out.setContent(getRandomBytes(contentLen));

        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        collector.addResponse();
                        if (response.getErrorCode() != 0) {
                            collector.addError();
                        }
                    }
                };
        sender.sendAmopMsg(out, callback);
    }

    public byte[] getRandomBytes(int len) {
        Random random = new Random();
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            Integer is = random.nextInt(9);
            b[i] = Byte.parseByte(is.toString());
        }
        return b;
    }
}
