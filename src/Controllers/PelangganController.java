/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.ModelPelanggan;
import Views.master.MasterPelanggan;
import Views.master.MasterProduk;
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
public class PelangganController extends Controller {
    MasterPelanggan View = new MasterPelanggan();
    private ArrayList<String[]> semuadatapelanggan = new ArrayList<>();
    private String idpelanggan;
    
    public PelangganController(){
        try {
            setData();
            createEventListener();
            createInputFilter();
        } catch (SQLException ex) {
            Logger.getLogger(PelangganController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void setData() throws SQLException {
        String searchtext = View.getTfsearch().getText();
        ModelPelanggan mp = new ModelPelanggan();
        mp.setWhere("nmpelanggan like \"%"+searchtext+"%\"");
        java.sql.ResultSet sql = mp.get();
        
        Object[] Baris ={"No","Nama Pelanggan", "Email", "No Handphone", "Alamat"};
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        semuadatapelanggan.removeAll(semuadatapelanggan);
        int no = 1;
        while(sql.next()){
            String idpelanggan = sql.getString("idpelanggan");
            String nmpelanggan = sql.getString("nmpelanggan");
            String email = sql.getString("email");
            String nohp = sql.getString("nohp");
            String alamat = sql.getString("alamat");
            String[] data={no+"", nmpelanggan, email, nohp, alamat};
            semuadatapelanggan.add(new String[]{idpelanggan, nmpelanggan, email, nohp, alamat});
            tabmodel.addRow(data);
            no++;
        }
        
        View.getTproduk().setModel(tabmodel);
    }
    
    void insertData(){
        try {
            ModelPelanggan mp = new ModelPelanggan();
            String nmpelanggan = View.getTfnmproduk().getText();
            String email = View.getTfemail().getText();
            String nohp = View.getTfnohp().getText();
            String alamat = View.getTaalamat().getText();
            mp.setDataSet(new HashMap<String, Object>(){{
                put("nmpelanggan", nmpelanggan);
                put("email", email);
                put("nohp", nohp);
                put("alamat", alamat);
            }});
            mp.insert();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiTambahkan");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiTambahkan");
        }
    }
    
    void updateData(){
        try {
            ModelPelanggan mp = new ModelPelanggan();
            String nmpelanggan = View.getTfnmproduk().getText();
            String email = View.getTfemail().getText();
            String nohp = View.getTfnohp().getText();
            String alamat = View.getTaalamat().getText();
            mp.setDataSet(new HashMap<String, Object>(){{
                put("nmpelanggan", nmpelanggan);
                put("email", email);
                put("nohp", nohp);
                put("alamat", alamat);
            }});
            mp.setWhere("idpelanggan="+idpelanggan+"");
            mp.update();
            JOptionPane.showMessageDialog(null,"Data Berhasil DiUpdate");
            resetForm();
            setData();
        } catch (SQLException ex) {
            Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Data Gagal DiUpdate");
        }
    }
    
    void deleteData(){
        int rowindex = View.getTproduk().getSelectedRow();
        String[] data = semuadatapelanggan.get(rowindex);
        
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus data", "Konfirmasi Dialog", JOptionPane.YES_NO_OPTION);
        if(ok==0){
            try {
                ModelPelanggan mp = new ModelPelanggan();
                mp.deleteByPk(Integer.parseInt(data[0]));
                JOptionPane.showMessageDialog(null,"Data Berhasil DiHapus");
                resetForm();
                setData();
            } catch (SQLException ex) {
                Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,"Data Gagal DiHapus");
            }
        }
    }
    
    void resetForm(){
        View.getTfnmproduk().setText("");
        View.getTfemail().setText("");
        View.getTfnohp().setText("");
        View.getTaalamat().setText("");
        View.getBtnEdit().setText("Edit");
        idpelanggan = null;
    }
    
    void createEventListener(){
        View.getBtnEdit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idpelanggan != null) {
                    updateData();
                    View.getBtnEdit().setText("Edit");
                    idpelanggan = null;
                }else{
                    int rowindex = View.getTproduk().getSelectedRow();
                    String[] data = semuadatapelanggan.get(rowindex);
                    View.getTfnmproduk().setText(data[1]);
                    View.getTfemail().setText(data[2]);
                    View.getTfnohp().setText(data[3]);
                    View.getTaalamat().setText(data[4]);
                    idpelanggan = data[0];
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
                    Logger.getLogger(MasterPelanggan.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        View.getTfemail().addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                Helpers.Common.validation("Email",View.getTfemail().getText());
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
