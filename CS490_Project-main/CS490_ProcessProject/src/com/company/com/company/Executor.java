package com.company;

import java.util.concurrent.locks.Lock;

/**
 * Class that mimics CPU execution of a list of processes in FIFO order.
 */
public class Executor implements Runnable{
    private CPUPanel cpu;
    private Lock processQueueLock;
    private Lock throughputLock;
    private int systemTimer;
    private int procFinished;

    /**
     * Creates the executor and initializes the threadLock as well as sets the CPUPanel to be updated during process execution
     */
    public Executor(CPUPanel cpu, Lock threadLock, Lock throughputLock){
        this.cpu = cpu;
        this.processQueueLock = threadLock;
        this.throughputLock = throughputLock;
        systemTimer = 0;
        procFinished = 0;
    }

    public int getTime(){
        return systemTimer;
    }

    public int getProcFinished(){
        return procFinished;
    }

    /**
     * Function to simulate a CPU executing processes one at a time using the processes in Main.processList. After the process is pulled, it is removed from processList
     */
    public void run(){
        int time = 0;
        Object[] timeRow = new Object[0];
        Boolean hasProcess = false;
        while (!Main.getIsPaused()) {
            try{
                while(!Main.getWaitingProc(1).getProcessList().isEmpty()){
                    //Lock processList while getting necessary information on next process to execute
                    processQueueLock.lock();
                    try{
                        //Check that the process next in line has actually "arrived". If not, sleep for a time unit and check again.
                        if(Main.getWaitingProc(1).getProcessList().get(0).getArrivalTime() <= systemTimer) {
                            cpu.setProcess(Main.getWaitingProc(1).getProcessList().get(0).getProcessID());
                            cpu.setTimeRem(Main.getWaitingProc(1).getProcessList().get(0).getServiceTime());
                            time = Main.getWaitingProc(1).getProcessList().get(0).getServiceTime();

                            //Initialize table
                            timeRow = new Object[6];
                            timeRow[0] = Main.getWaitingProc(1).getProcessList().get(0).getProcessID();
                            timeRow[1] = Main.getWaitingProc(1).getProcessList().get(0).getArrivalTime();
                            timeRow[2] = Main.getWaitingProc(1).getProcessList().get(0).getServiceTime();

                            //Update process table and queue
                            Main.getWaitingProc(1).removeRow(0);
                            hasProcess = true;
                        }
                        else{
                            Thread.sleep(Main.getTimeUnit());
                            systemTimer++;
                            Main.setThroughput();
                        }
                        //Move index 0 to finished list somewhere in here
                    }finally{
                        processQueueLock.unlock();
                    }

                    //Execute the process one second at a time, checking each second if the system is paused and pausing execution if it is
                    if(hasProcess) {
                        for (int j = time; j >= 0; j--) {
                            if (Main.getIsPaused()) {
                                //Do nothing if paused
                                Thread.sleep(Main.getTimeUnit());
                                j++;
                            } else {
                                //Sleep for a second and update timer
                                Thread.sleep(Main.getTimeUnit());
                                cpu.setTimeRem(j);
                                systemTimer++;
                                Main.setThroughput();
                            }

                        }
                        systemTimer--;
                        int taT = systemTimer - Integer.valueOf((Integer) timeRow[1]);
                        float nTaT = (float) taT / Integer.valueOf((Integer) timeRow[2]);
                        timeRow[3] = systemTimer;
                        timeRow[4] = taT;
                        timeRow[5] = nTaT;

                        Main.getFinishedTable(1).getModel().addRow(timeRow);
                        hasProcess = false;
                        procFinished++;
                        Main.setThroughput();
                    }

                }
            }catch (InterruptedException ex){
                //I don't know what to put here
            }
        }
    }
}
