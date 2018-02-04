package cn.edu.tj.wenda;

import javax.sound.midi.Soundbank;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mao on 2017/5/23.
 */
public class MultiThreadTest {
    public static void mainx(String[] args) {
//        testThread();
//        testBlockingQueue();
//        testThreadLocal();
//        testExecutor();
//        testWithAtomic();
//        testWithoutAtomic();
        testFuture();
    }

    public static void testFuture(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(2000);
                return 1;
            }
        });

        executorService.shutdown();

        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void testWithoutAtomic() {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 1000; j++) {
                            counter++;
                            System.out.println(counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void testWithAtomic(){
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 1000; j++) {
                            System.out.println(atomicInteger.incrementAndGet());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void testExecutor(){
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2:" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();

        while (!service.isTerminated()){
            try {
                Thread.sleep(1000);
                System.out.println("wait.....");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static int userId;
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    private static void testThreadLocal(){
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocal.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal" + threadLocal.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userId=finalI;
                        Thread.sleep(1000);
                        System.out.println("userId" + userId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    private static void testThread(){
//        for (int i = 0; i < 10; i++) {
//            new MyThread(i).start();
//        }

        for (int i = 0; i <10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            Thread.sleep(100);
                            System.out.println(String.format("t2 %d:%d",finalI,j));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    private static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q),"Consumer1").start();
        new Thread(new Consumer(q),"Consumer2").start();

    }
}
class MyThread extends Thread{
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
                System.out.println(String.format("%d:%d",tid,i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.run();
    }
}


class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (true)
                System.out.println(Thread.currentThread().getName()+":"+q.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                q.put(String.valueOf(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


