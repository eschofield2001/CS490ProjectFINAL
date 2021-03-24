package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
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
    //ArrayList of processes that need to be executed
    private static ArrayList<Process> processList = new ArrayList<>();
    //Displays the processes in the table
    private static DefaultTableModel waitingModel = new DefaultTableModel();
    //Displays the processes in the table
    private static DefaultTableModel updatedModel = new DefaultTableModel();
    //Used by Executor to determine how many milliseconds to sleep for
    private static int timeUnit = 1000;
    //Used by Executor to determine if the system is paused
    private static boolean isPaused = true;
    //JLabel containing the throughput. Will be updated by executor class
    private static JLabel throughputValue = new JLabel("0.0");

    /**
     * Function to update the throughput displayed by throughputValue
     * @param t The float representing the new throughput value to be displayed
     */
    public static void setThroughput(Float t){
        throughputValue.setText(String.valueOf(t));
    }

    //Functions to get the above private values. Will be used by Executor class
    //For phase 3, could add integer field to indicate which processList should be returned
    /**
     * Function to return the processList ArrayList of processes
     * @return ArrayList<Process> processList
     */
    public static ArrayList<Process> getProcessList() {
        return processList;
    }

    /**
     * Function to return the timeUnit
     * @return int timeUnit
     */
    public static int getTimeUnit() {
        return timeUnit;
    }

    /**
     * Function to return the waitingModel representing the table with the processes
     * @return DefaultTableModel waitingModel
     */
    public static DefaultTableModel getModel() {
        return waitingModel;
    }

    /**
     * Function to return the waitingModel representing the table with the processes
     * @return DefaultTableModel waitingModel
     */
    public static DefaultTableModel getUpdatedModel() {
        return updatedModel;
    }
    /**
     * Function to return isPaused to indicate if the system is paused
     * @return boolean isPaused
     */
    public static boolean getIsPaused(){
        return isPaused;
    }

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

        //Create table that displays the current loaded processes - initialized when Start button is pressed for the first time
        JPanel tableDisplay = new JPanel(new BorderLayout());
        Object columns[] = {"Process Name", "Service Time"};
        waitingModel = new DefaultTableModel();
        waitingModel.setColumnIdentifiers(columns);
        JTable processTable = new JTable(waitingModel);
        JScrollPane jsp = new JScrollPane(processTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JLabel tableTitle = new JLabel("Waiting Process Queue");

        tableDisplay.add(jsp, BorderLayout.CENTER);
        tableDisplay.add(tableTitle, BorderLayout.NORTH);

        //Create CPU Display - First half is creating time input field
        JPanel cpuDisplay = new JPanel(new BorderLayout());
        JPanel timeDisplay = new JPanel(new FlowLayout());
        JLabel timeFirstHalf = new JLabel("1 time unit = ");
        JLabel timeSecondHalf = new JLabel ("ms.");
        final int FIELD_WIDTH = 10;
        JTextField timeText = new JTextField(FIELD_WIDTH);
        timeText.setText("Time");

        //Updates timeUnit when enterButton is pressed
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> {
            timeUnit = Integer.parseInt(timeText.getText());
        });

        timeDisplay.add(timeFirstHalf);
        timeDisplay.add(timeText);
        timeDisplay.add(timeSecondHalf);
        timeDisplay.add(enterButton);
        cpuDisplay.add(timeDisplay, BorderLayout.NORTH);

        //Create CPU Display - Second half is creating the actual representation of the CPU
        JPanel CPUcontainer = new JPanel(new GridLayout(2, 1));
        CPUPanel cpu1 = new CPUPanel("CPU 1");
        CPUPanel cpu2 = new CPUPanel("CPU 2");
        CPUcontainer.add(cpu1);
        CPUcontainer.add(cpu2);
        cpuDisplay.add(CPUcontainer, BorderLayout.CENTER);

        //Start execution on each CPU
        Lock threadLock = new ReentrantLock();
        Executor CPU1 = new Executor(cpu1, threadLock);
        Executor CPU2 = new Executor(cpu2, threadLock);
        Thread execThread1 = new Thread(CPU1);
        Thread execThread2 = new Thread(CPU2);

        //Create top section of GUI that allows user to start or pause the CPU
        JLabel cpuState = new JLabel("System Uninitialized");

        JButton startButton = new JButton("Start System");
        startButton.addActionListener(e -> {
            isPaused = false;
            //Need to press start button to initialize process table
            if(cpuState.getText().equals("System Uninitialized")){
                Object[] row;
                //Initialize table
                for(int i = 0; i < processList.size(); i++){
                    row = new Object[2];
                    row[0] = processList.get(i).getProcessID();
                    row[1] = processList.get(i).getServiceTime();
                    waitingModel.addRow(row);
                }
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

        //Creating south section for the turnaround time table
        //Create table that displays the arrival, finish, and turnaround times
        JPanel finishedDisplay = new JPanel(new GridLayout(2,1));

        JPanel timetable = new JPanel(new BorderLayout());
        Object timeColumns[] = {"Process Name", "Arrival Time", "Service Time", "Finish Time", "TAT", "nTAT"};
        updatedModel = new DefaultTableModel();
        updatedModel.setColumnIdentifiers(timeColumns);
        JTable timeTable = new JTable(updatedModel);
        JScrollPane jsp2 = new JScrollPane(timeTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timetable.add(jsp2, BorderLayout.CENTER);

        JPanel throughputDisplay = new JPanel(new FlowLayout());
        JLabel throughputText = new JLabel("Current throughput: ");
        JLabel throughputUnitsText = new JLabel("process/unit of time.");
        throughputDisplay.add(throughputText);
        throughputDisplay.add(throughputValue);
        throughputDisplay.add(throughputUnitsText);

        finishedDisplay.add(timetable);
        finishedDisplay.add(throughputDisplay);

        //Add sections to GUI and initialize
        mainFrame.add(topSection, BorderLayout.NORTH);
        mainFrame.add(tableDisplay, BorderLayout.WEST);
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
        ArrayList<Process> processes = new ArrayList<>();

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
            processes.add(process);
            process = new Process();
        }

        processList = new ArrayList<>(processes);
    }
}
