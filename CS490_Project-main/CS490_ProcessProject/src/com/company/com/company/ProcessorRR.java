package com.company;

import java.util.concurrent.locks.Lock;

public class ProcessorRR extends Processor{
    /**
     * Creates the executor and initializes the threadLock as well as sets the CPUPanel to be updated during process execution
     *
     * @param cpu A CPUPanel that displays the CPU
     * @param threadLock A lock to be used when accessing shared variables
     * @param waitingProc A queue of processes waiting to be executed
     * @param finishedProc A table that displays information about the finished processes
     * @param finishedLock might not need
     * @param ntatDisplay A display for the average normalized turnaround time
     */
    public ProcessorRR(CPUPanel cpu, Lock threadLock, WaitingQueue waitingProc, FinishedTable finishedProc, Lock finishedLock, NTATDisplay ntatDisplay) {
        super(cpu, threadLock, waitingProc, finishedProc, finishedLock, ntatDisplay);
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

    /**
     * Overrides run in Processor super class to implement the RR algorithm
     */
    public void run(){
        int time = 0;
        int numProcess = 4;
        int quantum = 2;
        Object[] timeRow = new Object[0];
        boolean hasProcess = false;
        int remainingProcess = numProcess;

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
//                            waitingProc.removeRow(0);
//                            hasProcess = true;

                            //Algorithm start
                            //Store service time and remaining time of each process
                            int[] serviceTime = new int[numProcess];
                            int[] remainingTime = new int[numProcess];
                            int[] finishTime = new int[numProcess];

                            for (int i = 0; i < numProcess; i++) {
                                serviceTime[i] = (Integer) timeRow[2];
                                //Remaining time before any process is arrived
                                remainingTime[i] = serviceTime[i];
                            }

                            int currentProcess = 0;
                            while (remainingProcess != 0) {
                                if (remainingTime[currentProcess] > quantum) {
                                    remainingTime[currentProcess] = remainingTime[currentProcess] - quantum;
                                    time += quantum;

                                } else if (remainingTime[currentProcess] <= quantum && remainingTime[currentProcess] > 0) {
                                    time += remainingTime[currentProcess];
                                    finishTime[currentProcess] = time;
                                    remainingTime[currentProcess] = 0;
                                    remainingProcess--;
                                }
                                currentProcess++;
                                if (currentProcess == numProcess) {
                                    //Passes control back to initial process when currentProcess equal to numProcess
                                    //gets executed
                                    currentProcess = 0;
                                }
                                //Algorithm end

                            }
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
                            }

                        }
//                        systemTimer--;
//                        int taT = systemTimer - (Integer) timeRow[1];
//                        float nTaT = (float) taT / (Integer) timeRow[2];
//                        timeRow[3] = systemTimer;
//                        timeRow[4] = taT;
//                        timeRow[5] = nTaT;
//                        totalntat += nTaT;
//
//                        //Add timeRow to the finished process table
//                        finishedTableLock.lock();
//                        try{
//                            finishedProc.getModel().addRow(timeRow);
//                        } finally{
//                            finishedTableLock.unlock();
//                        }
//
//                        hasProcess = false;
//                        procFinished++;
//                        ntatDisplay.setAvg(procFinished, totalntat);
                    }

                }
            }catch (InterruptedException ex){
                //I don't know what to put here
            }
        }
    }
}
