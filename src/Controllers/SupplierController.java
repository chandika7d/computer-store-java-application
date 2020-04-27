/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.ModelSupplier;
import Views.master.MasterSupplier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
public class SupplierController extends Controller {
    private MasterSupplier View = new MasterSupplier();
    private ArrayList<String[]> semuadatasupplier = new ArrayList<>();
    private String idsupplier;
    
    public SupplierController(){
        try {
            setData();
            createEventListener();
            createInputFilter();
        } catch (SQLException ex) {
            Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setData() throws SQLException {
        String searchtext = View.getTfsearch().getText();
        ModelSupplier ms = new ModelSupplier();
        ms.setWhere("nmsupplier like \"%"+searchtext+"%\"");
        java.sql.ResultSet sql = ms.get();
        
        Object[] Baris ={"No","Nama Supplier", "No Handphone", "Alamat", "Keterangan"};
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        semuadatasupplier.removeAll(semuadatasupplier);
        int no = 1;
        while(sql.next()){
            String idsupplier = sql.getString("idsupplier");
            String nmsupplier = sql.getString("nmsupplier");
            String nohp = sql.getString("nohp");
            String alamat = sql.getString("alamat");
            String keterangan = sql.getString("keterangan");
            String[] data={no+"", nmsupplier, nohp, alamat, keterangan};
            semuadatasupplier.add(new String[]{idsupplier, nmsupplier, nohp, alamat, keterangan});
            tabmodel.addRow(data);
            no++;
        }
        View.getTproduk().setModel(tabmodel);
    }
    
    void insertData(){
        try {
            ModelSupplier ms = new ModelSupplier();
            String nmpelanggan = View.getTfnmproduk().getText();
            String nohp = View.getTfnohp().getText();
            String alamat = View.getTaalamat().getText();
            String keterangan = View.getTaketerangan().getText();
            ms.setDataSet(new HashMap<String, Object>(){{
                put("nmsupplier", nmpelanggan);
                put("nohp", nohp);
                put("alamat", alamat);
                put("keterangan", keterangan);
            }});
            ms.insert();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiTambahkan");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(MasterSupplier.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiTambahkan");
        }
    }
    
    void updateData(){
        try {
            ModelSupplier ms = new ModelSupplier();
            String nmsupplier = View.getTfnmproduk().getText();
            String nohp = View.getTfnohp().getText();
            String alamat = View.getTaalamat().getText();
            String keterangan = View.getTaketerangan().getText();
            ms.setDataSet(new HashMap<String, Object>(){{
                put("nmsupplier", nmsupplier);
                put("nohp", nohp);
                put("alamat", alamat);
                put("keterangan", keterangan);
            }});
            ms.setWhere("idsupplier="+idsupplier+"");
            ms.update();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiUpdate");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(MasterSupplier.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiUpdate");
        }
    }
    
    void deleteData(){
        int rowindex = View.getTproduk().getSelectedRow();
        String[] data = semuadatasupplier.get(rowindex);
        
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus data", "Konfirmasi Dialog", JOptionPane.YES_NO_OPTION);
        if(ok==0){
            try {
                ModelSupplier ms = new ModelSupplier();
                ms.deleteByPk(Integer.parseInt(data[0]));
                JOptionPane.showMessageDialog(null,"Data Berhasil DiHapus");
                resetForm();
                setData();
            } catch (SQLException ex) {
                Logger.getLogger(MasterSupplier.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Data Gagal DiHapus");
                System.out.println("Data Gagal DiHapus");
            }
        }
    }
    
    void resetForm(){
        View.getTfnmproduk().setText("");
        View.getTfnohp().setText("");
        View.getTaalamat().setText("");
        View.getTaketerangan().setText("");
    }
    
    void createEventListener(){
        View.getBtnEdit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idsupplier != null) {
                    updateData();
                    View.getBtnEdit().setText("Edit");
                    idsupplier = null;
                }else{
                    int rowindex = View.getTproduk().getSelectedRow();
                    String[] data = semuadatasupplier.get(rowindex);
                    View.getTfnmproduk().setText(data[1]);
                    View.getTfnohp().setText(data[2]);
                    View.getTaalamat().setText(data[3]);
                    View.getTaketerangan().setText(data[4]);
                    idsupplier = data[0];
                    View.getBtnEdit().setText("Simpan");
                }
            }
        });
        View.getBtnAdd().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        View.getBtnDelete().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
        View.getTfsearch().addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    setData();
                } catch (SQLException ex) {
                    Logger.getLogger(MasterSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        View.getTfnohp().addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                Helpers.Common.validation("No HP",View.getTfnohp().getText());
            }
        });
    }
    
    void createInputFilter(){
    }
    
    public JInternalFrame getView(){
        return (JInternalFrame) View;
    }
}
