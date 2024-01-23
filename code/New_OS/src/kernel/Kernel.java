package kernel;

import java.io.PrintStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Kernel {
    // 创建可重入锁
    public static ReentrantLock lock = new ReentrantLock();
    // 让线程在等待某个条件满足时挂起，直到另一个线程发出信号才被唤醒
    public static Condition clkCondition = lock.newCondition();  // 时钟条件变量
    public static Condition jitCondition = lock.newCondition();  // 作业进入Table条件变量
    public static Condition pstCondition = lock.newCondition();  // 进程调度条件变量
    public static Condition timeSyncCondition = lock.newCondition(); // 同步时间条件变量
    public static Condition inputBlockCondition = lock.newCondition(); // 键盘输入阻塞条件变量
    public static Condition outputBlockCondition = lock.newCondition(); // 屏幕输出阻塞条件变量

    // main方法作为程序的入口
    public static void main() {
        // 开启时钟线程
        new Thread(new Clock_thread(lock, jitCondition, pstCondition, clkCondition)).start();
        // 开启作业进入Table线程
        new Thread(new JobIn_thread(lock, jitCondition)).start();
        // 开启进程调度线程
        new Thread(new ProcessScheduling_thread(lock, pstCondition, clkCondition)).start();
        // 开启键盘输入阻塞线程
        new Thread(String.valueOf(new InputBlock_thread(lock, inputBlockCondition))).start();
    }
}