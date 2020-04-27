/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import static Helpers.Common.dateFormatter;
import static Helpers.Common.thousandSparator;
import Models.ModelPembelian;
import Models.ModelProduk;
import Models.ModelSupplier;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import Views.transaksi.TransaksiPembelian;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author jessica
 */
public class PembelianController extends Controller {
    TransaksiPembelian View = new TransaksiPembelian();
    private ArrayList<String[]> semuadatapembelian = new ArrayList<>();
    private ArrayList<String[]> datasupplier = new ArrayList<>();
    private ArrayList<String[]> dataproduk = new ArrayList<>();
    private ArrayList<String[]> tmpdetailpembelian = new ArrayList<>();
    private String iddpembelian, idpembelian;
    
    public PembelianController(){
        try {
            setData();
            setdatasupplier();
            setdataproduk();
            settmpdata();
        createEventListener();
        } catch (SQLException ex) {
            Logger.getLogger(PembelianController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void setData() throws SQLException {
        String searchtext = View.getTfsearch().getText();
        ModelPembelian mt = new ModelPembelian();
        mt.setTable("simplepembelian");
        mt.setWhere("tglpembelian like \"%"+searchtext+"%\"");
        java.sql.ResultSet sql = mt.get();
        
        Object[] Baris ={"No","Tanggal Pembelian", "Total Produk", "Total Harga", "Supplier", "Keterangan"};
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        int no = 1;
        while(sql.next()){
            String idpembelian = sql.getString("idpembelian");
            String tglpembelian = dateFormatter(sql.getObject("tglpembelian", Date.class), 1);
            String totalproduk = sql.getString("jumlahbarang");
            String totalharga = thousandSparator(sql.getInt("totalharga"));
            String supplier = sql.getString("nmsupplier");
            String keterangan = sql.getString("keterangan");
            String[] data={no+"", tglpembelian, totalproduk, totalharga, supplier, keterangan};
            semuadatapembelian.add(new String[]{idpembelian, tglpembelian, totalproduk, totalharga, supplier, keterangan});
            tabmodel.addRow(data);
            no++;
        }
        
        View.getTpembelian().setModel(tabmodel);
    }
    
    void insertData(){
        int totalharga = 0;
        for (int i = 0; i < tmpdetailpembelian.size(); i++) {
            totalharga += Integer.parseInt(tmpdetailpembelian.get(i)[1]) * Integer.parseInt(tmpdetailpembelian.get(i)[2]);
        }
        try {
            ModelPembelian mt = new ModelPembelian();
            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("keterangan", View.getTaketerangan().getText());
            dataset.put("totalharga", totalharga);
            dataset.put("idsupplier", datasupplier.get(View.getCbsupplier().getSelectedIndex())[0]);
            mt.setDataSet(dataset);
            idpembelian = mt.insert()+"";
            System.out.println(idpembelian);
            for (int i = 0; i < tmpdetailpembelian.size(); i++) {
                insertDataDetail(i);
            }
            setData();
            Object[] Baris ={"No","Produk", "Harga", "Qty"};
            DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
                @Override
                public boolean isCellEditable(int row, int column) {
                   return false;
                }
            };
            View.getTdetailpembelian().setModel(tabmodel);
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void insertDataDetail(int index){
        try {
            ModelPembelian mt = new ModelPembelian();
            mt.setTable("detailpembelian");
            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("idproduk", tmpdetailpembelian.get(index)[0]);
            dataset.put("harga", tmpdetailpembelian.get(index)[1]);
            dataset.put("jumlah", tmpdetailpembelian.get(index)[2]);
            dataset.put("idpembelian", idpembelian);
            mt.setDataSet(dataset);
            mt.insert();
            View.getTfqty().setText("");
            settmpdata();
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void settmpdata(){
        Object[] Baris ={"No","Produk", "Harga", "Qty"};
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        for (int i = 0; i < tmpdetailpembelian.size(); i++) {
            tabmodel.addRow(new String[]{(i+1)+"", tmpdetailpembelian.get(i)[3], tmpdetailpembelian.get(i)[1],tmpdetailpembelian.get(i)[2]});
        }
        View.getTdetailpembelian().setModel(tabmodel);
    }
    
    void inserttmpdata(){
        int index = View.getCbproduk().getSelectedIndex();
        tmpdetailpembelian.add(new String[]{dataproduk.get(index)[0],dataproduk.get(index)[1],View.getTfqty().getText(),dataproduk.get(index)[2]});
        settmpdata();
    }
    
    void updatetmpdata(){
        tmpdetailpembelian.set(View.getTdetailpembelian().getSelectedRow(), new String[]{dataproduk.get(View.getCbproduk().getSelectedIndex())[0],dataproduk.get(View.getCbproduk().getSelectedIndex())[1],View.getTfqty().getText(),dataproduk.get(View.getCbproduk().getSelectedIndex())[2]});
        settmpdata();
    }
    
    void deletetmpdata(){
        tmpdetailpembelian.remove(View.getTdetailpembelian().getSelectedRow());
        settmpdata();
    }
    
    void setdataproduk() throws SQLException {
        ModelProduk mp = new ModelProduk();
        java.sql.ResultSet sql = mp.get();
        View.getCbproduk().removeAllItems();
        dataproduk.clear();
        while(sql.next()){
            String idproduk = sql.getString("idproduk");
            String harga = sql.getString("harga");
            String nmproduk = sql.getString("nmproduk");
            View.getCbproduk().addItem(nmproduk);
            dataproduk.add(new String[]{idproduk, harga, nmproduk});
        }
    }
    
    void setdatasupplier() throws SQLException {
        ModelSupplier mp = new ModelSupplier();
        java.sql.ResultSet sql = mp.get();
        View.getCbsupplier().removeAllItems();
        datasupplier.clear();
        while(sql.next()){
            String idsupplier = sql.getString("idsupplier");
            String nmsupplier = sql.getString("nmsupplier");
            View.getCbsupplier().addItem(nmsupplier);
            datasupplier.add(new String[]{idsupplier, nmsupplier});
        }
    }
    
    void refreshData(){
        System.out.println("Masuk Pak Eko");
        try {
            setData();
            settmpdata();
            setdataproduk();
            setdatasupplier();
            tmpdetailpembelian.removeAll(tmpdetailpembelian);
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void createEventListener(){
        View.getBtnAdd().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                inserttmpdata();
            }
        });
        View.getBtnEdit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (iddpembelian != null) {
                    updatetmpdata();
                    View.getBtnEdit().setText("Edit");
                    iddpembelian = null;
                }else{
                    int rowindex = View.getTdetailpembelian().getSelectedRow();
                    String[] data = tmpdetailpembelian.get(rowindex);
                    iddpembelian = data[0];
                    View.getTfqty().setText(data[2]);
                    View.getCbproduk().setSelectedItem(data[3]);
                    View.getBtnEdit().setText("Simpan");
                }
            }
        });
        View.getBtnDelete().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deletetmpdata();
            }
        });
        View.getBtnSubmmit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        View.addInternalFrameListener(new InternalFrameListener(){
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                refreshData();
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        });
    }
    
    @Override
    public JInternalFrame getView(){
        return (JInternalFrame) View;
    }
}
