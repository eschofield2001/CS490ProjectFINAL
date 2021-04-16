package com.company;

import java.util.concurrent.locks.Lock;
import java.util.*;

public class ProcessorHRRN extends Processor{
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
    public ProcessorHRRN(CPUPanel cpu, Lock threadLock, WaitingQueue waitingProc, FinishedTable finishedProc, Lock finishedLock, NTATDisplay ntatDisplay) {
        super(cpu, threadLock, waitingProc, finishedProc, finishedLock, ntatDisplay);
    }

    /**
     * Overrides run in Processor super class to implement the HRRN algorithm
     */

    public void setHrrnRatio(List<Process> remainingProcesses, int tExecutedServiceTime){
        for (Process i: remainingProcesses){
            int wTime = tExecutedServiceTime-i.getArrivalTime();
            float hrrnRatio = (wTime + i.getServiceTime())/i.getServiceTime();
            i.setWaitT(wTime);
            i.setHrrnRatio(hrrnRatio);
        }
    }

    public Process getCurrentProcess(List<Process> remainingProcesses) {
        float max = 0;
        Process currentProc = null;
        for (Process i : remainingProcesses) {
            if (i.getHrrnRatio() > max) {
                max = i.getHrrnRatio();
                currentProc = i;
            }
        }
        return currentProc;
    }

    public void run(){
        Object[] timeRow;
        Process currentProc = null;
        List<Process> remainingProcesses = new ArrayList<>();
        boolean hasProcess = false;
        int tExecutedServiceTime = 0; //add service time of all process executed

        while(!Main.getIsPaused()){
            while(!waitingProc.getProcessList().isEmpty()) {
                //Since it isn't shared, don't need to use lock for this phase
                if(waitingProc.getProcessList().get(0).getArrivalTime() <= systemTimer){
                    currentProc = waitingProc.getProcessList().get(0);
                    tExecutedServiceTime += currentProc.getServiceTime();
                    cpu.setProcess(currentProc.getProcessID());
                    hasProcess = true;
                    waitingProc.removeRow(0);
                }

                //Check if there is a previously arrived process that can run
                else if(waitingProc.getProcessList().get(waitingProc.getProcessList().size() - 1).getArrivalTime() <= systemTimer){
                    remainingProcesses = waitingProc.getProcessList();
                    setHrrnRatio(remainingProcesses, tExecutedServiceTime);
                    currentProc = getCurrentProcess(remainingProcesses);
                    cpu.setProcess(currentProc.getProcessID());
                    tExecutedServiceTime += currentProc.getServiceTime();
                    hasProcess = true;
                    waitingProc.removeRow(waitingProc.getProcessList().size() - 1);
                }

                else{
                    //No process has arrived yet, sleep and check next time unit
                    try {
                        Thread.sleep(Main.getTimeUnit().getTimeUnit());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    systemTimer++;
                }

                //While processor has a process:
                if(hasProcess){
                    //Sleep until process has finished executing
                    for(int j = currentProc.getTimeRem(); (j > 0) ; j--) {
                        if (!Main.getIsPaused()) {
                            try {
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            currentProc.setTimeRem(currentProc.getTimeRem() - 1);
                            cpu.setTimeRem(currentProc.getTimeRem());
                            systemTimer++;
                        } else {
                            try {
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            j++;
                        }
                    }

                    if (currentProc.getTimeRem() == 0){
                        //Process has finished executing, add to process table
                        timeRow = new Object[6];
                        timeRow[0] = currentProc.getProcessID();
                        timeRow[1] = currentProc.getArrivalTime();
                        timeRow[2] = currentProc.getServiceTime();
                        int tat = systemTimer - currentProc.getArrivalTime();
                        float nTat = (float) tat / currentProc.getServiceTime();
                        timeRow[3] = systemTimer;
                        timeRow[4] = tat;
                        timeRow[5] = nTat;
                        totalntat += nTat;

                        finishedProc.getModel().addRow(timeRow);
                        procFinished++;
                        ntatDisplay.setAvg(procFinished, totalntat);
                    }
                    else{
                        //Process didn't finish executing, add to back of process queue
                        waitingProc.getProcessList().add(currentProc);
                        Object[] row = new Object[2];
                        row[0] = currentProc.getProcessID();
                        row[1] = currentProc.getTimeRem();
                        waitingProc.getModel().addRow(row);
                    }
                    hasProcess = false;
                }
            }
        }
    }
}


