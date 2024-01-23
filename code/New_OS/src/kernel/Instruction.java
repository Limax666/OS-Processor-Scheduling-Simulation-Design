package kernel;

import java.util.List;
import java.util.Queue;

public class Instruction {
    private int Instruc_ID;//指令段编号
    private int Instruc_State;//用户程序指令的类型
    private int InRunTimes;//每条指令运行或唤醒时间
    public int nowtime;

    public static Queue<Instruction[]> InstructionQueue;

    public Instruction() {
        this.Instruc_ID=0;
        this.Instruc_State=0;
        this.InRunTimes = 0;
    }
    public Instruction(int instruc_ID,int instruc_State){
        Instruc_ID = instruc_ID;
        Instruc_State = instruc_State;
    }


    public Instruction(int instruc_ID, int instruc_State,int inRunTimes) {
        Instruc_ID = instruc_ID;
        Instruc_State = instruc_State;
        InRunTimes = inRunTimes;
    }

    public int getInstruc_ID() {
        return Instruc_ID;
    }

    public void setInstruc_ID(int instruc_ID) {
        Instruc_ID = instruc_ID;
    }

    public int getInstruc_State() {
        return this.Instruc_State;
    }

    public void setInstruc_State(int instruc_State) {
        Instruc_State = instruc_State;
    }

    public int getInRunTimes() {
        return this.InRunTimes;
    }

    public void setInRunTimes(int inRunTimes) {
        InRunTimes = inRunTimes;
    }
}
