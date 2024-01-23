package kernel;

public class MMU {
    private Memory memory;
    public MMU(Memory memory){
        this.memory = memory;
    }
    public int getPhysicalAddress(Process process,int logicalAddress){
        int baseAddress = memory.getProcessBaseAddress(process.ProID);
        if(baseAddress == -1){
            throw new RuntimeException("错误");
        }
        return baseAddress+logicalAddress;
    }
//    public int setLogicalAddress(int index,int pro_id){
//        return pro_id*10+index;
//    }
}
