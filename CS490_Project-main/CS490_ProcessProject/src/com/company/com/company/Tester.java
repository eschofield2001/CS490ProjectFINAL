package com.company;

import java.util.ArrayList;

/**
 * A tester class whose functions are used to test the project
 */
public class Tester {

    /**
     * Constructs a tester
     */
    public void Tester(){

    }

    /**
     * Test verifying that processReader is accurately reading in the processes. If the printed values match those in the test file, then the test is a success
     * @param processes an ArrayList of Processes to be looked at
     */
    public void testProcessReader(ArrayList<Process> processes){
        for (int i = 0; i < processes.size(); i++){
            processes.get(i).print();
        }
    }
}
