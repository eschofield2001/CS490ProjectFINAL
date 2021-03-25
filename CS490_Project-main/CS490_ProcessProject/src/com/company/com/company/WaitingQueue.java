package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class to represent the waiting queue and its GUI
 */
public class WaitingQueue extends JPanel{
    DefaultTableModel model;
    ArrayList<Process> processList;

    //Creates and initializes empty waiting queue GUI
    public WaitingQueue(){
        setLayout(new BorderLayout());
        Object columns[] = {"Process Name", "Service Time"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        JTable processTable = new JTable(model);
        JScrollPane jsp = new JScrollPane(processTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JLabel tableTitle = new JLabel("Waiting Process Queue");

        add(jsp, BorderLayout.CENTER);
        add(tableTitle, BorderLayout.NORTH);
    }

    /**
     * Adds processes to waiting queue GUI + initializes processList
     * @param processes List of processes to be added to the GUI
     */
    public void initializeWaitingQueue(ArrayList<Process> processes){
        processList = processes;
        Object[] row;
        for(int i = 0; i < processList.size(); i++){
            row = new Object[2];
            row[0] = processList.get(i).getProcessID();
            row[1] = processList.get(i).getServiceTime();
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
