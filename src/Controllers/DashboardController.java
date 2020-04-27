/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.ModelPelanggan;
import Models.ModelPembelian;
import Models.ModelPenjualan;
import Models.ModelProduk;
import Views.Dashboard;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jessica
 */
public class DashboardController  extends Controller {
    public DashboardController(){
        Dashboard dashboard = new Dashboard();
        View = dashboard;
        int jmlProduk = 0;
        int jmlPelanggan = 0;
        int jmlPembelian = 0;
        int jmlPenjualan = 0;
        try {
            jmlProduk = new ModelProduk().getCount();
            jmlPelanggan = new ModelPelanggan().getCount();
            jmlPembelian = new ModelPembelian().getCount();
            jmlPenjualan = new ModelPenjualan().getCount();
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dashboard.getLproduk().setText(jmlProduk + "");
        dashboard.getLpelanggan().setText(jmlPelanggan + "");
        dashboard.getLpenjualan().setText(jmlPenjualan + "");
        dashboard.getLpembelian().setText(jmlPembelian + "");
    }
}
