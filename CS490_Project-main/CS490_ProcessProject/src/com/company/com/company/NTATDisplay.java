package com.company;

import javax.swing.*;
import java.awt.*;

public class NTATDisplay extends JPanel {
    private JLabel avg;

    public NTATDisplay(){
        avg = new JLabel("0.0");

        setLayout(new FlowLayout());
        JLabel nTATText = new JLabel("Current average nTAT: ");
        add(nTATText);
        add(avg);
    }

    public void setAvg(int proc, float totalNTAT){
        Float a = totalNTAT / proc;
        avg.setText(a.toString());
    }
}
