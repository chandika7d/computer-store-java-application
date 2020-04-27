/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.ModelKategoriProduk;
import Views.master.MasterKategoriProduk;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jessica
 */
public class KategoriProdukController extends Controller {
    private ArrayList<Object[]> semuadataktgproduk = new ArrayList<>();
    private String idktgproduk;
    private MasterKategoriProduk mktgproduk = new MasterKategoriProduk();
    private MasterKategoriProduk View = new MasterKategoriProduk();
    
    public KategoriProdukController(){
        try {
            setData();
            createEventListener();
        } catch (SQLException ex) {
            Logger.getLogger(KategoriProdukController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void setData() throws SQLException {
        String searchtext = View.getTfsearch().getText();
        ModelKategoriProduk mktg = new ModelKategoriProduk();
        mktg.setWhere("nmktgproduk like \"%"+searchtext+"%\"");
        java.sql.ResultSet sql = mktg.get();
        
        Object[] Baris ={"No","Nama Kategori Produk"};
        Object[][] datas = new Object[mktg.getCount()][2];
        
        semuadataktgproduk.removeAll(semuadataktgproduk);
        int index = 0;
        while(sql.next()){
            String idktgproduk = sql.getString("idktgproduk");
            String nmktgproduk = sql.getString("nmktgproduk");
            Object[] data = {(index+1)+"", nmktgproduk};
            semuadataktgproduk.add(new String[]{idktgproduk, nmktgproduk});
            datas[index] = data;
            index++;
        }
        DefaultTableModel tabmodel = new DefaultTableModel(datas, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        View.getTproduk().setModel(tabmodel);
    }
    void insertData(){
        try {
            ModelKategoriProduk mktg = new ModelKategoriProduk();
            String nmktgproduk = View.getTfnmproduk().getText();
            mktg.setDataSet(new HashMap<String, Object>(){{
                put("nmktgproduk", nmktgproduk);
            }});
            mktg.insert();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiTambahkan");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(KategoriProdukController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiTambahkan");
        }
    }
    
    void updateData(){
        try {
            ModelKategoriProduk mktg = new ModelKategoriProduk();
            String nmktgproduk = View.getTfnmproduk().getText();
            mktg.setDataSet(new HashMap<String, Object>(){{
                put("nmktgproduk", nmktgproduk);
            }});
            mktg.setWhere("idktgproduk="+idktgproduk);
            mktg.update();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiUpdate");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(KategoriProdukController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiUpdate");
        }
    }
    
    void deleteData(){
        int rowindex = View.getTproduk().getSelectedRow();
        Object[] data = semuadataktgproduk.get(rowindex);
        
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus data", "Konfirmasi Dialog", JOptionPane.YES_NO_OPTION);
        if(ok==0){
            try {
                ModelKategoriProduk mktg = new ModelKategoriProduk();
                mktg.deleteByPk(Integer.parseInt((String) data[0]));
                JOptionPane.showMessageDialog(null,"Data Berhasil DiHapus");
                resetForm();
                setData();
            } catch (SQLException ex) {
                Logger.getLogger(KategoriProdukController.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Data Gagal DiHapus");
            }
        }
    }
    
    void resetForm(){
        
    }
    void createEventListener(){
        View.getTfsearch().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    setData();
                } catch (SQLException ex) {
                    Logger.getLogger(KategoriProdukController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        });
        View.getBtnAdd().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        View.getBtnEdit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idktgproduk != null) {
                    updateData();
                    View.getBtnEdit().setText("Edit");
                    idktgproduk = null;
                }else{
                    int rowindex = View.getTproduk().getSelectedRow();
                    Object[] data = semuadataktgproduk.get(rowindex);
                    idktgproduk = data[0].toString();
                    View.getTfnmproduk().setText(data[1].toString());
                    View.getBtnEdit().setText("Simpan");
                }
            }
        });
        View.getBtnDelete().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
    }
    @Override
    public JInternalFrame getView(){
        return (JInternalFrame) View;
    }
}
