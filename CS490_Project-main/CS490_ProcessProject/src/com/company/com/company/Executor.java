package com.company;

import java.util.concurrent.locks.Lock;

/**
 * Class that mimics CPU execution of a list of processes in FIFO order.
 */
public class Executor implements Runnable{
    private final CPUPanel cpu;
    private final Lock processQueueLock;
    private final Lock finishedTableLock;
    private int systemTimer;
    private int procFinished;
    private final WaitingQueue waitingProc;
    private final FinishedTable finishedProc;
    //Will add Throughput Display in phase 3, shared with other CPU in phase 2

    /**
     * Creates the executor and initializes the threadLock as well as sets the CPUPanel to be updated during process execution
     */
    public Executor(CPUPanel cpu, Lock threadLock, WaitingQueue waitingProc, FinishedTable finishedProc, Lock finishedLock){
        this.cpu = cpu;
        this.processQueueLock = threadLock;
        systemTimer = 0;
        procFinished = 0;
        this.waitingProc = waitingProc;
        this.finishedProc = finishedProc;
        finishedTableLock = finishedLock;
    }

    /**
     * Returns the systemTimer
     * @return int systemTimer
     */
    public int getTime(){
        return systemTimer;
    }

    /**
     * Returns the number of processes executed
     * @return int procFinished
     */
    public int getProcFinished(){
        return procFinished;
    }

    /**
     * Function to simulate a CPU executing processes one at a time using the processes in Main.processList. After the process is pulled, it is removed from processList
     */
    public void run(){
        int time = 0;
        Object[] timeRow = new Object[0];
        boolean hasProcess = false;

        while (!Main.getIsPaused()) {
            try{
                while(!waitingProc.getProcessList().isEmpty()){
                    //Lock processList while getting necessary information on next process to execute
                    processQueueLock.lock();
                    try{
                        //Check that the process next in line has actually "arrived". If not, sleep for a time unit and check again.
                        if(waitingProc.getProcessList().get(0).getArrivalTime() <= systemTimer) {
                            cpu.setProcess(waitingProc.getProcessList().get(0).getProcessID());
                            cpu.setTimeRem(waitingProc.getProcessList().get(0).getServiceTime());
                            time = waitingProc.getProcessList().get(0).getServiceTime();

                            //Initialize table
                            timeRow = new Object[6];
                            timeRow[0] = waitingProc.getProcessList().get(0).getProcessID();
                            timeRow[1] = waitingProc.getProcessList().get(0).getArrivalTime();
                            timeRow[2] = waitingProc.getProcessList().get(0).getServiceTime();

                            //Update process table and queue
                            waitingProc.removeRow(0);
                            hasProcess = true;
                        }
                        else{
                            Thread.sleep(Main.getTimeUnit().getTimeUnit());
                            systemTimer++;
                            Main.setThroughput(1);
                        }
                    }finally{
                        processQueueLock.unlock();
                    }

                    //Execute the process one second at a time, checking each second if the system is paused and pausing execution if it is
                    if(hasProcess) {
                        for (int j = time; j >= 0; j--) {
                            if (Main.getIsPaused()) {
                                //Do nothing if paused
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                                j++;
                            } else {
                                //Sleep for a second and update timer
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                                cpu.setTimeRem(j);
                                systemTimer++;
                                Main.setThroughput(1);
                            }

                        }
                        systemTimer--;
                        int taT = systemTimer - (Integer) timeRow[1];
                        float nTaT = (float) taT / (Integer) timeRow[2];
                        timeRow[3] = systemTimer;
                        timeRow[4] = taT;
                        timeRow[5] = nTaT;

                        //Add timeRow to the finished process table
                        finishedTableLock.lock();
                        try{
                            finishedProc.getModel().addRow(timeRow);
                        } finally{
                            finishedTableLock.unlock();
                        }

                        hasProcess = false;
                        procFinished++;
                        Main.setThroughput(1);
                    }

                }
            }catch (InterruptedException ex){
                //I don't know what to put here
            }
        }
    }
}
