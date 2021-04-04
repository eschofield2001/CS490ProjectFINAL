package com.company;

import java.util.concurrent.locks.Lock;

public class ProcessorRR extends Processor{
    private final TimeSliceDisplay timeSlice;

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
    public ProcessorRR(CPUPanel cpu, Lock threadLock, WaitingQueue waitingProc, FinishedTable finishedProc, Lock finishedLock, NTATDisplay ntatDisplay, TimeSliceDisplay tSlice) {
        super(cpu, threadLock, waitingProc, finishedProc, finishedLock, ntatDisplay);
        timeSlice = tSlice;
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
     * Overrides run in Processor super class to implement the RR algorithm
     */
    public void run(){
        Object[] timeRow;
        boolean hasProcess = false;
        Process currentProc = null;

        while(!Main.getIsPaused()){
            while(!waitingProc.getProcessList().isEmpty()){
                //Since it isn't shared, don't need to use lock for this phase
                if(waitingProc.getProcessList().get(0).getArrivalTime() <= systemTimer){
                    currentProc = waitingProc.getProcessList().get(0);
                    cpu.setProcess(currentProc.getProcessID());
                    cpu.setTimeRem(currentProc.getTimeRem());

                    hasProcess = true;
                    waitingProc.removeRow(0);
                }

                //Check if there is a previously executed process that can run again
                else if(waitingProc.getProcessList().get(waitingProc.getProcessList().size() - 1).getArrivalTime() <= systemTimer){
                    currentProc = waitingProc.getProcessList().get(waitingProc.getProcessList().size() - 1);
                    cpu.setProcess(currentProc.getProcessID());
                    cpu.setTimeRem(currentProc.getTimeRem());
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
                    //Sleep either for duration of time slice, or until process has finished executing
                    for(int j = timeSlice.getTimeSlice(); (j > 0) && (currentProc.getTimeRem() > 0); j--){
                        if (!Main.getIsPaused()){
                            try {
                                Thread.sleep(Main.getTimeUnit().getTimeUnit());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            currentProc.setTimeRem(currentProc.getTimeRem() - 1);
                            cpu.setTimeRem(currentProc.getTimeRem());
                            systemTimer++;
                        }
                        else{
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
