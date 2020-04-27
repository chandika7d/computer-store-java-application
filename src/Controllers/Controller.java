/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import javax.swing.JInternalFrame;

/**
 *
 * @author jessica
 */
public class Controller {

    protected JInternalFrame View;

    public void showView() {
        getView().setVisible(true);
    }
    
    /**
     * @return the View
     */
    public JInternalFrame getView() {
        return View;
    }

    /**
     * @param View the View to set
     */
    public void setView(JInternalFrame View) {
        this.View = View;
    }

    
}
