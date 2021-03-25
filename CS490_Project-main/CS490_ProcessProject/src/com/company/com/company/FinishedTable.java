package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Class to represent the TAT table for finished processes
 */
public class FinishedTable extends JPanel {
    private DefaultTableModel model;

    //Initializes and displays empty TAT table
    public FinishedTable(){
        setLayout(new BorderLayout());
        Object[] timeColumns = {"Process Name", "Arrival Time", "Service Time", "Finish Time", "TAT", "nTAT"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(timeColumns);
        JTable timeTable = new JTable(model);
        JScrollPane jsp1 = new JScrollPane(timeTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(jsp1, BorderLayout.CENTER);
    }

    /**
     * Returns the model
     * @return DefaultTableModel model
     */
    public DefaultTableModel getModel(){
        return model;
    }


}
