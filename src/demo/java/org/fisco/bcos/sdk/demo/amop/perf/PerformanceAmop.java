package org.fisco.bcos.sdk.demo.amop.perf;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.atomic.AtomicInteger;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.utils.ThreadPoolService;

public class PerformanceAmop {
    private static final String senderConfig =
            PerformanceAmop.class.getClassLoader().getResource("config-sender.toml").getPath();
    private static final String subscriberConfig =
            PerformanceAmop.class.getClassLoader().getResource("config-subscriber.toml").getPath();
    private static AtomicInteger sendedMsg = new AtomicInteger(0);
    private static AmopMsgBuilder msgBuilder = new AmopMsgBuilder();

    public static void main(String[] args) {
        try {
            Integer count = Integer.valueOf(args[0]);
            Integer qps = Integer.valueOf(args[1]);
            Integer msgSize = Integer.valueOf(args[2]);
            Amop sender = new BcosSDK(senderConfig).getAmop();
            Amop subscriber = new BcosSDK(subscriberConfig).getAmop();
            AmopMsgCallback defaultCallback = new AmopMsgCallback();
            AmopMsgCollector collector = defaultCallback.getCollector();
            collector.setTotal(count);
            subscriber.subscribeTopic("normalTopic", defaultCallback);

            Thread.sleep(1000);
            System.out.println(
                    "====== PerformanceAmop Amop public topic text message performance start ======");
            RateLimiter limiter = RateLimiter.create(qps);
            Integer area = count / 10;
            final Integer total = count;
            ThreadPoolService threadPoolService = new ThreadPoolService("PerformanceAmop", 102400);

            for (Integer i = 0; i < count; i++) {
                limiter.acquire();
                threadPoolService
                        .getThreadPool()
                        .execute(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        msgBuilder.sendMsg(
                                                collector,
                                                sender,
                                                "normalTopic",
                                                TopicType.NORMAL_TOPIC,
                                                msgSize);
                                        int current = sendedMsg.incrementAndGet();
                                        if (current >= area && ((current % area) == 0)) {
                                            System.out.println(
                                                    "Already sended: "
                                                            + current
                                                            + "/"
                                                            + total
                                                            + " amop text message");
                                        }
                                    }
                                });
            }
            // wait to send all msg
            while (sendedMsg.get() != count) {
                Thread.sleep(1000);
            }
            threadPoolService.stop();
        } catch (Exception e) {
            System.out.println(
                    "====== PerformanceAmop test failed, error message: " + e.getMessage());
        }
    }
}
