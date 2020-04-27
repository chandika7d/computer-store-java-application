/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Views.laporan.Penjualan;
import Views.laporan.Pelanggan;
import javax.swing.JInternalFrame;

/**
 *
 * @author jessica
 */
public class ReportController extends Controller {
    private String jreport = "";
    private JInternalFrame View;
    
    public ReportController(String ajreport){
        jreport = ajreport;
        JInternalFrame jif = null;
        switch(jreport){
            case "penjualan":
                jif = (JInternalFrame) new Penjualan();
                break;
            case "pelanggan":
                jif = (JInternalFrame) new Pelanggan();
                break;
            default:
                jif = (JInternalFrame) new Penjualan();
        }
        View = jif;
    }
    
    @Override
    public JInternalFrame getView(){
        return View;
    }
}
