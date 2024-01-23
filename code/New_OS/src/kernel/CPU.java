package kernel;

public class CPU {
    private int PC;//下一条指令编号
    private int IR;//当前指令编号
    private int PSW;//0用户态  1内核态
    public static final int USER_STATE = 0;
    public static final int KERNEL_STATE = 1;

    // 用于保存状态的变量
    private int savedPC;
    private int savedIR;
    private int savedPSW;

    public CPU(){
        this.PC = 1;
        this.IR = 0;
        this.PSW = 0;
    }
    public int getPSW(){
        return this.PSW;
    }

    //切换用户态
    public synchronized void changeToUserState(){
        if(this.PSW == USER_STATE){
            return;
        }
        this.PSW = USER_STATE;
    }

    //切换内核态
    public synchronized void changeToKernelState(){
        if(this.PSW == KERNEL_STATE){
            return;
        }
        this.PSW = KERNEL_STATE;
    }
    public int setLogicalAddress(int index,int pro_id){
        return pro_id*10+index;
    }

    public void allocate(CPU cpu,Process process){
        //将CPU分配给当前进程
        cpu.PC = process.PC;
        cpu.IR = process.IR;
        cpu.PSW = process.PSW;
    }

    public void CPU_PRO(){
        //CPU现场保护函数
        this.savedPC = this.PC;
        this.savedIR = this.IR;
        this.savedPSW = this.PSW;
    }

    public void CPU_REC(){
        //CPU现场恢复函数
        this.PC = this.savedPC;
        this.PSW = this.savedPSW;
        this.IR = this.savedIR;
    }
}
