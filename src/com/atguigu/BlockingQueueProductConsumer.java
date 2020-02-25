package com.atguigu;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 阻塞队列实现生产者---消费者模式
 * volatile/cas/AtomicInteger/BlockingQueue阻塞队列
 */
public class BlockingQueueProductConsumer {
    private volatile boolean FALG;//标识总开关，是否开启生产消费模式 volatile修饰多线程修改后其他线程可见
    private AtomicInteger atomicInteger = new AtomicInteger();
    private BlockingQueue<String> blockingQueue;


    public BlockingQueueProductConsumer(boolean FALG, BlockingQueue<String> blockingQueue) {
        this.FALG = FALG;
        this.blockingQueue = blockingQueue;
    }

    //生产
    public void increase() throws Exception {
        while (FALG) {
            int andIncrement = atomicInteger.getAndIncrement();
            boolean offer = blockingQueue.offer(andIncrement + "", 2, TimeUnit.SECONDS);
            if (offer) {
                System.out.println(Thread.currentThread().getName() + "生产成功数据" + andIncrement);
            } else {
                System.out.println(Thread.currentThread().getName() + "生产数据失败");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "发现总开关已经关闭，停止生产");

    }

    //消费
    public void decrease() throws Exception {
        while (FALG) {
            String poll = blockingQueue.poll(2, TimeUnit.SECONDS);
            if (poll != null && !"".equals(poll)) {
                System.out.println(Thread.currentThread().getName() + "成功消费数据" + poll);
            } else {
                System.out.println(Thread.currentThread().getName() + "等待2秒没有数据消费，退出");
                System.out.println();
                System.out.println();
                return;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "发现总开关已经关闭，停止消费");

    }

    public void stop() {
        this.FALG = false;
    }


    public static void main(String[] args) {
        BlockingQueueProductConsumer myShareData = new BlockingQueueProductConsumer(true, new ArrayBlockingQueue<>(10));

        /*for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    myShareData.decrease();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "B" + String.valueOf(i)).start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    myShareData.increase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "A" + String.valueOf(i)).start();
        }
*/
        new Thread(() -> {
            try {
                myShareData.increase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "product").start();
        new Thread(() -> {
            try {
                myShareData.decrease();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "consumer").start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("........mian线程5秒钟后关闭工厂...");
        myShareData.stop();
    }


}
