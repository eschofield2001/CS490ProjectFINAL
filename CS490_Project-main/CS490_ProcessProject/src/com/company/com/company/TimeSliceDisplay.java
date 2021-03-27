package com.company;

import javax.swing.*;
import java.awt.*;

public class TimeSliceDisplay extends JPanel{
    int timeSlice;

    public TimeSliceDisplay(){
        timeSlice = 2;

        setLayout(new FlowLayout());
        JLabel rrtext = new JLabel("RR Time\n Slice Length ");
        final int FIELD_WIDTH = 10;
        JTextField timeText = new JTextField(FIELD_WIDTH);
        timeText.setText("2");

        //Updates timeUnit when enterButton is pressed
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> timeSlice = Integer.parseInt(timeText.getText()));

        add(rrtext);
        add(timeText);
        add(enterButton);
    }

    public int getTimeSlice(){
        return timeSlice;
    }
}
