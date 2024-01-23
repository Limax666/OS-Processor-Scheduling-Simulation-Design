package kernel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

public class Process {
    public boolean IOflag;
    //硬件中断信号
    public boolean hardware = false;
    //当前指令唤醒时间
    public int nowInstruc_time;
    public short state;
    //进程状态常量：运行、就绪、阻塞
    public static final short READY_STATE = 0;
    public static  final short RUNNING_STATE = 1;
    public static final short BLOCK_STATE = 2;

    //进程编号
    public int ProID;

    //进程优先数:随机生成[1-5]整数优先数，优先数越小，优先级越大
    public int Priority;

    //进程创建时间,结束时间
    public int InTimes;
    public int EndTimes;

    //进程状态
    public int PSW;

    //进程运行时间列表
    public int RunTime;

    //进程周转时间列表
    private int TurnTime;

    //进程包含的指令数目
    public int InstrucNum;

    //程序计数器信息:下一条将执行的指令编号
    public int PC;

    //指令寄存器信息：正在执行的指令编号
    public int IR;

    //就绪队列信息列表:位置编号（RqNum）、进入就绪队列时间（RqTimes）
    public static Queue<Process> readyQueue = new LinkedList<>();

    //结束队列
    public static Queue<Process> finishQueue = new LinkedList<>();

    //阻塞队列信息列表1：位置编号（BqNum1）、进程进入键盘输入阻塞队列时间
    // （ BqTimes1）
    public static Queue<Process> block1Queue = new LinkedList<>();

    //阻塞队列信息列表2：位置编号（BqNum2）、进程进入显示器输出阻塞队列时间
    //（ BqTimes2）
    public static Queue<Process> block2Queue = new LinkedList<>();

    public int logicalAddress;//逻辑地址
    public int physicalAddress;//物理地址
    public int shoot;
    public boolean dead = false;
    public int currentInstructions;

    public Process(int pcb_id,int instruc_num,int instruc_id,Job job){
        this.ProID = pcb_id;
        this.IR = instruc_id;
        this.PC = instruc_id;
        this.InstrucNum = instruc_num;
        this.InTimes = Clock_thread.COUNTTIME;
        this.RunTime = 0;
        this.TurnTime = job.getInTime();
        Random random = new Random();
        this.Priority = random.nextInt(6);
        this.logicalAddress = random.nextInt(30);
        this.physicalAddress = instruc_num;
        this.shoot = pcb_id;
        this.currentInstructions = instruc_num;
        this.hardware = false;
        this.IOflag = false;
    }
    public int getProcessID(){
        return  ProID;
    }
    public int getProcessPriority(){
        return Priority;
    }

    public void endProcess(Process process) {
        //进程撤销原语，结束进程
        synchronized (finishQueue){
            finishQueue.add(process);
            this.PSW = 0;
            this.EndTimes = Clock_thread.COUNTTIME;
            this.RunTime = this.EndTimes - this.InTimes;
            this.TurnTime += Clock_thread.COUNTTIME;
        }



    }


    public void setReadyState(Process process){
        process.state= READY_STATE;
    }
    public void setRunningState(Process process){
        process.state = RUNNING_STATE;
    }
    public void setBlockState(Process process){
        process.state = BLOCK_STATE;
    }

    public int getLogicalAddress() {
        return logicalAddress;
    }

    public int getProcessInstrucNum() {
        return InstrucNum;
    }

    public int getProcessIR() {
        return IR;
    }
    public int getProcessPC(){
        return PC;
    }
    public int getEndTimes(){
        return this.EndTimes;
    }

    public int getRunTimes() {
        return this.RunTime;
    }

    public int getTurnTimes() {
        return this.TurnTime;
    }

    public int getInTimes() {
        return this.InTimes;
    }

    public int getProcessShoot() {
        return this.shoot;
    }

//    public void createProcess(int ProID){
//        Process pcb = new Process(ProID);
//        readyQueue.add(pcb);
//    }

    //进程创建原语
//    public void createPro(){
//        //创建进程
//        int pro_id = Objects.requireNonNull(Job.jobQueue.poll()).getJobId();
//        int instruc_state = Objects.requireNonNull(Objects.requireNonNull(Instruction.InstructionQueue.poll())[(Objects.requireNonNull(Job.jobQueue.poll())).getJobId()]).getInstruc_State();
//        int pro_size = 0;
//        if(instruc_state == 1 || instruc_state == 2){
//            pro_size = 100;
//        }
//        createProcess(pro_id);//进程控制块进入就绪队列
//        System.out.println(Clock_thread.COUNTTIME+":[创建进程:"+pro_id+"]");
//
//        //为进程分配内存
//        Memory memory = new Memory();
//        memory.allocate(pro_size);
//        System.out.println(Clock_thread.COUNTTIME+":已为"+pro_id+"进程分配内存");
//
//    }

    //进程调度原语
//    public void schedule(){
//        while(!readyQueue.isEmpty()){
//            Process currentProcess = readyQueue.poll();
//            currentProcess.RunTime +=1;
//            if (currentProcess.RunTime == currentProcess.Priority){
//                currentProcess.endProcess();
//            }else{
//                readyQueue.add(currentProcess);
//            }
//        }
//    }
}
