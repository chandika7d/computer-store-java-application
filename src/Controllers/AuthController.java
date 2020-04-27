/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.ModelAuth;
import Views.Login;
import Views.MainForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author jessica
 */
public class AuthController extends Controller {
    Login lg = new Login();
    
    public void show(){
        RefineryUtilities.centerFrameOnScreen(lg);
        lg.setVisible(true);
        ActionListener l = (ActionEvent e) -> {
            login();
        };
        lg.getBtnLogin().addActionListener(l);
    }
    
    public void login(){
        ModelAuth mA = new ModelAuth();
        String email = lg.getTfemail().getText();
        String password = lg.getTfpassword().getText();
        try {
            if (mA.checkLogin(email, password)) {
                JOptionPane.showMessageDialog(null,"Login Berhasil");
                MainForm mainForm = new MainForm();
                lg.setVisible(false);
                lg.dispose();
            }else{
                JOptionPane.showMessageDialog(null,"Login Gagal\nMasukan username dan password yang sesuai");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
