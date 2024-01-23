package kernel;
import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private Map<Integer, Integer> pageTable;  // 页表，存储虚拟页号和物理页号的映射关系
    private Map<Integer, Integer> pagePermissions; // 存储虚拟页号和对应权限位的映射关系

    public PageTable() {
        pageTable = new HashMap<>();
        pagePermissions = new HashMap<>();
    }

    // 添加虚拟页与物理页的映射关系
    public void addMapping(int virtualPageNumber, int physicalPageNumber) {
        pageTable.put(virtualPageNumber, physicalPageNumber);
    }

    // 设置虚拟页的权限
    public void setPermissions(int virtualPageNumber, int permissions) {
        pagePermissions.put(virtualPageNumber, permissions);
    }

    // 根据虚拟页号获取物理页号
    public int getPhysicalPageNumber(int virtualPageNumber) {
        return pageTable.getOrDefault(virtualPageNumber, -1);
    }

    // 根据虚拟页号获取页面权限
    public int getPermissions(int virtualPageNumber) {
        return pagePermissions.getOrDefault(virtualPageNumber, 0);
    }

    // 页面置换算法：移除指定虚拟页号对应的页表项
    public void removeMapping(int virtualPageNumber) {
        pageTable.remove(virtualPageNumber);
        pagePermissions.remove(virtualPageNumber);
    }

    // 获取当前页表的大小（页表项数量）
    public int getSize() {
        return pageTable.size();
    }
}

