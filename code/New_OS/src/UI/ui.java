/*
 * Created by JFormDesigner on Sun Oct 01 02:09:03 CST 2023
 */

package UI;
//import static kernel.Job.jobQueue;
//import static kernel.Process.readyQueue;
//import static kernel.Instruction.InstructionQueue;
import static kernel.Process.finishQueue;
import static kernel.Memory.processBaseAddress;
import static kernel.ProcessScheduling_thread.*;

import javax.swing.Timer;
import javax.swing.table.*;

import kernel.*;
import kernel.Process;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 59216
 */
public class ui extends JFrame {
    static int count = 7;
    private boolean isFileSelected = false;

    public Timer timer = null;
    //private JTextPane textPane;

    public ui() {
        initComponents();
        selectfile();
        middlespeed();
        highspeed();
        lowspeed();
        startshow();
        readNewJob();//新增作业
        saveshow();
        memoryshow();
        exitshow();
        timeshow();
        cpuStateshow();
        queue1show();
        queue2show();
        queue3show();
        jobQueueshow();//作业后备队列展示
        //readyQueueshow();//就绪队列展示
        finishshow();
        stateshow();
        blockQueueshow();
        block2Queueshow();
        //重定向
        redirectSystemOut();

    }

    

    public void openfile(){
        // 文件选择对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); // 设置默认打开当前目录
        int result = fileChooser.showOpenDialog(this); // 打开文件选择对话框
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filepath = selectedFile.getAbsolutePath(); // 获取选中的文件路径

            JobIn_thread.jobArray = JobIn_thread.readJobsInputFile(filepath); // 读取文件数据，返回Job数组

            // 在这里可以处理读取到的jobArray数据，进行后续逻辑操作
            isFileSelected = true;


        }
    }
    public void readNewJob(){
        addjobbutton.addActionListener(new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                addNewJob();
                String filepath = "./input2/newjobs"+ (count-1) + ".txt";
                JobIn_thread.jobArray = JobIn_thread.readNewJobsInputFile(filepath,JobIn_thread.jobArray);
            }
        });
    }
    public void selectfile(){
        selectfilebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openfile();
            }
        });

    }

    public void stateshow(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (finishQueue){
                    StringBuilder sb = new StringBuilder();
                    if(!finishQueue.isEmpty()){
                        for(Process process:finishQueue){
                            int endtime = process.getEndTimes();
                            String Info = String.format("[%d:%d+%d+%d]",process.getProcessID(),
                                    process.getInTimes(),process.getInTimes(),process.getRunTimes());
                            String line = String.format("%d:%s\n",endtime,Info);
                            sb.append(line);
                        }
                    }

                    statetextPane.setText(sb.toString());
                }
            }
        });
        timer.start();
    }


    private void timeshow() {
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timetextField.setText(String.valueOf(Clock_thread.COUNTTIME));
            }
        });
        timer.start();
    }
    
    public void jobQueueshow() {
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    //Queue<Job> copyJobQueue = new LinkedList<>(jobQueue); // 创建 jobQueue 的副本
                    //从Job类的jobQueue队列中获取全部后备作业
                    Queue<Job> backJobQueue = Job.jobQueue;
                    //创建用于存储后备作业信息的二维数组
                    Object[][] jobQueue_data = new Object[backJobQueue.size()][3];

                    int i = 0;
                    for(Job job:backJobQueue) {
                        jobQueue_data[i][0] = job.getJobId();
                        jobQueue_data[i][1] = job.getInTime();
                        jobQueue_data[i][2] = job.getInstrucNum();
                        i++;
                    }
                    // 创建表格模型，将后备作业信息二维数组加入模型中
                    DefaultTableModel jobQueue_model = new DefaultTableModel(jobQueue_data, new String[]{"作业号","请求时间","指令数目"});

                    backjobtable.setModel(jobQueue_model);
                    backjobtable.revalidate();
                    backjobtable.repaint();

                }
        });
        timer.start();
    }
//    public void readyQueueshow() {
//        timer = new Timer(100, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                synchronized (readyQueue) {
//                    if (!readyQueue.isEmpty()) {
//                        // 创建用于存储进程信息的二维数组
//                        Object[][] data = new Object[readyQueue.size()][5];
//                        Object[][] data1 = new Object[1][6];
//
//                        int index = 0;
//                        for (Process process : readyQueue) {
//                            data[index][0] = process.getProcessID();
//                            data[index][1] = process.getProcessPriority();
//                            //currentProcess.InstrucNum-currentProcess.PC
//                            data[index][2] = process.getProcessInstrucNum()-process.getProcessPC();
//                            data[index][3] = process.getProcessIR();
//                            data[index][4] = process.getProcessPC();
//
//                            Instruction[][] instructionQueueCopy;
//                            synchronized (InstructionQueue) {
//                                instructionQueueCopy = InstructionQueue.toArray(new Instruction[InstructionQueue.size()][]);
//                            }
//
//                            for (Instruction[] instructions : instructionQueueCopy) {
//                                data1[0][0] = process.getProcessID();
//                                data1[0][1] = process.getProcessPriority();
//                                data1[0][2] = instructions[process.ProID].getInstruc_ID();
//                                data1[0][3] = instructions[process.ProID].getInstruc_ID();
//                                data1[0][4] = processBaseAddress.get(process.ProID);
//                                data1[0][5] = instructions[process.ProID].getInRunTimes();
//                            }
//
//                            index++;
//                        }
//                        // 创建表格模型，将进程信息二维数组加入模型中
//                        DefaultTableModel model = new DefaultTableModel(data, new String[]{"进程编号", "优先级", "指令数", "IR", "PC"});
//                        DefaultTableModel model1 = new DefaultTableModel(data1,new String[]{"进程ID","进程优先级","IR","PC","基地址","剩余时间"});
//
//                        Readytable.setModel(model);
//                        processtable.setModel(model1);
//                    }
//                }
//            }
//        });
//        timer.start();
//    }

    public void finishshow(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (finishQueue){
                    if(!finishQueue.isEmpty()){
                        Object[][] finish_data = new Object[finishQueue.size()][4];

                        int index = 0;
                        for(Process process:finishQueue){
                            finish_data[index][0] = process.getProcessID();
                            finish_data[index][1] = process.getEndTimes();
                            finish_data[index][2] = process.getRunTimes();
                            finish_data[index][3] = process.getTurnTimes();

                            index++;
                        }
                        DefaultTableModel finish_model = new DefaultTableModel(finish_data, new String[]{"进程ID", "撤销时间", "运行时间", "周转时间"});
                        finishtable.setModel(finish_model);
                    }
                }
            }
        });
        timer.start();
    }
    public void queue1show(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if(!queue1.isEmpty()){
                        Object[][] queue1_data = new Object[queue1.size()][1];
                        Object[][] data1 = new Object[1][6];
                        
                        int index = 0;
                        for(Process process : queue1){
                            queue1_data[index][0] = process.getProcessID();
                            
                            index++;
                            // 将相关的instruction信息存储到data1中
                            synchronized (Instruction.InstructionQueue) {
                                for (Instruction[] instructions : Instruction.InstructionQueue) {
                                    data1[0][0] = process.getProcessID();
                                    data1[0][1] = process.getProcessPriority();
                                    data1[0][2] = instructions[process.getProcessID()].getInstruc_ID();
                                    data1[0][3] = instructions[process.getProcessID()].getInstruc_ID();
                                    data1[0][4] = processBaseAddress.get(process.getProcessID());
                                    data1[0][5] = instructions[process.getProcessID()].getInRunTimes();
                                }
                            }
                            
                        }

                        // 创建表格模型，将进程信息二维数组加入模型中
                        DefaultTableModel queue1_model = new DefaultTableModel(queue1_data, new String[]{"进程ID"});
                        DefaultTableModel model1 = new DefaultTableModel(data1,new String[]{"进程ID","进程优先级","IR","PC","基地址","剩余时间"});

                        queue1table.setModel(queue1_model);
                        Processtable.setModel(model1);

                        queue1table.repaint();
                        Processtable.repaint();
                    }
                }
        });
        timer.start();
    }
    public void queue2show(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (queue2){
                    if(!queue2.isEmpty()){
                        Object[][] queue2_data = new Object[queue2.size()][1];
                        Object[][] data1 = new Object[1][6];

                        int index = 0;
                        for(Process process : queue2){
                            queue2_data[index][0] = process.getProcessID();

                            index++;
                            // 将相关的instruction信息存储到data1中
                            if(queue1.isEmpty()){
                                synchronized (Instruction.InstructionQueue) {
                                    for (Instruction[] instructions : Instruction.InstructionQueue) {
                                        data1[0][0] = process.getProcessID();
                                        data1[0][1] = process.getProcessPriority();
                                        data1[0][2] = instructions[process.getProcessID()].getInstruc_ID();
                                        data1[0][3] = instructions[process.getProcessID()].getInstruc_ID();
                                        data1[0][4] = processBaseAddress.get(process.getProcessID());
                                        data1[0][5] = instructions[process.getProcessID()].getInRunTimes();
                                    }
                                }
                            }

                        }

                        // 创建表格模型，将进程信息二维数组加入模型中
                        DefaultTableModel queue2_model = new DefaultTableModel(queue2_data, new String[]{"进程ID"});
                        queue2table.setModel(queue2_model);
                        queue2table.repaint();
                        if(queue1.isEmpty()){
                            DefaultTableModel model1 = new DefaultTableModel(data1,new String[]{"进程ID","进程优先级","IR","PC","基地址","剩余时间"});
                            Processtable.setModel(model1);
                            Processtable.repaint();
                        }

                    }
                }
            }
        });
        timer.start();
    }
    public void queue3show(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (queue3){
                    if(!queue3.isEmpty()){
                        Object[][] queue3_data = new Object[queue3.size()][1];
                        Object[][] data1 = new Object[1][6];

                        int index = 0;
                        for(Process process : queue3){
                            queue3_data[index][0] = process.getProcessID();

                            index++;
                            // 将相关的instruction信息存储到data1中
                            if(queue1.isEmpty() && queue2.isEmpty()){
                                synchronized (Instruction.InstructionQueue) {
                                    for (Instruction[] instructions : Instruction.InstructionQueue) {
                                        data1[0][0] = process.getProcessID();
                                        data1[0][1] = process.getProcessPriority();
                                        data1[0][2] = instructions[process.getProcessID()].getInstruc_ID();
                                        data1[0][3] = instructions[process.getProcessID()].getInstruc_ID();
                                        data1[0][4] = processBaseAddress.get(process.getProcessID());
                                        data1[0][5] = instructions[process.getProcessID()].getInRunTimes();
                                    }
                                }
                            }

                        }

                        // 创建表格模型，将进程信息二维数组加入模型中
                        DefaultTableModel queue3_model = new DefaultTableModel(queue3_data, new String[]{"进程ID"});
                        queue3table.setModel(queue3_model);
                        queue3table.repaint();

                        if(queue1.isEmpty() && queue2.isEmpty()){
                            DefaultTableModel model1 = new DefaultTableModel(data1,new String[]{"进程ID","进程优先级","IR","PC","基地址","剩余时间"});
                            Processtable.setModel(model1);
                            Processtable.repaint();
                        }
                    }
                }
            }
        });
        timer.start();
    }
//    public void readyQueueshow() {
//        timer = new Timer(100, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                synchronized (Process.readyQueue) {
//                    if (!Process.readyQueue.isEmpty()) {
//                        // 创建用于存储进程信息的二维数组
//                        Object[][] data = new Object[Process.readyQueue.size()][5];
//                        Object[][] data1 = new Object[1][6];
//
//                        int index = 0;
//                        for (Process process : Process.readyQueue) {
//                            data[index][0] = process.getProcessID();
//                            data[index][1] = process.getProcessPriority();
//                            data[index][2] = process.getProcessInstrucNum()-process.getProcessPC()+process.getProcessShoot();
//                            data[index][3] = process.getProcessIR();
//                            data[index][4] = process.getProcessPC();
//
//                            index++;
//
//                            // 将相关的instruction信息存储到data1中
//                            synchronized (Instruction.InstructionQueue) {
//                                for (Instruction[] instructions : Instruction.InstructionQueue) {
//                                    data1[0][0] = process.getProcessID();
//                                    data1[0][1] = process.getProcessPriority();
//                                    data1[0][2] = instructions[process.getProcessID()].getInstruc_ID();
//                                    data1[0][3] = instructions[process.getProcessID()].getInstruc_ID();
//                                    data1[0][4] = processBaseAddress.get(process.getProcessID());
//                                    data1[0][5] = instructions[process.getProcessID()].getInRunTimes();
//                                }
//                            }
//
//                        }
//                        // 创建表格模型，将进程信息二维数组加入模型中
//                        DefaultTableModel model = new DefaultTableModel(data, new String[]{"进程编号", "优先级", "指令数", "IR", "PC"});
//                        DefaultTableModel model1 = new DefaultTableModel(data1,new String[]{"进程ID","进程优先级","IR","PC","基地址","剩余时间"});
//
//                        Readytable.setModel(model);
//                        Processtable.setModel(model1);
//
//                        Readytable.repaint();
//                        Processtable.repaint();
//                    }
//                }
//            }
//        });
//        timer.start();
//    }
    
    
    public void blockQueueshow(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (Process.block1Queue){
                    if(!Process.block1Queue.isEmpty()){
                        Object[][] block_data = new Object[Process.block1Queue.size()][3];
                        
                        int index = 0;
                        for(Process process:Process.block1Queue){
                            block_data[index][0] = process.getProcessID();
                            block_data[index][1] = Clock_thread.COUNTTIME;
                            block_data[index][2] = process.getRunTimes();
                            
                            index++;
                        }
                        DefaultTableModel block_model = new DefaultTableModel(block_data, new String[]{"进程ID", "阻塞时间","剩余时间"});
                        blocktable.setModel(block_model);
                    }
                }
            }
        });
        timer.start();
        
    }
    public void block2Queueshow(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (Process.block2Queue){
                    if(!Process.block2Queue.isEmpty()){
                        Object[][] block2_data = new Object[Process.block2Queue.size()][3];

                        int index = 0;
                        for(Process process:Process.block2Queue){
                            block2_data[index][0] = process.getProcessID();
                            block2_data[index][1] = Clock_thread.COUNTTIME;
                            block2_data[index][2] = process.getRunTimes();
                            
                            index++;
                        }

                        DefaultTableModel block2_model = new DefaultTableModel(block2_data, new String[]{"进程ID", "阻塞时间","剩余时间"});
                        block2table.setModel(block2_model);
                    }
                }
            }
        });
        timer.start();
    }
    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                appendText(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendText(new String(b, off, len));
            }
        };

        PrintStream printStream = new PrintStream(out);
        System.setOut(printStream);
    }
    private void appendText(String text) {
        // 在JTextPane的末尾追加文本
        SwingUtilities.invokeLater(() -> {
            JPtextPane.setText(JPtextPane.getText() + text);
            JPtextPane.setCaretPosition(JPtextPane.getDocument().getLength());
        });
    }
    private void start(){
        //开始
        Kernel.main();
    }
//    private void addJob(){
//        //新增作业
//        readNewJob();
//    }
//    private void savestate(){
//        //保存状态信息
//        timer = new Timer(100, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String fileName = "ProcessResults-"+(String.valueOf(Clock_thread.COUNTTIME))+"-SJP.txt";
//                String filePath = "output1"+ File.separator+fileName;
//                //获取状态信息
//                String stateInfo = generateStateInfo();
//                //创建文件并写入内容
//                File file = new File(filePath);
//                try {
//                    FileWriter writer = new FileWriter(file);
//                    writer.write(stateInfo);
//                    writer.close();
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
//        timer.start();
//    }

    private void save(){
        //保存
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //构建文件名

                String fileName = "ProcessResults-"+(String.valueOf(Clock_thread.COUNTTIME))+"-DJFK.txt";
                String filePath = "output2"+ File.separator+fileName;

                //获取JPTextPane内容
                String content = JPtextPane.getText();
                String statecontent = statetextPane.getText();
                //添加状态统计信息
                String newStateContent = "\n状态统计信息:\n" + statecontent;
                StringBuilder block1RecordContent = new StringBuilder("BB1:[阻塞队列1,键盘输入:");
                StringBuilder block2RecordContent = new StringBuilder("\nBB2:[阻塞队列2,屏幕显示:");

                for (int i = 0;i<InputBlock_thread.block1ProcessIds.size();i++) {
                    int processId = InputBlock_thread.block1ProcessIds.get(i);
                    int startTimes = InputBlock_thread.block1EntryTimes.get(i);
                    block1RecordContent.append(startTimes).append(",").append(processId).append("/");
                }
                for (int i = 0;i<OutputBlock_thread.block2ProcessIds.size();i++) {
                    int processId = OutputBlock_thread.block2ProcessIds.get(i);
                    int startTimes = OutputBlock_thread.block2EntryTimes.get(i);
                    block2RecordContent.append(startTimes).append(",").append(processId).append("/");
                }

                block1RecordContent.setCharAt(block1RecordContent.length() - 1, ']');
                block2RecordContent.setCharAt(block2RecordContent.length() - 1, ']');

                //创建文件并写入内容
                File file = new File(filePath);
                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write(content);
                    writer.write(newStateContent);
                    writer.write(String.valueOf(block1RecordContent));
                    writer.write(String.valueOf(block2RecordContent));
                    writer.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        timer.start();
    }

    public void addNewJob() {
        Random random = new Random();
        int n = random.nextInt(2) * 10 + 10; // 生成 10、20
        int[][] jobs = new int[1][3];
        jobs[0][0] = count;
        jobs[0][1] = 20;
        jobs[0][2] = n;

        // 生成指令信息
        int[][] ins = new int[jobs[0][2]][2];
        for (int i = 0; i < jobs[0][2]; i++) {
            ins[i][0] = i + 1;
            ins[i][1] = random.nextInt(4);
        }

        // 保存作业信息到文件中
        String jobFileName = String.format("newjobs%d.txt", count);
        String jobFilePath = "./input2/" + jobFileName;
        File jobFile = new File(jobFilePath);
        try {
            FileWriter jobWriter = new FileWriter(jobFile);
            for (int i = 0; i < jobs.length; i++) {
                int[] job = jobs[i];
                if (i == jobs.length - 1) {
                    jobWriter.write(String.format("%d,%d,%d", job[0], job[1], job[2]));
                } else {
                    jobWriter.write(String.format("%d,%d,%d\n", job[0], job[1], job[2]));
                }
            }
            jobWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // 保存指令信息到文件中
        String insFileName = String.format("%d.txt", count);
        String insFilePath = "./input2/" + insFileName;
        File insFile = new File(insFilePath);
        try {
            FileWriter insWriter = new FileWriter(insFile);
            for (int i = 0; i < ins.length; i++) {
                int[] in = ins[i];
                if (i == ins.length - 1) {
                    insWriter.write(String.format("%d,%d", in[0], in[1]));
                } else {
                    insWriter.write(String.format("%d,%d\n", in[0], in[1]));
                }
            }
            insWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        // 新增一次作业++一次
        count++;
    }
    public void memoryshow() {
        Memory memory = ProcessScheduling_thread.memory;

        memorytable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int index = row * 16 + column;
                boolean isUsed = memory.bitMap[index];
                cell.setBackground(isUsed ? Color.RED : Color.GREEN);
                return cell;
            }
        });

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 更新单元格背景颜色
                for (int row = 0; row < memorytable.getRowCount(); row++) {
                    for (int col = 0; col < memorytable.getColumnCount(); col++) {
                        int index = row * 16 + col; // 计算位示图索引
                        boolean isUsed = memory.bitMap[index]; // 获取位示图值
                        memorytable.setValueAt(isUsed, row, col); // 设置单元格的值
                    }
                }
                // 更新表格视图
                memorytable.repaint();
            }
        });
        timer.start();
    }

    private void exit(){
        //退出
        System.exit(0);
    }
    public void startshow() {
        startbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFileSelected) {
                    if (isSpeedSelected()) {
                        start();
                    } else {
                        JOptionPane.showMessageDialog(null, "请先选择速度!", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请先选择文件!", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public boolean isSpeedSelected() {
        return highradioButton.isSelected() || middleradioButton.isSelected() || lowradioButton.isSelected();
    }
//    public void addjobshow(){
//        addjobbutton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addJob();
//            }
//        });
//    }
    public void saveshow(){
        savebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
    }
    public void exitshow(){
        exitbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }
    public void cpuStateshow(){
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Clock_thread.COUNTTIME < 10){
                    cpuStatetextField.setText("空闲");
                }else {
                    cpuStatetextField.setText("忙碌");
                }
            }
        });
        timer.start();
    }
    public void highspeed(){
        highradioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setClockSpeed(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void middlespeed(){
        middleradioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setClockSpeed(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void lowspeed(){
        lowradioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setClockSpeed(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void setClockSpeed(int time) throws InterruptedException {
        Clock_thread.speedTime = time;
    }


    private void initComponents() {

        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license 
        Buttonpanel = new JPanel();
        startbutton = new JButton();
        addjobbutton = new JButton();
        savebutton = new JButton();
        exitbutton = new JButton();
        Timepanel = new JPanel();
        timelabel = new JLabel();
        timetextField = new JTextField();
        cpuStatelabel = new JLabel();
        cpuStatetextField = new JTextField();
        Processpanel = new JPanel();
        Processlabel = new JLabel();
        ProcessscrollPane = new JScrollPane();
        processtable = new JTable();
        Readypanel = new JPanel();
        Readylabel = new JLabel();
        ReadyscrollPane = new JScrollPane();
        Readytable = new JTable();
        backjobpanel = new JPanel();
        label2 = new JLabel();
        backjobscrollPane = new JScrollPane();
        backjobtable = new JTable();
        scrollPane2 = new JScrollPane();
        Processtable = new JTable();
        scrollPane1 = new JScrollPane();
        das = new JTextPane();
        JPpanel = new JPanel();
        JPlabel = new JLabel();
        JPscrollPane = new JScrollPane();
        JPtextPane = new JTextPane();
        speedpanel = new JPanel();
        highradioButton = new JRadioButton();
        middleradioButton = new JRadioButton();
        lowradioButton = new JRadioButton();
        speedlabel = new JLabel();
        finishpanel = new JPanel();
        finishlabel = new JLabel();
        finishscrollPane = new JScrollPane();
        finishtable = new JTable();
        statepanel = new JPanel();
        statelabel = new JLabel();
        statescrollPane = new JScrollPane();
        statetextPane = new JTextPane();
        memorypanel = new JPanel();
        memoryscrollPane = new JScrollPane();
        memorytable = new JTable();
        selectfilepanel = new JPanel();
        selectfilebutton = new JButton();
        blockpanel = new JPanel();
        blockscrollPane = new JScrollPane();
        blocktable = new JTable();
        blocklabel = new JLabel();
        block2panel = new JPanel();
        block2label = new JLabel();
        block2scrollPane = new JScrollPane();
        block2table = new JTable();
        queuepanel = new JPanel();
        queue1label = new JLabel();
        scrollPane3 = new JScrollPane();
        queue1table = new JTable();
        queue2label = new JLabel();
        scrollPane4 = new JScrollPane();
        queue2table = new JTable();
        queue3label = new JLabel();
        queue3scrollPane = new JScrollPane();
        queue3table = new JTable();

        //======== this ========
        Container contentPane = getContentPane();
        //var contentPane = getContentPane();

        //======== Buttonpanel ========
        {
            Buttonpanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border.
            EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder. CENTER, javax. swing
            . border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ),
            java. awt. Color. red) ,Buttonpanel. getBorder( )) ); Buttonpanel. addPropertyChangeListener (new java. beans. PropertyChangeListener( )
            { @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .equals (e .getPropertyName () ))
            throw new RuntimeException( ); }} );

            //---- startbutton ----
            startbutton.setText("\u5f00\u59cb");

            //---- addjobbutton ----
            addjobbutton.setText("\u65b0\u589e\u4f5c\u4e1a");

            //---- savebutton ----
            savebutton.setText("\u4fdd\u5b58");

            //---- exitbutton ----
            exitbutton.setText("\u9000\u51fa");

            GroupLayout ButtonpanelLayout = new GroupLayout(Buttonpanel);
            Buttonpanel.setLayout(ButtonpanelLayout);
            ButtonpanelLayout.setHorizontalGroup(
                ButtonpanelLayout.createParallelGroup()
                    .addGroup(ButtonpanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(ButtonpanelLayout.createParallelGroup()
                            .addComponent(addjobbutton)
                            .addComponent(startbutton))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(ButtonpanelLayout.createParallelGroup()
                            .addComponent(exitbutton, GroupLayout.Alignment.TRAILING)
                            .addComponent(savebutton, GroupLayout.Alignment.TRAILING))
                        .addGap(35, 35, 35))
            );
            ButtonpanelLayout.setVerticalGroup(
                ButtonpanelLayout.createParallelGroup()
                    .addGroup(ButtonpanelLayout.createSequentialGroup()
                        .addContainerGap(29, Short.MAX_VALUE)
                        .addGroup(ButtonpanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(exitbutton)
                            .addComponent(startbutton))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(ButtonpanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(savebutton)
                            .addComponent(addjobbutton)))
            );
        }

        //======== Timepanel ========
        {

            //---- timelabel ----
            timelabel.setText("\u7cfb\u7edf\u65f6\u949f");

            //---- timetextField ----
            timetextField.setEditable(false);
            timetextField.setEnabled(false);

            //---- cpuStatelabel ----
            cpuStatelabel.setText("CPU\u72b6\u6001");

            //---- cpuStatetextField ----
            cpuStatetextField.setEditable(false);
            cpuStatetextField.setEnabled(false);

            GroupLayout TimepanelLayout = new GroupLayout(Timepanel);
            Timepanel.setLayout(TimepanelLayout);
            TimepanelLayout.setHorizontalGroup(
                TimepanelLayout.createParallelGroup()
                    .addGroup(TimepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(TimepanelLayout.createParallelGroup()
                            .addGroup(TimepanelLayout.createSequentialGroup()
                                .addComponent(timelabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timetextField, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                            .addGroup(TimepanelLayout.createSequentialGroup()
                                .addComponent(cpuStatelabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cpuStatetextField)))
                        .addContainerGap())
            );
            TimepanelLayout.setVerticalGroup(
                TimepanelLayout.createParallelGroup()
                    .addGroup(TimepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(timelabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(TimepanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, TimepanelLayout.createSequentialGroup()
                                .addComponent(cpuStatelabel)
                                .addGap(25, 25, 25))
                            .addGroup(GroupLayout.Alignment.TRAILING, TimepanelLayout.createSequentialGroup()
                                .addComponent(cpuStatetextField, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(TimepanelLayout.createSequentialGroup()
                        .addComponent(timetextField, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
        }

        //======== Processpanel ========
        {

            //---- Processlabel ----
            Processlabel.setText("\u8fd0\u884c\u8fdb\u7a0b");

            //======== ProcessscrollPane ========
            {

                //---- processtable ----
                processtable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID", "\u8fdb\u7a0b\u4f18\u5148\u7ea7", "IR", "PC", "\u57fa\u5730\u5740", "\u5269\u4f59\u65f6\u95f4"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                ProcessscrollPane.setViewportView(processtable);
            }

            GroupLayout ProcesspanelLayout = new GroupLayout(Processpanel);
            Processpanel.setLayout(ProcesspanelLayout);
            ProcesspanelLayout.setHorizontalGroup(
                ProcesspanelLayout.createParallelGroup()
                    .addGroup(ProcesspanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Processlabel, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(298, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, ProcesspanelLayout.createSequentialGroup()
                        .addContainerGap(12, Short.MAX_VALUE)
                        .addComponent(ProcessscrollPane, GroupLayout.PREFERRED_SIZE, 338, GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
            );
            ProcesspanelLayout.setVerticalGroup(
                ProcesspanelLayout.createParallelGroup()
                    .addGroup(ProcesspanelLayout.createSequentialGroup()
                        .addComponent(Processlabel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ProcessscrollPane, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== Readypanel ========
        {

            //---- Readylabel ----
            Readylabel.setText("\u5c31\u7eea\u961f\u5217(SJP)");

            //======== ReadyscrollPane ========
            {

                //---- Readytable ----
                Readytable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0b\u7f16\u53f7", "\u4f18\u5148\u7ea7", "\u6307\u4ee4\u6570", "IR", "PC"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                ReadyscrollPane.setViewportView(Readytable);
            }

            GroupLayout ReadypanelLayout = new GroupLayout(Readypanel);
            Readypanel.setLayout(ReadypanelLayout);
            ReadypanelLayout.setHorizontalGroup(
                ReadypanelLayout.createParallelGroup()
                    .addGroup(ReadypanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(ReadypanelLayout.createParallelGroup()
                            .addComponent(ReadyscrollPane, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Readylabel, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(17, Short.MAX_VALUE))
            );
            ReadypanelLayout.setVerticalGroup(
                ReadypanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, ReadypanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Readylabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ReadyscrollPane, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== backjobpanel ========
        {

            //---- label2 ----
            label2.setText("\u540e\u5907\u4f5c\u4e1a\u961f\u5217");

            //======== backjobscrollPane ========
            {

                //---- backjobtable ----
                backjobtable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u4f5c\u4e1a\u53f7", "\u8bf7\u6c42\u65f6\u95f4", "\u6307\u4ee4\u6570\u76ee"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                backjobscrollPane.setViewportView(backjobtable);
            }

            GroupLayout backjobpanelLayout = new GroupLayout(backjobpanel);
            backjobpanel.setLayout(backjobpanelLayout);
            backjobpanelLayout.setHorizontalGroup(
                backjobpanelLayout.createParallelGroup()
                    .addGroup(backjobpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(backjobpanelLayout.createParallelGroup()
                            .addComponent(label2)
                            .addComponent(backjobscrollPane, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(19, Short.MAX_VALUE))
            );
            backjobpanelLayout.setVerticalGroup(
                backjobpanelLayout.createParallelGroup()
                    .addGroup(backjobpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(backjobscrollPane, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== scrollPane2 ========
        {

            //---- Processtable ----
            Processtable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null, null, null},
                },
                new String[] {
                    "\u8fdb\u7a0bID", "\u8fdb\u7a0b\u4f18\u5148\u7ea7", "IR", "PC", "\u57fa\u5730\u5740", "\u5269\u4f59\u65f6\u95f4"
                }
            ) {
                Class<?>[] columnTypes = new Class<?>[] {
                    Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
                };
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
            scrollPane2.setViewportView(Processtable);
        }

        //======== scrollPane1 ========
        {

            //---- das ----
            das.setEditable(false);
            das.setEnabled(false);
            scrollPane1.setViewportView(das);
        }

        //======== JPpanel ========
        {

            //---- JPlabel ----
            JPlabel.setText("\u4f5c\u4e1a/\u8fdb\u7a0b\u8c03\u5ea6\u4e8b\u4ef6");

            //======== JPscrollPane ========
            {

                //---- JPtextPane ----
                JPtextPane.setEditable(false);
                JPtextPane.setEnabled(false);
                JPscrollPane.setViewportView(JPtextPane);
            }

            GroupLayout JPpanelLayout = new GroupLayout(JPpanel);
            JPpanel.setLayout(JPpanelLayout);
            JPpanelLayout.setHorizontalGroup(
                JPpanelLayout.createParallelGroup()
                    .addGroup(JPpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(JPpanelLayout.createParallelGroup()
                            .addComponent(JPscrollPane)
                            .addGroup(JPpanelLayout.createSequentialGroup()
                                .addComponent(JPlabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 102, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            JPpanelLayout.setVerticalGroup(
                JPpanelLayout.createParallelGroup()
                    .addGroup(JPpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(JPlabel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JPscrollPane, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== speedpanel ========
        {

            //---- highradioButton ----
            highradioButton.setText("\u9ad8");

            //---- middleradioButton ----
            middleradioButton.setText("\u4e2d");

            //---- lowradioButton ----
            lowradioButton.setText("\u4f4e");

            //---- speedlabel ----
            speedlabel.setText("\u7cfb\u7edf\u8fd0\u884c\u901f\u5ea6");

            GroupLayout speedpanelLayout = new GroupLayout(speedpanel);
            speedpanel.setLayout(speedpanelLayout);
            speedpanelLayout.setHorizontalGroup(
                speedpanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, speedpanelLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(speedpanelLayout.createParallelGroup()
                            .addComponent(lowradioButton)
                            .addComponent(middleradioButton)
                            .addComponent(highradioButton))
                        .addContainerGap())
                    .addGroup(speedpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(speedlabel)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            speedpanelLayout.setVerticalGroup(
                speedpanelLayout.createParallelGroup()
                    .addGroup(speedpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(speedlabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(highradioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(middleradioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lowradioButton)
                        .addContainerGap(16, Short.MAX_VALUE))
            );
        }

        //======== finishpanel ========
        {

            //---- finishlabel ----
            finishlabel.setText("\u5df2\u5b8c\u6210\u8fdb\u7a0b");

            //======== finishscrollPane ========
            {

                //---- finishtable ----
                finishtable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID", "\u64a4\u9500\u65f6\u95f4", "\u8fd0\u884c\u65f6\u95f4", "\u5468\u8f6c\u65f6\u95f4"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                finishscrollPane.setViewportView(finishtable);
            }

            GroupLayout finishpanelLayout = new GroupLayout(finishpanel);
            finishpanel.setLayout(finishpanelLayout);
            finishpanelLayout.setHorizontalGroup(
                finishpanelLayout.createParallelGroup()
                    .addGroup(finishpanelLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(finishpanelLayout.createParallelGroup()
                            .addComponent(finishlabel)
                            .addComponent(finishscrollPane, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
            );
            finishpanelLayout.setVerticalGroup(
                finishpanelLayout.createParallelGroup()
                    .addGroup(finishpanelLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(finishlabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(finishscrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
            );
        }

        //======== statepanel ========
        {

            //---- statelabel ----
            statelabel.setText("\u72b6\u6001\u7edf\u8ba1\u4fe1\u606f");

            //======== statescrollPane ========
            {
                statescrollPane.setViewportView(statetextPane);
            }

            GroupLayout statepanelLayout = new GroupLayout(statepanel);
            statepanel.setLayout(statepanelLayout);
            statepanelLayout.setHorizontalGroup(
                statepanelLayout.createParallelGroup()
                    .addGroup(statepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(statepanelLayout.createParallelGroup()
                            .addComponent(statescrollPane, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
                            .addComponent(statelabel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            statepanelLayout.setVerticalGroup(
                statepanelLayout.createParallelGroup()
                    .addGroup(statepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statelabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statescrollPane, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        //======== memorypanel ========
        {

            GroupLayout memorypanelLayout = new GroupLayout(memorypanel);
            memorypanel.setLayout(memorypanelLayout);
            memorypanelLayout.setHorizontalGroup(
                memorypanelLayout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
            memorypanelLayout.setVerticalGroup(
                memorypanelLayout.createParallelGroup()
                    .addGap(0, 145, Short.MAX_VALUE)
            );
        }

        //======== memoryscrollPane ========
        {

            //---- memorytable ----
            memorytable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                },
                new String[] {
                    "\u7269\u7406\u57571", "\u7269\u7406\u57572", "\u7269\u7406\u57573", "\u7269\u7406\u57574", "\u7269\u7406\u57575", "\u7269\u7406\u57576", "\u7269\u7406\u57577", "\u7269\u7406\u57578", "\u7269\u7406\u57579", "\u7269\u7406\u575710", "\u7269\u7406\u575711", "\u7269\u7406\u575712", "\u7269\u7406\u575713", "\u7269\u7406\u575714", "\u7269\u7406\u575715", "\u7269\u7406\u575716"
                }
            ));
            memorytable.setEnabled(false);
            memoryscrollPane.setViewportView(memorytable);
        }

        //======== selectfilepanel ========
        {

            //---- selectfilebutton ----
            selectfilebutton.setText("\u9009\u62e9\u6587\u4ef6");

            GroupLayout selectfilepanelLayout = new GroupLayout(selectfilepanel);
            selectfilepanel.setLayout(selectfilepanelLayout);
            selectfilepanelLayout.setHorizontalGroup(
                selectfilepanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, selectfilepanelLayout.createSequentialGroup()
                        .addContainerGap(16, Short.MAX_VALUE)
                        .addComponent(selectfilebutton)
                        .addContainerGap())
            );
            selectfilepanelLayout.setVerticalGroup(
                selectfilepanelLayout.createParallelGroup()
                    .addGroup(selectfilepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(selectfilebutton)
                        .addContainerGap(64, Short.MAX_VALUE))
            );
        }

        //======== blockpanel ========
        {

            //======== blockscrollPane ========
            {

                //---- blocktable ----
                blocktable.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID", "\u963b\u585e\u65f6\u95f4", "\u5269\u4f59\u65f6\u95f4"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                blockscrollPane.setViewportView(blocktable);
            }

            //---- blocklabel ----
            blocklabel.setText("\u963b\u585e\u961f\u52171");

            GroupLayout blockpanelLayout = new GroupLayout(blockpanel);
            blockpanel.setLayout(blockpanelLayout);
            blockpanelLayout.setHorizontalGroup(
                blockpanelLayout.createParallelGroup()
                    .addGroup(blockpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(blockpanelLayout.createParallelGroup()
                            .addComponent(blockscrollPane, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addGroup(blockpanelLayout.createSequentialGroup()
                                .addComponent(blocklabel)
                                .addGap(0, 208, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            blockpanelLayout.setVerticalGroup(
                blockpanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, blockpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(blocklabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(blockscrollPane, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
        }

        //======== block2panel ========
        {

            //---- block2label ----
            block2label.setText("\u963b\u585e\u961f\u52172");

            //======== block2scrollPane ========
            {

                //---- block2table ----
                block2table.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID", "\u963b\u585e\u65f6\u95f4", "\u5269\u4f59\u65f6\u95f4"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class, Integer.class, Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                block2scrollPane.setViewportView(block2table);
            }

            GroupLayout block2panelLayout = new GroupLayout(block2panel);
            block2panel.setLayout(block2panelLayout);
            block2panelLayout.setHorizontalGroup(
                block2panelLayout.createParallelGroup()
                    .addGroup(block2panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(block2panelLayout.createParallelGroup()
                            .addComponent(block2label)
                            .addComponent(block2scrollPane, GroupLayout.PREFERRED_SIZE, 284, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(18, Short.MAX_VALUE))
            );
            block2panelLayout.setVerticalGroup(
                block2panelLayout.createParallelGroup()
                    .addGroup(block2panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(block2label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(block2scrollPane, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(11, Short.MAX_VALUE))
            );
        }

        //======== queuepanel ========
        {

            //---- queue1label ----
            queue1label.setText("\u4e00\u7ea7\u961f\u5217");

            //======== scrollPane3 ========
            {

                //---- queue1table ----
                queue1table.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                scrollPane3.setViewportView(queue1table);
            }

            //---- queue2label ----
            queue2label.setText("\u4e8c\u7ea7\u961f\u5217");

            //======== scrollPane4 ========
            {

                //---- queue2table ----
                queue2table.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                scrollPane4.setViewportView(queue2table);
            }

            //---- queue3label ----
            queue3label.setText("\u4e09\u7ea7\u961f\u5217");

            //======== queue3scrollPane ========
            {

                //---- queue3table ----
                queue3table.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8fdb\u7a0bID"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Integer.class
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
                queue3scrollPane.setViewportView(queue3table);
            }

            GroupLayout queuepanelLayout = new GroupLayout(queuepanel);
            queuepanel.setLayout(queuepanelLayout);
            queuepanelLayout.setHorizontalGroup(
                queuepanelLayout.createParallelGroup()
                    .addGroup(queuepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(queuepanelLayout.createParallelGroup()
                            .addComponent(queue1label)
                            .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addComponent(queue2label)
                            .addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addComponent(queue3label)
                            .addComponent(queue3scrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(19, Short.MAX_VALUE))
            );
            queuepanelLayout.setVerticalGroup(
                queuepanelLayout.createParallelGroup()
                    .addGroup(queuepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(queue1label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(queue2label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(queue3label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(queue3scrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(27, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(6, 6, 6)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(Processpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(Readypanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(Timepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(speedpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addComponent(backjobpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(block2panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(36, 36, 36)
                                    .addComponent(memorypanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                                    .addComponent(finishpanel, GroupLayout.PREFERRED_SIZE, 276, GroupLayout.PREFERRED_SIZE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                                            .addGap(11, 11, 11)
                                            .addComponent(blockpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(statepanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Buttonpanel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(contentPaneLayout.createParallelGroup()
                                                .addComponent(JPpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                    .addComponent(selectfilepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addGap(100, 100, 100))))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addGap(40, 40, 40)
                                            .addComponent(queuepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 289, Short.MAX_VALUE)))))
                            .addContainerGap())))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(memoryscrollPane))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(selectfilepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 624, Short.MAX_VALUE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(JPpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(12, 12, 12))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(141, 141, 141)
                                    .addComponent(queuepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(memorypanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(finishpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(Timepanel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(speedpanel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Buttonpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(statepanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(blockpanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(Processpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Readypanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(backjobpanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(block2panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(33, 33, 33)))
                    .addComponent(memoryscrollPane, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                    .addGap(123, 123, 123))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - 淘宝星河飘雪
    private JPanel Buttonpanel;
    private JButton startbutton;
    private JButton addjobbutton;
    private JButton savebutton;
    private JButton exitbutton;
    private JPanel Timepanel;
    private JLabel timelabel;
    private JTextField timetextField;
    private JLabel cpuStatelabel;
    private JTextField cpuStatetextField;
    private JPanel Processpanel;
    private JLabel Processlabel;
    private JScrollPane ProcessscrollPane;
    private JTable processtable;
    private JPanel Readypanel;
    private JLabel Readylabel;
    private JScrollPane ReadyscrollPane;
    private JTable Readytable;
    private JPanel backjobpanel;
    private JLabel label2;
    private JScrollPane backjobscrollPane;
    private static JTable backjobtable;
    private JScrollPane scrollPane2;
    private JTable Processtable;
    private JScrollPane scrollPane1;
    private JTextPane das;
    private JPanel JPpanel;
    private JLabel JPlabel;
    private JScrollPane JPscrollPane;
    private JTextPane JPtextPane;
    private JPanel speedpanel;
    private JRadioButton highradioButton;
    private JRadioButton middleradioButton;
    private JRadioButton lowradioButton;
    private JLabel speedlabel;
    private JPanel finishpanel;
    private JLabel finishlabel;
    private JScrollPane finishscrollPane;
    private JTable finishtable;
    private JPanel statepanel;
    private JLabel statelabel;
    private JScrollPane statescrollPane;
    private JTextPane statetextPane;
    private JPanel memorypanel;
    private JScrollPane memoryscrollPane;
    private JTable memorytable;
    private JPanel selectfilepanel;
    private JButton selectfilebutton;
    private JPanel blockpanel;
    private JScrollPane blockscrollPane;
    private JTable blocktable;
    private JLabel blocklabel;
    private JPanel block2panel;
    private JLabel block2label;
    private JScrollPane block2scrollPane;
    private JTable block2table;
    private JPanel queuepanel;
    private JLabel queue1label;
    private JScrollPane scrollPane3;
    private JTable queue1table;
    private JLabel queue2label;
    private JScrollPane scrollPane4;
    private JTable queue2table;
    private JLabel queue3label;
    private JScrollPane queue3scrollPane;
    private JTable queue3table;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
