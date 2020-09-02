package org.fisco.bcos.sdk.demo.amop.perf;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopMsgCollector {
    private static Logger logger = LoggerFactory.getLogger(AmopMsgCollector.class);
    private AtomicLong less500 = new AtomicLong(0);
    private AtomicLong less1000 = new AtomicLong(0);
    private AtomicLong less2000 = new AtomicLong(0);
    private AtomicLong less4000 = new AtomicLong(0);
    private AtomicLong less10000 = new AtomicLong(0);
    private AtomicLong timeout10000 = new AtomicLong(0);
    private AtomicLong totalCost = new AtomicLong(0);

    private Integer total = 0;
    private AtomicInteger received = new AtomicInteger(0);
    private AtomicInteger error = new AtomicInteger(0);
    private AtomicInteger responsed = new AtomicInteger(0);
    private Long startTimestamp = System.currentTimeMillis();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void addResponse() {
        responsed.incrementAndGet();
        if (responsed.get() >= total) {
            printResult();
        }
    }

    public void addError() {
        error.incrementAndGet();
    }

    public void onSubscribedTopicMsg(AmopMsgIn msg, long cost) {

        // System.out.println("Subscriber receive msg :" + Hex.toHexString(msg.getContent()));
        try {
            if (cost < 500) {
                less500.incrementAndGet();
            } else if (cost < 1000) {
                less1000.incrementAndGet();
            } else if (cost < 2000) {
                less2000.incrementAndGet();
            } else if (cost < 4000) {
                less4000.incrementAndGet();
            } else if (cost < 10000) {
                less10000.incrementAndGet();
            } else {
                timeout10000.incrementAndGet();
            }
            totalCost.addAndGet(cost);
            received.incrementAndGet();
        } catch (Exception e) {
            logger.error("error:", e);
        }
    }

    public void printResult() {

        System.out.println("total");

        Long totalTime = System.currentTimeMillis() - startTimestamp;

        System.out.println("===================================================================");

        System.out.println("Total amop msg:  " + String.valueOf(total));
        System.out.println("Total time: " + String.valueOf(totalTime) + "ms");
        System.out.println("Success received:  " + String.valueOf(received.get()));
        System.out.println(
                "Msg per second(exclude error requests): "
                        + String.valueOf(received.get() / ((double) totalTime / 1000)));
        System.out.println("Avg time cost: " + String.valueOf(totalCost.get() / total) + "ms");
        System.out.println("Time area:");
        System.out.println(
                "0    < time <  0.5s : "
                        + String.valueOf(less500)
                        + "  : "
                        + String.valueOf((double) less500.get() / total * 100)
                        + "%");
        System.out.println(
                "0.5  < time <  1s   : "
                        + String.valueOf(less1000)
                        + "  : "
                        + String.valueOf((double) less1000.get() / total * 100)
                        + "%");
        System.out.println(
                "1    < time <  2s   : "
                        + String.valueOf(less2000)
                        + "  : "
                        + String.valueOf((double) less2000.get() / total * 100)
                        + "%");
        System.out.println(
                "2    < time <  4s   : "
                        + String.valueOf(less4000)
                        + "  : "
                        + String.valueOf((double) less4000.get() / total * 100)
                        + "%");
        System.out.println(
                "4    < time <  10s  : "
                        + String.valueOf(less10000)
                        + "  : "
                        + String.valueOf((double) less10000.get() / total * 100)
                        + "%");
        System.out.println(
                "10   < time         : "
                        + String.valueOf(timeout10000)
                        + "  : "
                        + String.valueOf((double) timeout10000.get() / total * 100)
                        + "%");

        System.exit(0);
    }
}
