package kernel;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static kernel.Kernel.jitCondition;
import static kernel.Process.*;
import java.util.stream.Collectors;

public class ProcessScheduling_thread implements Runnable{
    public static Memory memory = new Memory();
    static public boolean isSleep = false;
    static int Times = 3;//初始时间片大小
    private ReentrantLock lock;
    private Condition pstCondition;
    private Condition clkCondition;
    public static CPU cpu = new CPU();//CPU类的实例化

    //DJFK
    // 三个队列
    public static Queue<Process> queue1 = new LinkedList<>();  // 时间片为3
    static int times1 = 3;
    public static Queue<Process> queue2 = new LinkedList<>();  // 时间片为5
    static int times2 = 5;
    public static Queue<Process> queue3 = new LinkedList<>();  // 时间片为6
    static int times3 = 6;
    public static int FinalTime = 0;

    public ProcessScheduling_thread(ReentrantLock lock, Condition pstCondition, Condition clkCondition) {
        this.lock = lock;
        this.pstCondition = pstCondition;
        this.clkCondition = clkCondition;
    }
//    public void createProcess(int ProID,int instruc_num){
//        Process pcb = new Process(ProID,instruc_num);
//        readyQueue.add(pcb);
//    }
    public void SetSchedulingTime(int times){
        // 以初始静态时间片大小为基础单元，重新赋值时间片大小
        this.Times = times;
    }
    public Queue<Instruction[]> getInstructionQueue() {
        return Instruction.InstructionQueue;
    }

    public void createPro_DJFK(){
        //创建进程
        int pro_size = 0;
        Job job = Job.jobQueue.poll();
        if(job != null){
            int pro_id = job.getJobId();
            Instruction[] instructions = Instruction.InstructionQueue.poll();
            Instruction.InstructionQueue.add(instructions);
            if (instructions != null && pro_id <= instructions.length)
            {
                int instruc_state = instructions[pro_id-1].getInstruc_State();
                int instruc_num = instructions.length;
                int instruc_id=  instructions[pro_id-1].getInstruc_ID();
                if(instruc_num == 10){
                    pro_size = 100;
                } else if (instruc_num == 20) {
                    pro_size = 200;
                }
                Process pcb = new Process(pro_id,instruc_num,instruc_id,job);
                //进程控制块进入多级反馈队列
                queue1.add(pcb);
                //readyQueue.add(pcb);//进程控制块进入就绪队列
                int proBaseAddress = memory.allocate(pcb,pro_size);
                //int proBaseAddress = memory.getProcessBaseAddress(pro_id);
                System.out.println(Clock_thread.COUNTTIME+":[创建进程:"+pro_id+":"+proBaseAddress+":"+"连续分配"+"]");

                System.out.println(Clock_thread.COUNTTIME+":[进入就绪队列:"+pro_id+":"+instruc_num+"]");
            }
        }
    }
    public synchronized void createPro() throws InterruptedException {
        //创建进程
        int pro_size = 0;
        Job job = Job.jobQueue.poll();
        if(job != null){
            int pro_id = job.getJobId();
            Instruction[] instructions = Instruction.InstructionQueue.poll();
            Instruction.InstructionQueue.add(instructions);
            if (instructions != null && pro_id <= instructions.length)
            {
                int instruc_state = instructions[pro_id-1].getInstruc_State();
                int instruc_num = instructions.length;
                int instruc_id=  instructions[pro_id-1].getInstruc_ID();
                if(instruc_num == 10){
                    pro_size = 100;
                } else if (instruc_num == 20) {
                    pro_size = 200;
                }
                Process pcb = new Process(pro_id,instruc_num,instruc_id,job);
                readyQueue.add(pcb);//进程控制块进入就绪队列
                //createProcess(pro_id,instruc_num);
                //Memory memory = new Memory();
                //为进程分配内存
                //memory.allocate(pcb,pro_size);
                int proBaseAddress = memory.allocate(pcb,pro_size);
                //int proBaseAddress = memory.getProcessBaseAddress(pro_id);
                System.out.println(Clock_thread.COUNTTIME+":[创建进程:"+pro_id+":"+proBaseAddress+":"+"连续分配"+"]");


                //Memory memory = new Memory();
                //memory.allocate(pro_size);
                //System.out.println(Clock_thread.COUNTTIME+":已为"+pro_id+"进程分配内存");


                System.out.println(Clock_thread.COUNTTIME+":[进入就绪队列:"+pro_id+":"+instruc_num+"]");
            }
        }
    }
    public void schedule_DJFK() throws InterruptedException {
        while(!queue1.isEmpty()){
            //获取就绪队列队首进程
            Process currentProcess1 = queue1.poll();

            //将CPU分配给当前进程
            //CPU cpu = new CPU();//CPU类的实例化
            cpu.allocate(cpu,currentProcess1);
            //CPU切换内核态
            cpu.changeToKernelState();
            //恢复CPU现场
            currentProcess1.setRunningState(currentProcess1);
            cpu.CPU_REC();
            //CPU切换用户态
            cpu.changeToUserState();

            //执行指令
            while(times1>=0 && !currentProcess1.dead){
                if(!currentProcess1.IOflag){
                    executeInstruction_DJFK1(currentProcess1);
                }else {
                    break;
                }
                //判断当前进程是否执行完所有指令
                if(currentProcess1.PC-currentProcess1.shoot+1 > currentProcess1.InstrucNum){
                    //进程运行完毕，CPU转换为内核态
                    cpu.changeToKernelState();
                    //结束进程
                    currentProcess1.endProcess(currentProcess1);
                    //释放内存
                    //memory.free((currentProcess.ProID),currentProcess.physicalAddress);
                    memory.free(memory.getProcessBaseAddress(currentProcess1.getProcessID()),currentProcess1.physicalAddress);
                    //memory.free(currentProcess.getProcessID(),currentProcess.getProcessInstrucNum());

                    //CPU切换用户态
                    cpu.changeToUserState();
                    System.out.println(Clock_thread.COUNTTIME+":[终止进程："+currentProcess1.ProID+"]");
                    times1 = 3;
                    currentProcess1.dead = true;
                    break;
                }
            }
            if(currentProcess1.dead){
                break;
            }
            times1 = 3;
        }

        while(!queue2.isEmpty()){
            //获取就绪队列队首进程
            Process currentProcess2 = queue2.poll();

            //将CPU分配给当前进程
            //CPU cpu = new CPU();//CPU类的实例化
            cpu.allocate(cpu,currentProcess2);
            //CPU切换内核态
            cpu.changeToKernelState();
            //恢复CPU现场
            currentProcess2.setRunningState(currentProcess2);
            cpu.CPU_REC();
            //CPU切换用户态
            cpu.changeToUserState();

            //执行指令
            while(times2>=0&&!currentProcess2.dead){
                if(!currentProcess2.IOflag){
                    executeInstruction_DJFK2(currentProcess2);
                }else {
                    break;
                }
                //判断当前进程是否执行完所有指令
                if(currentProcess2.PC-currentProcess2.shoot+1 > currentProcess2.InstrucNum){
                    //进程运行完毕，CPU转换为内核态
                    cpu.changeToKernelState();
                    //结束进程
                    currentProcess2.endProcess(currentProcess2);
                    //释放内存
                    //memory.free((currentProcess.ProID),currentProcess.physicalAddress);
                    memory.free(memory.getProcessBaseAddress(currentProcess2.getProcessID()),currentProcess2.physicalAddress);
                    //memory.free(currentProcess.getProcessID(),currentProcess.getProcessInstrucNum());

                    //CPU切换用户态
                    cpu.changeToUserState();
                    System.out.println(Clock_thread.COUNTTIME+":[终止进程："+currentProcess2.ProID+"]");
                    times2 = 5;
                    currentProcess2.dead = true;
                    break;
                }
            }
            if(currentProcess2.dead){
                break;
            }
            times2 = 5;

        }

        //进入最后一级队列调度，时间片轮转
        while(!queue3.isEmpty()){
            //获取就绪队列队首进程
            Process currentProcess3 = queue3.poll();

            //将CPU分配给当前进程
            //CPU cpu = new CPU();//CPU类的实例化
            assert currentProcess3 != null;
            cpu.allocate(cpu,currentProcess3);
            //CPU切换内核态
            cpu.changeToKernelState();
            //恢复CPU现场
            currentProcess3.setRunningState(currentProcess3);
            cpu.CPU_REC();
            //CPU切换用户态
            cpu.changeToUserState();

            //执行指令
            while(times3>0 && !currentProcess3.dead){
                if(!currentProcess3.IOflag){
                    executeInstruction_DJFK3(currentProcess3);
                }else {
                    break;
                }

                //判断当前进程是否执行完所有指令
                if(currentProcess3.PC-currentProcess3.shoot+1 > currentProcess3.InstrucNum){
                    //进程运行完毕，CPU转换为内核态
                    cpu.changeToKernelState();
                    //结束进程
                    currentProcess3.endProcess(currentProcess3);
                    //释放内存
                    //memory.free((currentProcess.ProID),currentProcess.physicalAddress);
                    memory.free(memory.getProcessBaseAddress(currentProcess3.getProcessID()),currentProcess3.physicalAddress);
                    //memory.free(currentProcess.getProcessID(),currentProcess.getProcessInstrucNum());

                    //CPU切换用户态
                    cpu.changeToUserState();
                    System.out.println(Clock_thread.COUNTTIME+":[终止进程："+currentProcess3.ProID+"]");
                    times3 = 6;
                    currentProcess3.dead = true;
                    break;
                }
            }
            if(times3 <= 0){
                //时间片用完，当前进程 运行态->就绪态
                //CPU切换内核态
                cpu.changeToKernelState();
                //判断该进程是否执行完
//                if(currentProcess.PC-currentProcess.shoot+1>currentProcess.InstrucNum){
//                    break;
//                }
                //将当前进程重新插入就绪队列队尾
                currentProcess3.RunTime += 6;
                queue3.add(currentProcess3);
                System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+currentProcess3.ProID+":"
                        +(currentProcess3.InstrucNum-currentProcess3.PC+currentProcess3.shoot)+"]");
                //currentProcess.PC++;
                times3 = 6;//恢复时间片
                currentProcess3.dead = false;
                //保护CPU现场
                currentProcess3.setReadyState(currentProcess3);
                cpu.CPU_PRO();
                //CPU切换用户态
                cpu.changeToUserState();

            }

        }

        //当三个多级反馈队列都为空了且阻塞队列不空，则正常唤醒阻塞队列
        while((!block2Queue.isEmpty() || !block1Queue.isEmpty()) && queue3.isEmpty() && queue1.isEmpty() && queue2.isEmpty()){
            if(!block2Queue.isEmpty()){
                InputBlock_thread.awakeInputBlock3(block2Queue.poll());
            }
            if(!block1Queue.isEmpty()){
                InputBlock_thread.awakeInputBlock3(block1Queue.poll());
            }

            //再次进入就绪队列
            while(!queue3.isEmpty()){
                //获取就绪队列队首进程
                Process currentProcess3 = queue3.poll();

                //将CPU分配给当前进程
                //CPU cpu = new CPU();//CPU类的实例化
                assert currentProcess3 != null;
                cpu.allocate(cpu,currentProcess3);
                //CPU切换内核态
                cpu.changeToKernelState();
                //恢复CPU现场
                currentProcess3.setRunningState(currentProcess3);
                cpu.CPU_REC();
                //CPU切换用户态
                cpu.changeToUserState();

                //执行指令
                while(times3>0 && !currentProcess3.dead){
                    if(!currentProcess3.IOflag){
                        executeInstruction_DJFK3(currentProcess3);
                    }else {
                        break;
                    }

                    //判断当前进程是否执行完所有指令
                    if(currentProcess3.PC-currentProcess3.shoot+1 > currentProcess3.InstrucNum){
                        //进程运行完毕，CPU转换为内核态
                        cpu.changeToKernelState();
                        //结束进程
                        currentProcess3.endProcess(currentProcess3);
                        //释放内存
                        //memory.free((currentProcess.ProID),currentProcess.physicalAddress);
                        memory.free(memory.getProcessBaseAddress(currentProcess3.getProcessID()),currentProcess3.physicalAddress);
                        //memory.free(currentProcess.getProcessID(),currentProcess.getProcessInstrucNum());

                        //CPU切换用户态
                        cpu.changeToUserState();
                        System.out.println(Clock_thread.COUNTTIME+":[终止进程："+currentProcess3.ProID+"]");
                        times3 = 6;
                        currentProcess3.dead = true;
                        break;
                    }
                }
                if(times3 <= 0){
                    //时间片用完，当前进程 运行态->就绪态
                    //CPU切换内核态
                    cpu.changeToKernelState();
                    //判断该进程是否执行完
//                if(currentProcess.PC-currentProcess.shoot+1>currentProcess.InstrucNum){
//                    break;
//                }
                    //将当前进程重新插入就绪队列队尾
                    currentProcess3.RunTime += 6;
                    queue3.add(currentProcess3);
                    System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+currentProcess3.ProID+":"
                            +(currentProcess3.InstrucNum-currentProcess3.PC+currentProcess3.shoot)+"]");
                    //currentProcess.PC++;
                    times3 = 6;//恢复时间片
                    currentProcess3.dead = false;
                    //保护CPU现场
                    currentProcess3.setReadyState(currentProcess3);
                    cpu.CPU_PRO();
                    //CPU切换用户态
                    cpu.changeToUserState();

                }

            }



        }

    }

    public void schedule() throws InterruptedException {
        int beginTime = Clock_thread.COUNTTIME;
        //将就绪队列转化为优先级队列，并提供自定义比较器比较进程的优先级
        PriorityQueue<Process> priorityQueue = new PriorityQueue<>(readyQueue.size(),
                (p1,p2) -> Integer.compare(p1.getProcessPriority(),p2.getProcessPriority()));
        priorityQueue.addAll(readyQueue);
        //进程调度
        while(!readyQueue.isEmpty()){
            //获取就绪队列队首进程
            Process currentProcess = readyQueue.poll();
            //B等级
            Times = 3;
            //将CPU分配给当前进程
            //CPU cpu = new CPU();//CPU类的实例化
            cpu.allocate(cpu,currentProcess);
            //CPU切换内核态
            cpu.changeToKernelState();
            //恢复CPU现场
            currentProcess.setRunningState(currentProcess);
            cpu.CPU_REC();
            //CPU切换用户态
            cpu.changeToUserState();

            while(Times>=0){
                if(!currentProcess.IOflag){
                    executeInstruction(currentProcess);
                }else {
                    break;
                }

                //C等级
                //executeInstruction(currentProcess);

                Thread.sleep(100);
//                Times--;
                //判断当前进程是否执行完所有指令
                if(currentProcess.PC-currentProcess.shoot+1 > currentProcess.InstrucNum){
                    //进程运行完毕，CPU转换为内核态
                    cpu.changeToKernelState();
                    //结束进程
                    currentProcess.endProcess(currentProcess);
                    //释放内存
                    //memory.free((currentProcess.ProID),currentProcess.physicalAddress);
                    memory.free(memory.getProcessBaseAddress(currentProcess.getProcessID()),currentProcess.physicalAddress);
                    //memory.free(currentProcess.getProcessID(),currentProcess.getProcessInstrucNum());

                    //CPU切换用户态
                    cpu.changeToUserState();
                    System.out.println(Clock_thread.COUNTTIME+":[终止进程："+currentProcess.ProID+"]");
                    Times = 3;
                    currentProcess.dead = true;
                    break;
                }
            }
            if(Times < 0 ){
                //时间片用完，当前进程 运行态->就绪态
                //CPU切换内核态
                cpu.changeToKernelState();
                //判断该进程是否执行完
//                if(currentProcess.PC-currentProcess.shoot+1>currentProcess.InstrucNum){
//                    break;
//                }
                //将当前进程重新插入就绪队列队尾
                currentProcess.RunTime += 3;
                readyQueue.add(currentProcess);
                System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+currentProcess.ProID+":"
                        +(currentProcess.InstrucNum-currentProcess.PC+currentProcess.shoot)+"]");
                //currentProcess.PC++;
                Times = 3;//恢复时间片
                currentProcess.dead = false;
                //保护CPU现场
                currentProcess.setReadyState(currentProcess);
                cpu.CPU_PRO();
                //CPU切换用户态
                cpu.changeToUserState();

            }

        }
    }
    public void executeInstruction_DJFK1(Process process) throws InterruptedException {
        Instruction[] ins = new Instruction[process.InstrucNum];
        //因为要找第i个进程队列的指令集，队列不宜按位查找，所以先将队列转化为一个列表
        List<Instruction[]> instructionList = new ArrayList<>(Instruction.InstructionQueue);
        //List<Instruction[]> instructionList = Instruction.InstructionQueue.stream().toList();
        //获取当前进程对应的指令集
        ins = instructionList.get(process.getProcessID()-1);
        //ins = instructionList.get(process.getProcessID()-1);
        //执行当前指令
        assert ins != null;
        //获取当前进程PC指针对应的那一条指令
        Instruction nowInstruction = ins[process.PC-process.shoot];
        //判断该指令是否是IO中断指令
        if((nowInstruction.getInstruc_State() == 2 || nowInstruction.getInstruc_State() == 3) && !process.IOflag){
            process.hardware = true;
        }
        //获取当前指令的执行时间
        int wakeUpTime = nowInstruction.getInRunTimes();
        process.nowInstruc_time = wakeUpTime;
        //判断当前指令运行几秒
        if(process.nowInstruc_time == 2){
            times1 -= 2;
        }else if (process.nowInstruc_time == 1){
            times1 -= 1;
        }
        if(!process.hardware){
            //非IO
            if(times1<0){
                //队列1的时间片到，进程进入队列2
                queue2.add(process);
                System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+process.ProID+":"
                        +(process.InstrucNum-process.PC+process.shoot)+"]");
                //times1 = 3;//恢复时间片
                return;
            }else {
                if(process.nowInstruc_time == 1){
                    runInstruction_DJFK(nowInstruction,process);
                } else if (process.nowInstruc_time == 2) {
                    runInstruction2_DJFK(nowInstruction,process);
                }

            }
            while(!block1Queue.isEmpty()){
                Process pcb = block1Queue.poll();
                InputBlock_thread.awakeInputBlock1(pcb);
            }
            while(!block2Queue.isEmpty()){
                Process pcb = block2Queue.poll();
                OutputBlock_thread.awakeOutputBlock1(pcb);
            }
        }else {
            //IO
            if(nowInstruction.getInstruc_State() == 2){
                InputBlock_thread.blockProcess(nowInstruction,process);
            }
            else if(nowInstruction.getInstruc_State() == 3){
                OutputBlock_thread.blockProcess(nowInstruction,process);
            }

            //nowInstruction.nowtime = Clock_thread.COUNTTIME;
            Clock_thread.COUNTTIME++;
            //InputBlock_thread.CheckBlock(nowInstruction.nowtime,process);
//            InputBlock_thread.awakeInputBlock(process);
            process.IOflag = true;

        }
    }
    public void executeInstruction_DJFK2(Process process) throws InterruptedException {
        Instruction[] ins = new Instruction[process.InstrucNum];
        List<Instruction[]> instructionList = new ArrayList<>(Instruction.InstructionQueue);
//        List<Instruction[]> instructionList = Instruction.InstructionQueue.stream().toList();
        ins = instructionList.get(process.getProcessID()-1);
        //执行当前指令
        assert ins != null;
        Instruction nowInstruction = ins[process.PC-process.shoot];
        if((nowInstruction.getInstruc_State() == 2 || nowInstruction.getInstruc_State() == 3) && !process.IOflag){
            process.hardware = true;
        }
        int wakeUpTime = nowInstruction.getInRunTimes();
        process.nowInstruc_time = wakeUpTime;
        //判断当前指令运行几秒
        if(process.nowInstruc_time == 2){
            times2 -= 2;
        }else if (process.nowInstruc_time == 1){
            times2 -= 1;
        }
        if(!process.hardware){
            //非IO
            if(times2<0){
                //队列2的时间片到，进程进入队列3
                queue3.add(process);
                System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+process.ProID+":"
                        +(process.InstrucNum-process.PC+process.shoot)+"]");
                //times2 = 5;
                return;
            }else {
                if(process.nowInstruc_time == 1){
                    runInstruction_DJFK(nowInstruction,process);
                } else if (process.nowInstruc_time == 2) {
                    runInstruction2_DJFK(nowInstruction,process);
                }

            }
            while(!block1Queue.isEmpty()){
                Process pcb = block1Queue.poll();
                InputBlock_thread.awakeInputBlock2(pcb);
            }
            while(!block2Queue.isEmpty()){
                Process pcb = block2Queue.poll();
                OutputBlock_thread.awakeOutputBlock2(pcb);
            }
        }else {
            //IO
            if(nowInstruction.getInstruc_State() == 2){
                InputBlock_thread.blockProcess(nowInstruction,process);
            }
            else if(nowInstruction.getInstruc_State() == 3){
                OutputBlock_thread.blockProcess(nowInstruction,process);
            }

            //nowInstruction.nowtime = Clock_thread.COUNTTIME;
            Clock_thread.COUNTTIME++;
            //InputBlock_thread.CheckBlock(nowInstruction.nowtime,process);
//            InputBlock_thread.awakeInputBlock(process);
            process.IOflag = true;

        }
    }
    public void executeInstruction_DJFK3(Process process) throws InterruptedException {
        Instruction[] ins = new Instruction[process.InstrucNum];
        List<Instruction[]> instructionList = new ArrayList<>(Instruction.InstructionQueue);
        //List<Instruction[]> instructionList = Instruction.InstructionQueue.stream().toList();
        ins = instructionList.get(process.getProcessID()-1);
        //执行当前指令
        assert ins != null;
        Instruction nowInstruction = ins[process.PC-process.shoot];
        //Instruction nowInstruction = ins[process.PC-process.shoot];
        if((nowInstruction.getInstruc_State() == 2 || nowInstruction.getInstruc_State() == 3) && !process.IOflag){
            process.hardware = true;
        }
        int wakeUpTime = nowInstruction.getInRunTimes();
        process.nowInstruc_time = wakeUpTime;
        //判断当前指令运行几秒
        if(process.nowInstruc_time == 2){
            times3 -= 2;
        }else if (process.nowInstruc_time == 1){
            times3 -= 1;
        }
        if(!process.hardware){
            //非IO
            if(times3<0){
                //队列3的时间片到，进程进入队列3
                queue3.add(process);
                System.out.println(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+process.ProID+":"
                        +(process.InstrucNum-process.PC+process.shoot)+"]");
                //times3 = 6;
                return;
            }else {
                if(process.nowInstruc_time == 1){
                    runInstruction_DJFK(nowInstruction,process);
                } else if (process.nowInstruc_time == 2) {
                    runInstruction2_DJFK(nowInstruction,process);
                }

            }
            while(!block1Queue.isEmpty()){
                Process pcb = block1Queue.poll();
                InputBlock_thread.awakeInputBlock3(pcb);
            }
            while(!block2Queue.isEmpty()){
                Process pcb = block2Queue.poll();
                OutputBlock_thread.awakeOutputBlock3(pcb);
            }
        }else {
            //IO
            if(nowInstruction.getInstruc_State() == 2){
                InputBlock_thread.blockProcess(nowInstruction,process);
            }
            else if(nowInstruction.getInstruc_State() == 3){
                OutputBlock_thread.blockProcess(nowInstruction,process);
            }

            //nowInstruction.nowtime = Clock_thread.COUNTTIME;
            Clock_thread.COUNTTIME++;
            //InputBlock_thread.CheckBlock(nowInstruction.nowtime,process);
//            InputBlock_thread.awakeInputBlock(process);
            process.IOflag = true;

        }
    }


    public void executeInstruction(Process process) throws InterruptedException {
        Instruction currentInstruction = new Instruction();
        Instruction[] ins = new Instruction[process.InstrucNum];
        List<Instruction[]> instructionList = new ArrayList<>(Instruction.InstructionQueue);
        //List<Instruction[]> instructionList = Instruction.InstructionQueue.stream().toList();
        ins = instructionList.get(process.getProcessID()-1);

        //ins = iterator.next();
//        for(Instruction[] instructions:Instruction.InstructionQueue){
//            if(instructions.length == process.InstrucNum){
//                ins = instructions;
//                break;
//            }
//        }
        //执行当前指令
        assert ins != null;
        Instruction nowInstruction = ins[process.PC-process.shoot];
        if((nowInstruction.getInstruc_State() == 2 || nowInstruction.getInstruc_State() == 3) && !process.IOflag){
            process.hardware = true;
        }
        int wakeUpTime = nowInstruction.getInRunTimes();
        process.nowInstruc_time = wakeUpTime;
        //判断当前指令运行几秒
        if(process.nowInstruc_time == 2){
            Times-=2;
        }else {
            Times-=1;
        }
        if(!process.hardware){
            if(Times<0){
                process.dead = true;
            }
            if(process.dead){
                return;
            }else if(!process.dead && process.nowInstruc_time == 1){
                runInstruction(nowInstruction,process);
            } else if (!process.dead && process.nowInstruc_time == 2) {
                runInstruction2(nowInstruction,process);
            }
            if(!block1Queue.isEmpty()){
                Process pcb = block1Queue.poll();
                InputBlock_thread.awakeInputBlock(pcb);
            }
            if(!block2Queue.isEmpty()){
                Process pcb = block2Queue.poll();
                OutputBlock_thread.awakeOutputBlock(pcb);
            }
        }else {
            InputBlock_thread.blockProcess(nowInstruction,process);
            Times = 3;
            //nowInstruction.nowtime = Clock_thread.COUNTTIME;
            Clock_thread.COUNTTIME++;
            //InputBlock_thread.CheckBlock(nowInstruction.nowtime,process);
//            InputBlock_thread.awakeInputBlock(process);
            process.IOflag = true;
        }

    }
//    public void blockProcess(Instruction nowInstruction,Process process){
//        if(nowInstruction.getInstruc_State() == 2){
//            block1Queue.add(process);
//            System.out.println(Clock_thread.COUNTTIME+":[阻塞进程:"+":阻塞队列1:"+process.getProcessID()+"]");
//        } else if (nowInstruction.getInstruc_State() == 3) {
//            block2Queue.add(process);
//            System.out.println(Clock_thread.COUNTTIME+":[阻塞进程:"+":阻塞队列2:"+process.getProcessID()+"]");
//        }
//    }
    public void runInstruction2_DJFK(Instruction nowInstruction,Process process) throws InterruptedException {
        int index = nowInstruction.getInstruc_ID();
        MMU mmu = new MMU(memory);
        //计算当前指令的逻辑地址和物理地址
        int instruct_logicalAddress = cpu.setLogicalAddress(index, process.ProID);
        int instruct_physicalAddress = mmu.getPhysicalAddress(process,instruct_logicalAddress);
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");
        Clock_thread.COUNTTIME++;
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");

        //通知其它线程更新时间
        Kernel.timeSyncCondition.signalAll();
        process.PC++;
        //唤醒其它线程
        Kernel.clkCondition.signalAll();
        //睡眠当前线程
        isSleep = true;
        Kernel.pstCondition.await();
        while(!Job.jobQueue.isEmpty() && !Instruction.InstructionQueue.isEmpty()){
            createPro_DJFK();
        }
    }

    private void runInstruction2(Instruction nowInstruction, Process process) throws InterruptedException {
        int index = nowInstruction.getInstruc_ID();
        MMU mmu = new MMU(memory);
        //计算当前指令的逻辑地址和物理地址
        int instruct_logicalAddress = cpu.setLogicalAddress(index, process.ProID);
        int instruct_physicalAddress = mmu.getPhysicalAddress(process,instruct_logicalAddress);
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");
        Clock_thread.COUNTTIME++;
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");

        //通知其它线程更新时间
        Kernel.timeSyncCondition.signalAll();
        process.PC++;
        //唤醒其它线程
        Kernel.clkCondition.signalAll();
        //睡眠当前线程
        isSleep = true;
        Kernel.pstCondition.await();
        while(!Job.jobQueue.isEmpty() && !Instruction.InstructionQueue.isEmpty()){
            createPro();
        }
    }
    public void runInstruction_DJFK(Instruction nowInstruction,Process process) throws InterruptedException {
        int index = nowInstruction.getInstruc_ID();
        MMU mmu = new MMU(memory);
        //计算当前指令的逻辑地址和物理地址
        int instruct_logicalAddress = cpu.setLogicalAddress(index, process.ProID);
        int instruct_physicalAddress = mmu.getPhysicalAddress(process,instruct_logicalAddress);
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");
        //Clock_thread.COUNTTIME++;

        //通知其它线程更新时间
        Kernel.timeSyncCondition.signalAll();
        process.PC++;
        //唤醒其它线程
        Kernel.clkCondition.signalAll();
        //睡眠当前线程
        isSleep = true;
        Kernel.pstCondition.await();
        while(!Job.jobQueue.isEmpty() && !Instruction.InstructionQueue.isEmpty()){
            createPro_DJFK();
        }
    }

    public void runInstruction(Instruction nowInstruction,Process process) throws InterruptedException {
        int index = nowInstruction.getInstruc_ID();
        MMU mmu = new MMU(memory);
        //计算当前指令的逻辑地址和物理地址
        int instruct_logicalAddress = cpu.setLogicalAddress(index, process.ProID);
        int instruct_physicalAddress = mmu.getPhysicalAddress(process,instruct_logicalAddress);
        System.out.println(Clock_thread.COUNTTIME+":[运行进程:"+process.ProID+":"+
                nowInstruction.getInstruc_ID()+":"+nowInstruction.getInstruc_State()+":"+instruct_logicalAddress+
                ":"+instruct_physicalAddress+"]");
        //Clock_thread.COUNTTIME++;

        //通知其它线程更新时间
        Kernel.timeSyncCondition.signalAll();
        process.PC++;
        //唤醒其它线程
        Kernel.clkCondition.signalAll();
        //睡眠当前线程
        isSleep = true;
        Kernel.pstCondition.await();
        while(!Job.jobQueue.isEmpty() && !Instruction.InstructionQueue.isEmpty()){
            createPro();
        }
    }

    //DJFK
    @Override
    public void run(){
        while(true){
            Kernel.lock.lock();
            try {
                Kernel.pstCondition.await();
                if((block1Queue.isEmpty() || block2Queue.isEmpty()) && queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty() && Job.jobQueue.isEmpty()){
                    System.out.println(Clock_thread.COUNTTIME+":[CPU 空闲]");
                    if(Clock_thread.COUNTTIME > 10){
                        FinalTime = Clock_thread.COUNTTIME-1;
                    }
                }else {
                    while(!Job.jobQueue.isEmpty()){
                        createPro_DJFK();
                    }
                    schedule_DJFK();

                    //唤醒其它线程
                    Kernel.clkCondition.signalAll();
                }

                Kernel.clkCondition.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                Kernel.lock.unlock();
            }
        }
    }

//    //SJP
//    @Override
//    public void run() {
//            while(true){
//                Kernel.lock.lock();
//                try {
//                    Kernel.pstCondition.await();
//                    if(Times == 0 || Clock_thread.COUNTTIME%10 == 0){
//                        Times = 3;//重置时间片
//                        //通知其它线程更新时间
//                        //Kernel.timeSyncCondition.signalAll();
//                        while(!Job.jobQueue.isEmpty() && !Instruction.InstructionQueue.isEmpty()){
//                            createPro();
//                        }
//                        if(!readyQueue.isEmpty()){
//                            schedule();
//                        }
//
//                        //唤醒其它线程
//                        Kernel.clkCondition.signalAll();
//                    }else {
//                        System.out.println(Clock_thread.COUNTTIME+":[CPU 空闲]");
//                        Times--;
//                    }
//
//                    Kernel.clkCondition.signal();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }finally {
//                    Kernel.lock.unlock();
//                }
//            }
//    }

    public void ProcessScheduling() throws InterruptedException {
        //进程调度函数
        createPro();
        schedule();
    }
}
