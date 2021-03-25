package com.company;

import javax.swing.*;
import java.awt.*;


//Class to represent the throughput and its display
public class ThroughputDisplay extends JPanel {
    private JLabel throughput;

    /**
     * Constructor for ThroughputDisplay. Creates the GUI and initializes throughput
     */
    public ThroughputDisplay(){
        throughput = new JLabel("0.0");

        setLayout(new FlowLayout());
        JLabel throughputText = new JLabel("Current throughput: ");
        JLabel throughputUnitsText = new JLabel("process/unit of time.");
        add(throughputText);
        add(throughput);
        add(throughputUnitsText);
    }

    //For phase 3, will add a set throughput function that uses data from Executor class. For now, will use a basic one that can be used by main
    public void setThroughput(String thp){
        throughput.setText(thp);
    }
}
