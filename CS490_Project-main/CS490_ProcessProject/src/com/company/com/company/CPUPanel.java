package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * JPanel that displays data such as CPU name, the process it is executing, as well as the time remaining until completion
 */
public class CPUPanel extends JPanel {
    private JLabel pExec;
    private JLabel timeRem;

    /**
     * Creates the panel and initializes the time and process name to blank values
     */
    public CPUPanel(String cpuName){
        setLayout(new GridLayout(3,1));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);

        JLabel cpuNameLabel = new JLabel(cpuName);
        add(cpuNameLabel);

        JPanel cpuExec = new JPanel(new FlowLayout());
        JLabel execText = new JLabel("Executing: ");
        pExec = new JLabel("Idle");
        cpuExec.add(execText);
        cpuExec.add(pExec);
        add(cpuExec);

        JPanel timeRemPanel = new JPanel(new FlowLayout());
        JLabel timeRemText = new JLabel("Time Remaining: ");
        timeRem = new JLabel("N/A");
        timeRemPanel.add(timeRemText);
        timeRemPanel.add(timeRem);
        add(timeRemPanel);
    }

    /**
     * Sets pExec (process executing) name to process
     * @param process A string representing the process name
     */
    public void setProcess(String process){
        pExec.setText(process);
    }

    /**
     * Sets timeRem (time remaining) to a new value representing the time left for execution
     * @param time An int representin the time remaining until a process finishes executing
     */
    public void setTimeRem(int time){
        timeRem.setText(String.valueOf(time));
    }
}
