package com.atguigu;

public class SynchronizedProductConsumer {

    int num = 0;

    /*虚假唤醒：有这么一种情况，2个生产者A,B 2个消费者C,D， 某个时刻num=1,生产者A进入increase()方法，等待并且释放锁
              B获得锁进入increase()方法，num还是为1，进入等待释放锁，消费者C消费了，num=0,唤醒了A和B,A先获得锁，那么
              A线程将从 this.wait();后面开始执行，num++，num=1,(此时B获得锁，也从this.wait();开始这行num++,
              此时num=2,出问题了)，如果用while判断B获得锁时候从,this.wait()后开始执行，在循环判断num=1,num!=0
              进入while循环，等待，这种情况就是虚假唤醒，while判断可以解决该问题
     public synchronized void increase() {
        if (num != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName() + " 线程生产数据 " + num);
        this.notifyAll();
    }
    解决：所以为了避免这种情况，只好用while循环避免虚假唤醒。（因为if只判断一次，不能避免虚假唤醒）*/

    public synchronized void increase() {
        while (num != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName() + " 线程生产数据 " + num);
        this.notifyAll();
    }

    public synchronized void decrease() {
        while (num == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;
        System.out.println(Thread.currentThread().getName() + " 线程消费数据 " + num);
        this.notifyAll();
    }

    public static void main(String[] args) {
        SynchronizedProductConsumer shareData = new SynchronizedProductConsumer();
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
