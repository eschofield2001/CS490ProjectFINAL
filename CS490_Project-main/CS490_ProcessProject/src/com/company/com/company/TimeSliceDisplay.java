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

    /**
     * Returns timeSlice
     * @return int timeSlices
     */
    public int getTimeSlice(){
        return timeSlice;
    }

    /**
     * Sets timeSlice equal to t
     * @param t An integer representing the new time slice
     */
    public void setTimeSlice(int t){
        timeSlice = t;
    }
}
