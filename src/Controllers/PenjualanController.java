/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import static Helpers.Common.dateFormatter;
import static Helpers.Common.thousandSparator;
import Models.ModelPenjualan;
import Models.ModelProduk;
import Models.ModelPelanggan;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import Views.transaksi.TransaksiPenjualan;

/**
 *
 * @author jessica
 */
public class PenjualanController extends Controller {
    TransaksiPenjualan View = new TransaksiPenjualan();
    private ArrayList<String[]> semuadatapenjualan = new ArrayList<>();
    private ArrayList<String[]> datapelanggan = new ArrayList<>();
    private ArrayList<String[]> dataproduk = new ArrayList<>();
    private ArrayList<String[]> tmpdetailpenjualan = new ArrayList<>();
    private String iddpenjualan, idpenjualan;
    public PenjualanController(){
        try {
            setData();
            setdatapelanggan();
            setdataproduk();
            settmpdata();
        createEventListener();
        } catch (SQLException ex) {
            Logger.getLogger(PenjualanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void setData() throws SQLException {
        String searchtext = View.getTfsearch().getText();
        ModelPenjualan mt = new ModelPenjualan();
        mt.setTable("simplepenjualan");
        mt.setWhere("tglpenjualan like \"%"+searchtext+"%\"");
        java.sql.ResultSet sql = mt.get();
        
        Object[] Baris ={"No","Tanggal Penjualan", "Total Produk", "Total Harga", "Pelanggan", "Keterangan"};
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        int no = 1;
        while(sql.next()){
            String idpenjualan = sql.getString("idpenjualan");
            String tglpenjualan = dateFormatter(sql.getObject("tglpenjualan", Date.class),1);
            String totalproduk = sql.getString("jumlahbarang");
            String totalharga = thousandSparator(sql.getInt("totalharga"));
            String pelanggan = sql.getString("nmpelanggan");
            String keterangan = sql.getString("keterangan");
            String[] data={no+"", tglpenjualan, totalproduk, totalharga, pelanggan, keterangan};
            semuadatapenjualan.add(new String[]{idpenjualan, tglpenjualan, totalproduk, totalharga, pelanggan, keterangan});
            tabmodel.addRow(data);
            no++;
        }
        
        View.getTpenjualan().setModel(tabmodel);
    }
    
    void insertData(){
        int totalharga = 0;
        for (int i = 0; i < tmpdetailpenjualan.size(); i++) {
            totalharga += Integer.parseInt(tmpdetailpenjualan.get(i)[1]) * Integer.parseInt(tmpdetailpenjualan.get(i)[2]);
        }
        try {
            ModelPenjualan mt = new ModelPenjualan();
            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("keterangan", View.getTaketerangan().getText());
            dataset.put("totalharga", totalharga);
            dataset.put("idpelanggan", datapelanggan.get(View.getCbpelanggan().getSelectedIndex())[0]);
            mt.setDataSet(dataset);
            idpenjualan = mt.insert()+"";
            System.out.println(idpenjualan);
            for (int i = 0; i < tmpdetailpenjualan.size(); i++) {
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
            View.getTdetailpenjualan().setModel(tabmodel);
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void insertDataDetail(int index){
        try {
            ModelPenjualan mt = new ModelPenjualan();
            mt.setTable("detailpenjualan");
            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("idproduk", tmpdetailpenjualan.get(index)[0]);
            dataset.put("harga", tmpdetailpenjualan.get(index)[1]);
            dataset.put("jumlah", tmpdetailpenjualan.get(index)[2]);
            dataset.put("idpenjualan", idpenjualan);
            mt.setDataSet(dataset);
            mt.insert();
            View.getTfqty().setText("");
            settmpdata();
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPenjualan.class.getName()).log(Level.SEVERE, null, ex);
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
        
        for (int i = 0; i < tmpdetailpenjualan.size(); i++) {
            tabmodel.addRow(new String[]{(i+1)+"", tmpdetailpenjualan.get(i)[3], tmpdetailpenjualan.get(i)[1],tmpdetailpenjualan.get(i)[2]});
        }
        View.getTdetailpenjualan().setModel(tabmodel);
    }
    
    void inserttmpdata(){
        int index = View.getCbproduk().getSelectedIndex();
        tmpdetailpenjualan.add(new String[]{dataproduk.get(index)[0],dataproduk.get(index)[1],View.getTfqty().getText(),dataproduk.get(index)[2]});
        settmpdata();
    }
    
    void updatetmpdata(){
        tmpdetailpenjualan.set(View.getTdetailpenjualan().getSelectedRow(), new String[]{dataproduk.get(View.getCbproduk().getSelectedIndex())[0],dataproduk.get(View.getCbproduk().getSelectedIndex())[1],View.getTfqty().getText(),dataproduk.get(View.getCbproduk().getSelectedIndex())[2]});
        settmpdata();
    }
    
    void deletetmpdata(){
        tmpdetailpenjualan.remove(View.getTdetailpenjualan().getSelectedRow());
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
    
    void setdatapelanggan() throws SQLException {
        ModelPelanggan mp = new ModelPelanggan();
        java.sql.ResultSet sql = mp.get();
        View.getCbpelanggan().removeAllItems();
        datapelanggan.clear();
        while(sql.next()){
            String idpelanggan = sql.getString("idpelanggan");
            String nmpelanggan = sql.getString("nmpelanggan");
            View.getCbpelanggan().addItem(nmpelanggan);
            datapelanggan.add(new String[]{idpelanggan, nmpelanggan});
        }
    }
    
    void refreshData(){
        System.out.println("Masuk Pak Eko");
        try {
            setData();
            settmpdata();
            setdataproduk();
            setdatapelanggan();
            tmpdetailpenjualan.removeAll(tmpdetailpenjualan);
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPenjualan.class.getName()).log(Level.SEVERE, null, ex);
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
                if (iddpenjualan != null) {
                    updatetmpdata();
                    View.getBtnEdit().setText("Edit");
                    iddpenjualan = null;
                }else{
                    int rowindex = View.getTdetailpenjualan().getSelectedRow();
                    String[] data = tmpdetailpenjualan.get(rowindex);
                    iddpenjualan = data[0];
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
        View.getBtnSubmit().addActionListener(new ActionListener(){
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
