package kernel;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import UI.ui;

import static kernel.Instruction.InstructionQueue;
import static kernel.Job.jobQueue;


public class JobIn_thread implements Runnable{
    static volatile boolean isActivated = false;//开关变量，用于控制线程是否激活
    private ReentrantLock lock;
    private Condition jitCondition;
    public static Job[] jobArray = new Job[100];

    public JobIn_thread(ReentrantLock lock, Condition jitCondition) {
        this.lock = lock;
        this.jitCondition = jitCondition;
    }

    public static Job[] readNewJobsInputFile(String filepath, Job[] jobArray) {
        List<Job> jobList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while((line = reader.readLine()) != null){
                //将每一行按逗号分割成三个部分
                String[] parts = line.split(",");
                int JobsID = Integer.parseInt(parts[0].trim());
                int InTimes = Integer.parseInt(parts[1].trim());
                int InstrucNum = Integer.parseInt(parts[2].trim());

                //创建Job对象并添加到列表
                jobList.add(new Job(JobsID,InTimes,InstrucNum));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 如果已有的数组不为空，则将已有数组添加到新数组的尾部
        if (jobArray != null && jobArray.length > 0) {
            jobList.addAll(Arrays.asList(jobArray));
        }

        // 将新作业列表添加到已有数组的尾部
        jobList.addAll(Arrays.asList(jobArray));

        // 将列表转换为数组
        Job[] tArray = new Job[jobList.size()];
        return jobList.toArray(tArray);
    }

    @Override
    public void run() {
        //读取文件内容到临时数组
//        String filepath = "./input1/jobs-input.txt";
//        jobArray = readJobsInputFile(filepath);
        Job.jobQueue = new LinkedList<>();
        Instruction.InstructionQueue = new LinkedList<Instruction[]>();
        while(true){
            Kernel.lock.lock();
            try {
                while(!isActivated){
                    //初始休眠，保持各个线程的同步，由别人唤醒
                    Kernel.jitCondition.await();
                }
                CheckJob(jobArray);
                //PrintJob(jobArray);
                //PrintInstruction(InstructionQueue);

                //重置开关变量
                isActivated = false;

                //唤醒PST
                //Kernel.pstCondition.signal();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                Kernel.lock.unlock();
            }
        }
        }

    private void ClearJob(Job[] jobArray,int count) {
        for (int i = 0; i < count; i++) {
            jobArray[i] = null;
        }
    }

    private void PrintJob(Job[] jobs) {
        for (Job job:jobs){
            System.out.println("Job ID: " + job.getJobId());
            System.out.println("Job Request Time: " + job.getInTime());
            System.out.println("Number of Instructions: " + job.getInstrucNum());
            System.out.println("------------------------------");
        }
    }

    private void PrintInstruction(Queue<Instruction[]> InstructionQueue){
        // 输出放入指令队列的指令
        for (Instruction[] instructions: InstructionQueue) {
            for (Instruction instruction : instructions) {
                System.out.println("Instruction ID: " + instruction.getInstruc_ID());
                System.out.println("Instruction State: " + instruction.getInstruc_State());
                System.out.println("------------------------------");
            }
        }

    }


    private synchronized void CheckJob(Job[] tArray) throws FileNotFoundException {
        synchronized (this) {
            if (Clock_thread.COUNTTIME % 10 == 0 && Clock_thread.COUNTTIME != 0) {
                System.out.println(Clock_thread.COUNTTIME + ":[Job Request Check]");
                // 对作业数组按进入时间进行排序
                Arrays.sort(tArray, Comparator.comparingInt(job -> job.JobsID));
                for (int i = 0; i < tArray.length; i++) {
                    if (tArray[i].InTimes <= Clock_thread.COUNTTIME && !tArray[i].isCreated)  {
                        //System.out.println("第"+tArray[i].JobsID+"个作业满足时间请求");
                        //加入后备队列
                        //Job.jobQueue = new LinkedList<>();
                        jobQueue.offer(tArray[i]);
                        tArray[i].isCreated = true;
                        //System.out.println("第"+tArray[i].JobsID+"个作业加入后备队列");

                        //根据作业编号找到指令文件，将其内容保存到相应数据结构中
                        //Instruction.InstructionQueue = new LinkedList<Instruction[]>();
                        String filePath = "./input2/"+tArray[i].JobsID+".txt";
                        InstructionQueue.offer(readJobInstruction(tArray[i].JobsID,filePath));
                        //System.out.println("找到第"+tArray[i].JobsID+"号指令文件，内容放入指令队列中");

                        System.out.println(Clock_thread.COUNTTIME + ":[新建作业:" + tArray[i].JobsID + "," +
                                tArray[i].InTimes + "," + tArray[i].InstrucNum + "]");
                    }

                }

            }
        }

    }


    private Instruction[] readJobInstruction(int jobs_id,String filePath) throws FileNotFoundException {
        int inRunTimes = 0;
        List<Instruction> instructions = new ArrayList<>();
        final String DELIMITER = ",";
        //String filePath = "./input1/"+job_id+".txt";
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(DELIMITER);
                if(parts.length == 2){
                    int Instruc_ID = Integer.parseInt(parts[0].trim());
                    int Instruc_State = Integer.parseInt(parts[1].trim());
                    if(Instruc_State == 0){
                        inRunTimes = 1;
                    } else if (Instruc_State == 1) {
                        inRunTimes = 2;
                    } else if (Instruc_State == 2) {
                        inRunTimes = 2;
                    } else if (Instruc_State == 3) {
                        inRunTimes = 2;

                    }
                    Instruction instruction = new Instruction(Instruc_ID,Instruc_State,inRunTimes);
                    instructions.add(instruction);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;
        return instructions.toArray(new Instruction[0]);
    }

    static void activateThread() {
        isActivated = true;
        Kernel.jitCondition.signal();//激活线程
    }

    public static Job[] readJobsInputFile(String filepath) {
        List<Job> jobList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while((line = reader.readLine()) != null){
                //将每一行按逗号分割成三个部分
                String[] parts = line.split(",");
                int JobsID = Integer.parseInt(parts[0].trim());
                int InTimes = Integer.parseInt(parts[1].trim());
                int InstrucNum = Integer.parseInt(parts[2].trim());

                //创建Job对象并添加到列表
                jobList.add(new Job(JobsID,InTimes,InstrucNum));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将列表转换为数组
        Job[] tArray = new Job[jobList.size()];
        return jobList.toArray(tArray);
    }
}
