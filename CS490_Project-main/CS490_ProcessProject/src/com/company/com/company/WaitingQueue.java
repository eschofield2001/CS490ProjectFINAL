package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class to represent the waiting queue and its GUI
 */
public class WaitingQueue extends JPanel{
    private DefaultTableModel model;
    private ArrayList<Process> processList;

    //Creates and initializes empty waiting queue GUI
    public WaitingQueue(){
        processList = new ArrayList<>();
        setLayout(new BorderLayout());
        Object[] columns = {"Process Name", "Service Time"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        JTable processTable = new JTable(model);
        JScrollPane jsp = new JScrollPane(processTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JLabel tableTitle = new JLabel("Waiting Process Queue");

        add(jsp, BorderLayout.CENTER);
        add(tableTitle, BorderLayout.NORTH);
    }

    /**
     * Adds processes to waiting queue GUI
     */
    public void initializeWaitingQueue(){

        Object[] row;
        for (Process process : processList) {
            row = new Object[2];
            row[0] = process.getProcessID();
            row[1] = process.getServiceTime();
            model.addRow(row);
        }
    }

    /**
     * Removes row i from the GUI and the processList
     * @param i Index of the row to remove
     */
    public void removeRow(int i){
        model.removeRow(i);
        processList.remove(i);
    }

    /**
     * Returns processList
     * @return ArrayList<Process> processList</Process>
     */
    public ArrayList<Process> getProcessList(){
        return processList;
    }

    /**
     * Returns model
     * @return DefaultTableModel model
     */
    public DefaultTableModel getModel(){
        return model;
    }
}
