/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import static Helpers.Common.validation;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author jessica
 */
public final class InputValidation implements DocumentListener {
    private String tipeData = "int";
    private JTextField tf = null;
    private boolean onInsert = false;
    private boolean onRemove = false;
    private boolean onUpdate = false;
    
    public InputValidation(String atipeData, JTextField aTf, boolean aOnInsert,boolean aOnRemove, boolean aOnUpdate){
        setTipeData(atipeData);
        setTf(aTf);
        setOnInsert(aOnInsert);
        setOnRemove(aOnRemove);
        setOnUpdate(aOnUpdate);
    }
    
    public boolean verify() {
        JTextField temp = (JTextField) getTf();
        boolean isValid = validation(tipeData,temp.getText()); 
        return isValid;
    }

    public void setTipeData(String tipeData) {
        this.tipeData = tipeData;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        verify();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        verify();
    }

    /**
     * @return the tipeData
     */
    public String getTipeData() {
        return tipeData;
    }

    /**
     * @param tipeData the tipeData to set
     */
    public JTextField getTf() {
        return tf;
    }

    public void setTf(JTextField tf) {
        this.tf = tf;
    }

    /**
     * @return the onInsert
     */
    public boolean isOnInsert() {
        return onInsert;
    }

    /**
     * @param onInsert the onInsert to set
     */
    public void setOnInsert(boolean onInsert) {
        this.onInsert = onInsert;
    }

    /**
     * @return the onRemove
     */
    public boolean isOnRemove() {
        return onRemove;
    }

    /**
     * @param onRemove the onRemove to set
     */
    public void setOnRemove(boolean onRemove) {
        this.onRemove = onRemove;
    }

    /**
     * @return the onUpdate
     */
    public boolean isOnUpdate() {
        return onUpdate;
    }

    /**
     * @param onUpdate the onUpdate to set
     */
    public void setOnUpdate(boolean onUpdate) {
        this.onUpdate = onUpdate;
    }
    
}
