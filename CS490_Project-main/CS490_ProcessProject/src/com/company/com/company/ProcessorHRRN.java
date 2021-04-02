package com.company;

import java.util.concurrent.locks.Lock;

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
    public void run(){
        //Can be very similar to original run(), just pick index based off of HRRN value instead of using FIFO
    }
}
