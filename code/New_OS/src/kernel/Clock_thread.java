package kernel;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Clock_thread implements Runnable{
    private ReentrantLock lock;
    private Condition jitCondition;
    private Condition pstCondition;
    private Condition clkCondition;
    public static int speedTime;

    public Clock_thread(ReentrantLock lock, Condition jitCondition, Condition pstCondition, Condition clkCondition) {
        this.lock = lock;
        this.jitCondition = jitCondition;
        this.pstCondition = pstCondition;
        this.clkCondition = clkCondition;
    }

    //初始时间的静态变量
    public static int COUNTTIME = 0;

    public static void setSleepTime(int speedTime) throws InterruptedException {
        Thread.sleep(Clock_thread.speedTime);
    }

    @Override
    public void run() {
        //休眠来同步所有线程
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while(true){
            //上锁
            Kernel.lock.lock();
            if(ProcessScheduling_thread.isSleep){
                Kernel.jitCondition.signal();
                Kernel.pstCondition.signal();
            }

            try{
                //唤醒JIT
                Kernel.jitCondition.signal();
                JobIn_thread.activateThread();
                //唤醒PST
                Kernel.pstCondition.signal();

                //JobIn_thread.activateThread();
                //休眠CLK
                Kernel.clkCondition.await();

                //调用计时函数
                timeCount();


            }catch (Exception e){

            }finally {
                //释放锁
                Kernel.lock.unlock();
            }
        }
    }
    public void timeCount() throws InterruptedException {
//        synchronized (this) { // 加锁操作，保证互斥访问COUNTTIME变量
//            Thread.sleep(1000);
//            System.out.println(COUNTTIME + ":[CLK RUN]");
//            COUNTTIME++;
//        }
        Kernel.lock.lock();
        try{
            setSleepTime(speedTime);
            //Thread.sleep(1000);
            System.out.println(COUNTTIME + ":[CLK RUN]");
            COUNTTIME++;
        }finally {
            Kernel.lock.unlock();
        }
    }
    public int  getCurrentTime(){
        return COUNTTIME;
    }


}


