/*

Copyright (c) 2001, 2002, 2003 Flo Ledermann <flo@subnet.at>

This file is part of parvis - a parallel coordiante based data visualisation
tool written in java. You find parvis and additional information on its
website at http://www.mediavirus.org/parvis.

parvis is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

parvis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with parvis (in the file LICENSE.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

package org.mediavirus.parvis.gui;

import org.mediavirus.parvis.model.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;

/**
 *
 * @author  flo
 */
public class BrushList extends javax.swing.JFrame implements BrushListener, ListSelectionListener{
    
    /** The ParallelDisplay component we are assigned to. */
    ParallelDisplay parent;
    
    /** The current brush, appears in the inputfield. */
    Brush currentBrush = null;

    /** Creates new form BrushList */
    public BrushList() {
        initComponents();
        brushList.setModel(new DefaultListModel());
        brushList.addListSelectionListener(this);
        
    }
    
    /**
     * Creates a new BrushList for the given parent Component.
     */
    public BrushList(ParallelDisplay display){
        this();
        this.parent = display;
        display.addBrushListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        listScroll = new javax.swing.JScrollPane();
        brushList = new javax.swing.JList();
        nameField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        brushColButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Brush List");
        setFont(new java.awt.Font("Dialog", 0, 10));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        listScroll.setPreferredSize(new java.awt.Dimension(160, 160));
        brushList.setFont(new java.awt.Font("Dialog", 0, 10));
        brushList.setMaximumSize(null);
        brushList.setMinimumSize(null);
        brushList.setPreferredSize(null);
        brushList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        brushList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                brushListMousePressed(evt);
            }
        });

        listScroll.setViewportView(brushList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(listScroll, gridBagConstraints);

        nameField.setFont(new java.awt.Font("Dialog", 0, 10));
        nameField.setText("<no brush>");
        nameField.setEnabled(false);
        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        nameField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nameFieldMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(nameField, gridBagConstraints);

        saveButton.setFont(new java.awt.Font("Dialog", 0, 10));
        saveButton.setText("Save");
        saveButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(saveButton, gridBagConstraints);

        brushColButton.setBackground(new java.awt.Color(0, 0, 0));
        brushColButton.setFont(new java.awt.Font("Dialog", 1, 3));
        brushColButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2));
        brushColButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        brushColButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        brushColButton.setMaximumSize(new java.awt.Dimension(10, 10));
        brushColButton.setMinimumSize(new java.awt.Dimension(10, 10));
        brushColButton.setPreferredSize(new java.awt.Dimension(14, 14));
        brushColButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brushColButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(brushColButton, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    /**
     * Callback for click on the color button. Brings up a color chooser and
     * assigns the result to the current brush.
     */
    private void brushColButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brushColButtonActionPerformed
        Color current = brushColButton.getBackground();

        // Bring up a color chooser 
        Color c = JColorChooser.showDialog(this, "Choose Color", current); 

        brushColButton.setBackground(c);
        currentBrush.setColor(c);
    }//GEN-LAST:event_brushColButtonActionPerformed

    private boolean isShiftDown = false;
    private boolean isAltDown = false;
    private boolean isControlDown = false;
    
    /**
     * We dont get modifier keys with the select events, so we have to catch them here:
     */
    private void brushListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_brushListMousePressed
        isShiftDown = evt.isShiftDown();
        isAltDown = evt.isAltDown();
        isControlDown = evt.isControlDown();
    }//GEN-LAST:event_brushListMousePressed

    private void nameFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameFieldMouseClicked
        nameField.selectAll();
    }//GEN-LAST:event_nameFieldMouseClicked

    /**
     * Callback for click on the save button.
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if ((currentBrush != null) && (!nameField.getText().equals("")) && (!nameField.getText().startsWith("<"))){
            boolean newName = true;
            String name = nameField.getText();
            currentBrush.setName(name);
            DefaultListModel model = (DefaultListModel)brushList.getModel();
            for (int i=0; i<model.size(); i++){
                if (((Brush)model.elementAt(i)).getName().equals(name)){
                    model.setElementAt(currentBrush, i);
                    brushList.setSelectedIndex(i);
                    newName = false;
                    break;
                }
            }
            if (newName){
                model.addElement(currentBrush);
                brushList.setSelectedIndex(model.size() - 1);
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm

    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new BrushList().show();
    }
    
    /**
     * Callback triggered if the current brush of the parent component changed.
     */
    public void brushChanged(Brush b){
        if (b == null){
            nameField.setText("<no brush>");
            nameField.setEnabled(false);
            saveButton.setEnabled(false);
            brushColButton.setEnabled(false);
            return;
        }
        else {
            currentBrush = b;
            
            if (b.getName() == null){
                nameField.setText("<unnamed brush>");
                brushList.clearSelection();
            }
            else {
                nameField.setText(b.getName());
            }
            nameField.setEnabled(true);
            saveButton.setEnabled(true);
            brushColButton.setEnabled(true);
            brushColButton.setBackground(b.getColor());
            return;
        }
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()){ //ignore all but last of multiple events
            Brush b = (Brush)(((JList)e.getSource()).getSelectedValue());
            if (b != null){
                if (isShiftDown){
                    Brush newBrush = parent.getCurrentBrush().add(b);
                    newBrush.setName(parent.getCurrentBrush().getName() + " + " + b.getName());
                    parent.setCurrentBrush(newBrush);
                    brushList.setSelectedIndex(-1);
                }
                else if (isAltDown){
                    Brush newBrush = parent.getCurrentBrush().subtract(b);
                    newBrush.setName(parent.getCurrentBrush().getName() + " - " + b.getName());
                    parent.setCurrentBrush(newBrush);
                    brushList.setSelectedIndex(-1);
                }
                else if (isControlDown){
                    Brush newBrush = parent.getCurrentBrush().intersect(b);
                    newBrush.setName(parent.getCurrentBrush().getName() + " X " + b.getName());
                    parent.setCurrentBrush(newBrush);
                    brushList.setSelectedIndex(-1);
                }
                else {
                    if (parent.getCurrentBrush() != b){
                        parent.setCurrentBrush(b);
                    }
                }
            }
            
        }
        
    }
    
    /**
     * Called when the current brush of the parent component is modified,
     * but not replaced by another brush.
     */
    public void brushModified(Brush b) {
        //do nothing
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brushColButton;
    private javax.swing.JList brushList;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton saveButton;
    private javax.swing.JScrollPane listScroll;
    // End of variables declaration//GEN-END:variables
    
}
