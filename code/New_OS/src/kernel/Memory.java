package kernel;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private static final int USER_MEMORY_SIZE = 16 * 1024; // 用户区内存大小，16KB
    private static final int PHYSICAL_BLOCK_SIZE = 1024; // 物理块大小，1024B
    private static final int LOGICAL_BLOCK_SIZE = 100; // 逻辑块大小，100B
    public static final int TOTAL_PHYSICAL_BLOCKS = 16; // 总物理块数，16个

    public static boolean[] bitMap;//位示图
    public static Map<Integer,Integer> processBaseAddress; //进程基地址


    public Memory(){
        int totalLogicalBlocks = USER_MEMORY_SIZE / LOGICAL_BLOCK_SIZE;
        bitMap = new boolean[TOTAL_PHYSICAL_BLOCKS];
        processBaseAddress = new HashMap<>();

        //初始化位示图，全为false,表示空闲
        for (int i = 0; i < TOTAL_PHYSICAL_BLOCKS; i++) {
            bitMap[i]= false;
        }
    }
    public boolean[] getBitMap() {
        return bitMap;
    }
    public int allocate(Process pcb,int size){
        //创建进程调用此方法
        int requiredBlocks = (int) Math.ceil((double) size / LOGICAL_BLOCK_SIZE);
        for (int i = 0; i < TOTAL_PHYSICAL_BLOCKS; i++) {
            if(!bitMap[i]){
                int j = i;
                while(j<i+requiredBlocks && j<TOTAL_PHYSICAL_BLOCKS && !bitMap[j]){
                    j++;
                }
                if(j == i+requiredBlocks){
                    //找到足够的连续空闲块
                    for(int k = i;k<j;k++){
                        bitMap[k] = true;
                    }
                    int processID = pcb.ProID;
                    int baseAddress = i;
                    //int baseAddress = i*PHYSICAL_BLOCK_SIZE;
                    processBaseAddress.put(processID,baseAddress);
                    return baseAddress;//返回基地址
                }else {
                    i = j;//跳过已占用的块
                }
            }
        }
        return -1;//内容不足
    }
    public void free(int baseAddress, int size) {
        //进程结束调用此方法
        int startBlock = baseAddress;
        int endBlock = startBlock + (int) Math.ceil((double) size / 10);
        for (int i = startBlock; i < endBlock && i < TOTAL_PHYSICAL_BLOCKS; i++) {
            bitMap[i] = false;
        }
    }

    public int getProcessBaseAddress(int pro_id){
        return processBaseAddress.getOrDefault(pro_id,-1);
    }

}
