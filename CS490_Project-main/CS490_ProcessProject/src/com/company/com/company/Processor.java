package com.company;

import java.util.concurrent.locks.Lock;

/**
 * Class that mimics CPU execution of a list of processes in FIFO order.
 */
public class Processor implements Runnable{
    protected final CPUPanel cpu;
    protected final Lock processQueueLock;
    protected final Lock finishedTableLock;
    protected int systemTimer;
    protected int procFinished;
    protected final WaitingQueue waitingProc;
    protected final FinishedTable finishedProc;
    protected final NTATDisplay ntatDisplay;
    protected float totalntat;

    /**
     * Creates the executor and initializes the threadLock as well as sets the CPUPanel to be updated during process execution
     */
    public Processor(CPUPanel cpu, Lock threadLock, WaitingQueue waitingProc, FinishedTable finishedProc, Lock finishedLock, NTATDisplay ntatDisplay){
        this.cpu = cpu;
        this.processQueueLock = threadLock;
        systemTimer = 0;
        procFinished = 0;
        this.waitingProc = waitingProc;
        this.finishedProc = finishedProc;
        finishedTableLock = finishedLock;
        this.ntatDisplay = ntatDisplay;
        totalntat = 0.0f;

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
                        }
                    }finally{
                        processQueueLock.unlock();
                    }

                    //Execute the process one second at a time, checking each second if the system is paused and pausing execution if it is
                    if(hasProcess) {
                        for (int j = time; j > 0; j--) {
                            if (Main.getIsPaused()) {
                                //Do nothing if paused
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                                j++;
                            } else {
                                //Sleep for a second and update timer
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                                cpu.setTimeRem(j-1);
                                systemTimer++;
                            }

                        }
                        //systemTimer--;
                        int taT = systemTimer - (Integer) timeRow[1];
                        float nTaT = (float) taT / (Integer) timeRow[2];
                        timeRow[3] = systemTimer;
                        timeRow[4] = taT;
                        timeRow[5] = nTaT;
                        totalntat += nTaT;

                        //Add timeRow to the finished process table
                        finishedTableLock.lock();
                        try{
                            finishedProc.getModel().addRow(timeRow);
                        } finally{
                            finishedTableLock.unlock();
                        }

                        hasProcess = false;
                        procFinished++;
                        ntatDisplay.setAvg(procFinished, totalntat);
                    }

                }
            }catch (InterruptedException ex){
                //I don't know what to put here
            }
        }
    }
}
