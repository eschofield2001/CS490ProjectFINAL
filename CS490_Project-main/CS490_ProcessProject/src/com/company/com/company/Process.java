package com.company;

/**
 * A simulated process
 */
public class Process{
    private int arrivalT;
    private int serviceT;
    private int priority;
    private String processID;
    private int timeRem;

    /**
     * Constructs a process and initializes the values
     * @param aTime The arrival time of the process
     * @param sTime The service time of the process
     * @param p The priority of the process
     * @param pID The name of the process
     */
    public Process(int aTime, int sTime, int p, String pID){
        arrivalT = aTime;
        serviceT = sTime;
        priority = p;
        processID = pID;
        timeRem = sTime;
    }

    /**
     * Constructs a process and initializes all of the values to null values
     */
    public Process(){
        arrivalT = 0;
        serviceT = 0;
        priority = 0;
        processID = null;
    }

    public void setTimeRem(int sTime){
        timeRem = sTime;
    }

    public int getTimeRem(){
        return timeRem;
    }

    /**
     * Sets the value of arrivalT to aTime
     * @param aTime an int representing the new arrivalT value
     */
    public void setArrivalT(int aTime){
        arrivalT = aTime;
    }

    /**
     * Sets the value of serviceT to sTime
     * @param sTime an int representing the new serviceT value
     */
    public void setServiceT(int sTime){
        serviceT = sTime;
    }

    /**
     * Sets the value of priority to p
     * @param p an int representing the new priority value
     */
    public void setPriority(int p){
        priority = p;
    }

    /**
     * Sets the value of processID to pID
     * @param pID a String representing the new processID value
     */
    public void setProcessID(String pID){
        processID = pID;
    }

    /**
     * Prints the elements of the process
     */
    public void print() {
        System.out.printf("Arrival Time: %d \nProcessID: %s \nService Time: %d \nPriority: %d \n\n", arrivalT, processID, serviceT, priority);
    }

    /**
     * Returns the processID string
     * @return processID: A string with the name of the process
     */
    public String getProcessID(){
        return processID;
    }

    /**
     * Returns the serviceTime int
     * @return serviceT: An int with the time it takes to execute the process
     */
    public int getServiceTime(){
        return serviceT;
    }
    /**
     * Returns the serviceTime int
     * @return serviceT: An int with the time it takes to execute the process
     */
    public int getArrivalTime(){
        return arrivalT;
    }

}
