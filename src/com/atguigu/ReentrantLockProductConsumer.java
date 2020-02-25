package com.atguigu;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 问题（面试题）：synchronized和ReentrantLock有什么区别？
 *
 * 1 原始构成 synchronized是java关键字JVM层面的，ReentrantLock是jdk5以后的API层面
 * 2 使用方法 synchronized不需要手动释放锁，执行完自动释放，ReentrantLock需要try finally手动释放
 * 3 是否公平锁 synchronized非公平锁，ReentrantLock默认非公平锁，new ReentrantLock(true)则为公平锁
 */
public class ReentrantLockProductConsumer {
    int num = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increase() {
        lock.lock();
        try {
            while (num != 0) {
                condition.await();
            }
            num++;
            System.out.println(Thread.currentThread().getName() + "线程生产" + num);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrease() {
        lock.lock();
        try {
            while (num == 0) {
                condition.await();
            }
            num--;
            System.out.println(Thread.currentThread().getName() + "线程消费" + num);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockProductConsumer shareData = new ReentrantLockProductConsumer();
        new Thread(() -> {
            for (int j = 0; j < 5; j++) {

                shareData.increase();
            }
        }, "A").start();
        new Thread(() -> {
            for (int j = 0; j < 5; j++) {

                shareData.increase();
            }
        }, "B").start();

        new Thread(() -> {
            for (int j = 0; j < 5; j++) {

                shareData.decrease();
            }
        }, "C").start();
        new Thread(() -> {
            for (int j = 0; j < 5; j++) {

                shareData.decrease();
            }
        }, "D").start();

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }
}
