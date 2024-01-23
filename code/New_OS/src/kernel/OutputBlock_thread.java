package kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static kernel.Process.*;
import static kernel.ProcessScheduling_thread.memory;

public class OutputBlock_thread {
    private ReentrantLock lock;
    public static Condition outputBlockCondition;
    public Process pcb;
    static CPU cpu = ProcessScheduling_thread.cpu;
    public OutputBlock_thread(ReentrantLock lock, Condition inputBlockCondition){
        this.lock = lock;
        outputBlockCondition = inputBlockCondition;
    }
    //输入中断阻塞记录表
    public static List<Integer> block2ProcessIds = new ArrayList<>();
    public static List<Integer> block2EntryTimes = new ArrayList<>();


    public static void awakeOutputBlock(Process process){
        //CPU切换内核态
        cpu.changeToKernelState();
        //将当前进程重新插入就绪队列队尾
        //process.RunTime += 3;
        readyQueue.add(process);
        System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+process.ProID+":"
                +(process.InstrucNum-process.PC+process.shoot)+"]");
        process.hardware = false;
        //currentProcess.PC++;
        //ProcessScheduling_thread.Times = 3;//恢复时间片
        //保护CPU现场
        process.setReadyState(process);
        cpu.CPU_PRO();
        //CPU切换用户态
        cpu.changeToUserState();
    }

    public static void blockProcess(Instruction nowInstruction, Process process){
        int index = nowInstruction.getInstruc_ID();
        MMU mmu = new MMU(memory);
        //计算当前指令的逻辑地址和物理地址
        int instruct_logicalAddress = cpu.setLogicalAddress(index, process.ProID);
        int instruct_physicalAddress = mmu.getPhysicalAddress(process,instruct_logicalAddress);
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.getProcessID()+":"+nowInstruction.getInstruc_ID()+":"
                +nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+":"+instruct_physicalAddress+"]");

        if(nowInstruction.getInstruc_State() == 2){
            block1Queue.add(process);
            System.out.println(Clock_thread.COUNTTIME+":[阻塞进程:"+"阻塞队列1:"+process.getProcessID()+"]");
        } else if (nowInstruction.getInstruc_State() == 3) {
            block2Queue.add(process);
            System.out.println(Clock_thread.COUNTTIME+":[阻塞进程:"+"阻塞队列2:"+process.getProcessID()+"]");
        }

        //设置为阻塞态
        process.setBlockState(process);

        // 记录进程ID和当前时间到阻塞记录表中
        block2ProcessIds.add(process.getProcessID());
        block2EntryTimes.add(Clock_thread.COUNTTIME);
    }

    public static void awakeOutputBlock1(Process pcb) {
        //CPU切换内核态
        cpu.changeToKernelState();
        //将当前进程重新插入就绪队列队尾
        //process.RunTime += 3;
        //回到原队列
        ProcessScheduling_thread.queue1.add(pcb);

        System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+pcb.ProID+":"
                +(pcb.InstrucNum-pcb.PC+pcb.shoot)+"]");
        pcb.hardware = false;
        pcb.IOflag = false;
        pcb.PC++;
        //currentProcess.PC++;
        //ProcessScheduling_thread.times1 = 3;//恢复时间片
        //保护CPU现场
        pcb.setReadyState(pcb);
        cpu.CPU_PRO();
        //CPU切换用户态
        cpu.changeToUserState();
    }

    public static void awakeOutputBlock2(Process pcb) {
        //CPU切换内核态
        cpu.changeToKernelState();
        //将当前进程重新插入就绪队列队尾
        //process.RunTime += 3;
        //回到原队列
        ProcessScheduling_thread.queue2.add(pcb);

        System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+pcb.ProID+":"
                +(pcb.InstrucNum-pcb.PC+pcb.shoot)+"]");
        pcb.hardware = false;
        pcb.IOflag = false;
        pcb.PC++;
        //currentProcess.PC++;
        //ProcessScheduling_thread.times2 = 4;//恢复时间片
        //保护CPU现场
        pcb.setReadyState(pcb);
        cpu.CPU_PRO();
        //CPU切换用户态
        cpu.changeToUserState();
    }

    public static void awakeOutputBlock3(Process pcb) {
        //CPU切换内核态
        cpu.changeToKernelState();
        //将当前进程重新插入就绪队列队尾
        //process.RunTime += 3;
        //回到原队列
        ProcessScheduling_thread.queue3.add(pcb);

        System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+pcb.ProID+":"
                +(pcb.InstrucNum-pcb.PC+pcb.shoot)+"]");
        pcb.hardware = false;
        pcb.IOflag = false;
        pcb.PC++;
        //currentProcess.PC++;
        //ProcessScheduling_thread.times3 = 5;//恢复时间片
        //保护CPU现场
        pcb.setReadyState(pcb);
        cpu.CPU_PRO();
        //CPU切换用户态
        cpu.changeToUserState();
    }
}
