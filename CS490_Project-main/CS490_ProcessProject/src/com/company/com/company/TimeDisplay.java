package com.company;

import javax.swing.*;
import java.awt.*;

//Class to represent the set time unit GUI
public class TimeDisplay extends JPanel{
    private int timeUnit;

    /**
     * Constructor. Initializes timeUnit and creates the time unit GUI
     */
    public TimeDisplay(){
        timeUnit = 1000;

        setLayout(new FlowLayout());
        JLabel timeFirstHalf = new JLabel("1 time unit = ");
        JLabel timeSecondHalf = new JLabel ("ms.");
        final int FIELD_WIDTH = 10;
        JTextField timeText = new JTextField(FIELD_WIDTH);
        timeText.setText("Time");

        //Updates timeUnit when enterButton is pressed
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> timeUnit = Integer.parseInt(timeText.getText()));

        add(timeFirstHalf);
        add(timeText);
        add(timeSecondHalf);
        add(enterButton);
    }

    /**
     * Returns timeUnit
     * @return int timeUnit
     */
    public int getTimeUnit(){
        return timeUnit;
    }

    /**
     * Sets timeUnit equal to t
     * @param t An integer representing the new time unit in milliseconds
     */
    public void setTimeUnit(int t){
        timeUnit = t;
    }
}
