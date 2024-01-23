package kernel;

import java.util.LinkedList;
import java.util.Queue;

public class Job {
    int JobsID;
    int InTimes;//作业进入时间
    int InstrucNum;
    private String filePath;//保存jobs-input.txt文件的路径
    private Job[] tArray;//用于保存作业信息的临时数组
    public static Queue<Job> jobQueue = new LinkedList<>();//作业后备队列

    //public static Queue<Job> NewjobQueue = new LinkedList<>();

    public boolean isCreated = false;

    public Job(int jobsID, int inTimes, int instrucNum) {
        this.JobsID = jobsID;
        this.InTimes = inTimes;
        this.InstrucNum = instrucNum;
    }

    public int  getJobId() {
        return this.JobsID;
    }

    public int getInTime() {
        return this.InTimes;
    }

    public int getInstrucNum() {
        return this.InstrucNum;
    }

    public boolean isCreated() {
        return isCreated;
    }

}
