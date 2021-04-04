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

    //Displays the processes in the table
    private static WaitingQueue waitingProc1;
    private static WaitingQueue waitingProc2; //for phase 3
    //Display for the time unit
    private static TimeDisplay timeUnit;
    //Used by Executor to determine if the system is paused
    private static boolean isPaused = true;

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

        //Create CPU Display - First half is creating time input field
        JPanel systemDisplay1 = new JPanel(new BorderLayout());
        JPanel systemDisplay2 = new JPanel(new BorderLayout());

        //Create table that displays the current loaded processes - GUI portion initialized when Start button is pressed for the first time, actual Process list is initialized when a file name is entered by the user
        waitingProc1 = new WaitingQueue();
        waitingProc2 = new WaitingQueue();
        systemDisplay1.add(waitingProc1, BorderLayout.WEST);
        systemDisplay2.add(waitingProc2, BorderLayout.WEST);

        //Create systemDisplay for CPUs 1 and 2 and add the CPUs
        CPUPanel cpu1 = new CPUPanel("CPU 1 (HRRN)");
        CPUPanel cpu2 = new CPUPanel("CPU 2 (RR)");
        systemDisplay1.add(cpu1, BorderLayout.EAST);
        systemDisplay2.add(cpu2, BorderLayout.CENTER);

        //Creating the turnaround time table
        //Displays the finished processes and data about them
        FinishedTable timeTable1 = new FinishedTable();
        FinishedTable timeTable2 = new FinishedTable();

        //Create the nTAT displays
        NTATDisplay ntat1 = new NTATDisplay();
        NTATDisplay ntat2 = new NTATDisplay();

        JPanel finishedProcDis1 = new JPanel(new GridLayout(2,1));
        JPanel finishedProcDis2 = new JPanel(new GridLayout(2,1));

        finishedProcDis1.add(timeTable1);
        finishedProcDis1.add(ntat1);

        finishedProcDis2.add(timeTable2);
        finishedProcDis2.add(ntat2);

        systemDisplay1.add(finishedProcDis1, BorderLayout.SOUTH);
        systemDisplay2.add(finishedProcDis2, BorderLayout.SOUTH);

        //Add RR time slice to systemDisplay2
        TimeSliceDisplay rrTimeSlice = new TimeSliceDisplay();
        systemDisplay2.add(rrTimeSlice, BorderLayout.EAST);

        //Start execution on each CPU
        Lock processQueueLock = new ReentrantLock();
        Lock finishedTableLock = new ReentrantLock();
        //Executor objects representing the 2 CPUs
//        Processor CPU1 = new Processor(cpu1, processQueueLock, waitingProc1, timeTable1, finishedTableLock, ntat1);
//        Processor CPU2 = new Processor(cpu2, processQueueLock, waitingProc2, timeTable2, finishedTableLock, ntat2);
        //CPU implementing round robin
        Processor CPU2 = new ProcessorRR(cpu2, processQueueLock, waitingProc2, timeTable2, finishedTableLock, ntat2, rrTimeSlice);
        //CPU implementing HRRN
        Processor CPU1 = new Processor(cpu1, processQueueLock, waitingProc1, timeTable1, finishedTableLock, ntat1);
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
                waitingProc2.initializeWaitingQueue();
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

        JPanel buttonSection = new JPanel(new FlowLayout());
        timeUnit = new TimeDisplay();
        buttonSection.add(startButton);
        buttonSection.add(pauseButton);
        buttonSection.add(cpuState);
        buttonSection.add(timeUnit);

        //Add sections to GUI and initialize
        mainFrame.add(buttonSection, BorderLayout.NORTH);
        mainFrame.add(systemDisplay1, BorderLayout.WEST);
        mainFrame.add(systemDisplay2, BorderLayout.EAST);

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

        final int FIELD_WIDTH = 20;
        JTextField inputText = new JTextField(FIELD_WIDTH);
        inputText.setText("File name");

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> {
            processReader(inputText.getText());
            frame.setVisible(true);
            start.dispose();
        });

        JLabel instructions = new JLabel("  Enter the path of the file containing the processes:");
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
                    process.setTimeRem(Integer.parseInt(processInfo[2]));
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
