package com.atguigu;

public class ProConsumer01 {

    public static void main1(String[] args) {
        SynchronizedProductConsumer shareData = new SynchronizedProductConsumer();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData.increase();
              /*  System.out.println(Thread.currentThread().getName() + " 生产数据 " + shareData.num);
                System.out.println();
                System.out.println();*/

            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData.decrease();
                System.out.println(Thread.currentThread().getName() + " 消费数据 " + shareData.num);
                System.out.println();
                System.out.println();

            }
        }, "t2").start();
    }

    public static void main(String args[]) {
        ReentrantLockProductConsumer shareData2 = new ReentrantLockProductConsumer();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData2.increase();
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData2.decrease();
            }
        }, "t2").start();
    }


}
