package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main controller of the project. Displays the GUI and controls the flow of execution of the processes
 */
public class Main {

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 1000;

    //Global variables to be used for thread execution by Executor class:
    //Displays the processes in the table
    private static WaitingQueue waitingProc1;
    private static WaitingQueue waitingProc2; //for phase 3
    //Displays the finished processes and data about them
    private static FinishedTable timeTable1;
    private static FinishedTable timeTable2; //for phase 3
    //Display for the time unit
    private static TimeDisplay timeUnit;
    //Used by Executor to determine if the system is paused
    private static boolean isPaused = true;
    //JLabel containing the throughput. Will be updated by executor class
    private static ThroughputDisplay throughput1;
    private static ThroughputDisplay throughput2; //for phase 3
    //Executor objects representing the 2 CPUs
    private static Executor CPU1;
    private static Executor CPU2;

    /**
     * Main function of the project
     * @param args
     */
    public static void main(String[] args) {
        /*
        Create main GUI of the project -------------------------------------------------------------------------------------
         */
        JFrame mainFrame = new JFrame("Process Simulation", null);
        Dimension d = new Dimension();
        d.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setSize(d);
        mainFrame.setLayout(new BorderLayout());

        //Create table that displays the current loaded processes - GUI portion initialized when Start button is pressed for the first time, actual Process list is initialized when a file name is entered by the user
        waitingProc1 = new WaitingQueue();
        waitingProc2 = new WaitingQueue();

        //Create CPU Display - First half is creating time input field
        JPanel cpuDisplay = new JPanel(new BorderLayout());
        timeUnit = new TimeDisplay();
        cpuDisplay.add(timeUnit, BorderLayout.NORTH);

        //Create CPU Display - Second half is creating the actual representation of the CPU
        JPanel CPUContainer = new JPanel(new GridLayout(2, 1));
        CPUPanel cpu1 = new CPUPanel("CPU 1");
        CPUPanel cpu2 = new CPUPanel("CPU 2");
        CPUContainer.add(cpu1);
        CPUContainer.add(cpu2);
        cpuDisplay.add(CPUContainer, BorderLayout.CENTER);

        //Start execution on each CPU
        Lock processQueueLock = new ReentrantLock();
        CPU1 = new Executor(cpu1, processQueueLock);
        CPU2 = new Executor(cpu2, processQueueLock);
        Thread execThread1 = new Thread(CPU1);
        Thread execThread2 = new Thread(CPU2);

        //Create top section of GUI that allows user to start or pause the CPU
        JLabel cpuState = new JLabel("System Uninitialized");

        JButton startButton = new JButton("Start System");
        startButton.addActionListener(e -> {
            isPaused = false;
            //Need to press start button to initialize process table
            if(cpuState.getText().equals("System Uninitialized")){
                //Initialize waitingProc
                waitingProc1.initializeWaitingQueue();
                //Start execThreads, which will work through processList and execute the processes on 3 CPUs
                execThread1.start();
                execThread2.start();
            }
            cpuState.setText("System Running");
        });

        JButton pauseButton = new JButton("Pause System");
        pauseButton.addActionListener(e -> {
            cpuState.setText("System Paused");
            isPaused = true;
        });

        JPanel topSection = new JPanel(new FlowLayout());
        topSection.add(startButton);
        topSection.add(pauseButton);
        topSection.add(cpuState);

        //Creating the turnaround time table
        timeTable1 = new FinishedTable();

        //Create throughput display
        throughput1 = new ThroughputDisplay();

        //Create panel to display TAT table and throughput
        JPanel finishedDisplay = new JPanel(new GridLayout(2,1));

        finishedDisplay.add(timeTable1);
        finishedDisplay.add(throughput1);

        //Add sections to GUI and initialize
        mainFrame.add(topSection, BorderLayout.NORTH);
        mainFrame.add(waitingProc1, BorderLayout.WEST);
        mainFrame.add(cpuDisplay, BorderLayout.EAST);
        mainFrame.add(finishedDisplay, BorderLayout.SOUTH);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(false);

        //Display start menu. When that is exited, the main GUI will be set to visible
        startMenu(mainFrame);

    }

    /**
     * Displays a start menu to the user asking for a name for the file containing the processes. Updates processList and sets the visibility of frame to true
     * @param frame The main display of the project, will be set to visible when processList is initialized
     */
    public static void startMenu(JFrame frame){
        JFrame start = new JFrame("Start", null);
        Dimension d = new Dimension();
        d.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        start.setSize(d);
        start.setLayout(new BorderLayout());

        final int FIELD_WIDTH = 10;
        JTextField inputText = new JTextField(FIELD_WIDTH);
        inputText.setText("File name");

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> {
            processReader(inputText.getText());
            frame.setVisible(true);
            start.dispose();
        });

        JLabel instructions = new JLabel("Enter the directory of the file containing the processes.");
        JPanel flowLayout = new JPanel();
        flowLayout.add(inputText);
        flowLayout.add(enterButton);

        start.add(flowLayout, BorderLayout.SOUTH);
        start.add(instructions, BorderLayout.NORTH);
        start.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        start.pack();
        start.setLocationRelativeTo(null);
        start.setVisible(true);
    }

    /**
     * Reads in the processes from the file indicated by fileName and updates processList with its contents
     * @param fileName The name of the file the user would like to open that contains a list of processes
     */
    public static void processReader(String fileName){
        FileReader infile = null;

        try{
            assert fileName != null;
            infile = new FileReader(fileName);
        }
        catch(FileNotFoundException ex){
            System.err.println("File could not be located.");
            System.exit(1);
        }

        Scanner fileIn = new Scanner(infile);

        String processLine;
        String[] processInfo;
        Process process = new Process();

        while(fileIn.hasNextLine()){
            processLine = fileIn.nextLine();
            processInfo = processLine.split(", ");
            for (int i = 0; i < 4; i++){
                if (i == 0){
                    process.setArrivalT(Integer.parseInt(processInfo[0]));
                }
                else if (i == 1){
                    process.setProcessID(processInfo[1]);
                }
                else if (i == 2){
                    process.setServiceT(Integer.parseInt(processInfo[2]));
                }
                else{
                    process.setPriority(Integer.parseInt(processInfo[3]));
                }
            }
            waitingProc1.getProcessList().add(process);
            waitingProc2.getProcessList().add(process);
            process = new Process();
        }

    }

    /**
     * Returns the WaitingQueue corresponding to i
     * @param i A number 1 or 2 representing which WaitingQueue (waitingProc1/2) to return
     * @return WaitingQueue waitingProc1 or waitingProc2
     */
    public static WaitingQueue getWaitingProc(int i){
        if (i == 1){
            return waitingProc1;
        }
        else {
            return waitingProc2;
        }
    }

    /**
     * Returns the FinishedTable corresponding to i
     * @param i A number 1 or 2 representing which FinishedTable (timeTable1/2) to return
     * @return FinishedTable timeTable1 or timeTable2
     */
    public static FinishedTable getFinishedTable(int i){
        if (i == 1){
            return timeTable1;
        }
        else{
            return timeTable2;
        }
    }

    /**
     * Function to update the throughput displayed by throughputValue
     */
    public static void setThroughput(int i){
        int proc = CPU1.getProcFinished() + CPU2.getProcFinished();
        int time = Math.max(CPU1.getTime(), CPU2.getTime());

        //Base the throughput on the max time passed between the two CPUs (they aren't going to match exactly due to threading)
        Float throughputVal = (float)proc/time;

        if(i == 1){
            throughput1.setThroughput(String.valueOf(throughputVal));
        }
        else if (i == 2){
            throughput2.setThroughput(String.valueOf(throughputVal));
        }

    }

    /**
     * Function to return the timeUnit
     * @return TimeDisplay timeUnit
     */
    public static TimeDisplay getTimeUnit() {
        return timeUnit;
    }

    /**
     * Function to return isPaused to indicate if the system is paused
     * @return boolean isPaused
     */
    public static boolean getIsPaused(){
        return isPaused;
    }
}
