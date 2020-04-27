/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import static Helpers.Common.thousandSparator;
import Helpers.FileManager;
import Helpers.InputValidation;
import Models.ModelKategoriProduk;
import Models.ModelProduk;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableModel;
import Views.master.MasterProduk;
import javax.swing.JInternalFrame;
import javax.swing.JTable;

/**
 *
 * @author jessica
 */
public class ProdukController  extends Controller {
    private MasterProduk View = new MasterProduk();
    private ArrayList<Object[]> semuadataproduk = new ArrayList<>();
    private ArrayList<String[]> semuadataktgproduk = new ArrayList<>();
    private String idproduk;
    private FileManager fm = new FileManager();
    private File fileGambar = null;
    
    public ProdukController(){
        try {
            setData();
            setKtgProduk();
            createEventListener();
            createInputFilter();
        } catch (SQLException ex) {
            Logger.getLogger(ProdukController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void setKtgProduk() throws SQLException{
        ModelKategoriProduk mp = new ModelKategoriProduk();
        java.sql.ResultSet sql = mp.get();
        
        View.getCbktgproduk().removeAllItems();
        View.getCmbFilterKtg().removeAllItems();
        
        semuadataktgproduk.removeAll(semuadataktgproduk);
        
        View.getCmbFilterKtg().addItem("Semua Kategori");
        while(sql.next()){
            String idktgproduk = sql.getString("idktgproduk");
            String nmktgproduk = sql.getString("nmktgproduk");
            View.getCbktgproduk().addItem(nmktgproduk);
            View.getCmbFilterKtg().addItem(nmktgproduk);
            semuadataktgproduk.add(new String[]{idktgproduk, nmktgproduk});
        }
    }
    
    void setData() throws SQLException{
        String searchtext = View.getTfsearch().getText();
        int indexktg = View.getCmbFilterKtg().getSelectedIndex();
        ModelProduk mp = new ModelProduk();
        mp.setTable("vproduk");
        if(indexktg != 0)
            mp.setWhere("nmproduk like \"%"+searchtext+"%\" and idktgproduk = \""+semuadataktgproduk.get(indexktg - 1)[0]+"\"");
        else
            mp.setWhere("nmproduk like \"%"+searchtext+"%\"");
        
        java.sql.ResultSet sql = mp.get();
        Object[] Baris ={"No", "Gambar","Nama Produk","Kategori Produk","Harga","Stock"};
        
        DefaultTableModel tabmodel = new DefaultTableModel(null, Baris){
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column==1) return ImageIcon.class;
                return Object.class;
            }
        };
        semuadataproduk.removeAll(semuadataproduk);
        
        int no = 1;
        while(sql.next()){
            String idproduk = sql.getString("idproduk");
            String nmproduk = sql.getString("nmproduk");
            String harga = thousandSparator(sql.getInt("harga"));
            String stock = sql.getString("stock");
            String idktgproduk = sql.getString("idktgproduk");
            String nmktgproduk = sql.getString("nmktgproduk");
            String satuan = sql.getString("satuan");
            String keterangan = sql.getString("keterangan");
            ImageIcon gambar = null;
            if(sql.getBytes("filedata") != null){
                gambar = new ImageIcon(sql.getBytes("filedata"));
                Image image = gambar.getImage();
                Image newimg = image.getScaledInstance(250, (gambar.getIconHeight()*250/gambar.getIconWidth()),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                gambar = new ImageIcon(newimg);
            }
            Object[] data={no+"", gambar, nmproduk, nmktgproduk, harga, stock};
            semuadataproduk.add(new Object[]{idproduk, gambar, nmproduk, sql.getInt("harga"), stock, idktgproduk, nmktgproduk, satuan, keterangan});
            tabmodel.addRow(data);
            no++;
        }
        
        View.getTproduk().setModel(tabmodel);
        
        View.getTproduk().getColumnModel().getColumn(0).setMaxWidth(50);
        View.getTproduk().getColumnModel().getColumn(1).setPreferredWidth(250);
        View.getTproduk().setRowHeight(250);
        View.getTproduk().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }
    
    void insertData(){
        try {
            ModelProduk mp = new ModelProduk();
            
            mp.setDataSet(new HashMap<String, Object>(){{
                int idfile = 0;
                if(fileGambar != null){
                    try {
                        idfile = fm.insertImage(fileGambar);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                put("nmproduk", View.getTfnmproduk().getText());
                put("harga",View.getTfhrgproduk().getText());
                put("idktgproduk",semuadataktgproduk.get(View.getCbktgproduk().getSelectedIndex())[0]);
                put("satuan",View.getTfsatuan().getText());
                put("keterangan",View.getTaKeterangan().getText());
                put("stock", 0);
                put("idfile",idfile);
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
            ModelProduk mp = new ModelProduk();
            
            mp.setDataSet(new HashMap<String, Object>(){{
                int idfile = 0;
                if(fileGambar != null){
                    try {
                        idfile = fm.insertImage(fileGambar);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                put("nmproduk", View.getTfnmproduk().getText());
                put("harga",View.getTfhrgproduk().getText());
                put("idktgproduk",semuadataktgproduk.get(View.getCbktgproduk().getSelectedIndex())[0]);
                put("satuan",View.getTfsatuan().getText());
                put("keterangan",View.getTaKeterangan().getText());
                if(idfile != 0)
                    put("idfile",idfile);
            }});
            mp.setWhere("idproduk="+idproduk);
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
        Object[] data = semuadataproduk.get(rowindex);
        
        int ok = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus data", "Konfirmasi Dialog", JOptionPane.YES_NO_OPTION);
        if(ok==0){
            try {
                ModelProduk mp = new ModelProduk();
                mp.deleteByPk(Integer.parseInt((String) data[0]));
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
        View.getTfhrgproduk().setText("");
        View.getTfsatuan().setText("");
        View.getTaKeterangan().setText("");
        View.getlStGambar().setText("");
        fileGambar = null;
    }
    
    void createEventListener(){
        View.getBtnAdd().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        View.getBtnEdit().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idproduk != null) {
                    updateData();
                    View.getBtnEdit().setText("Edit");
                    idproduk = null;
                }else{
                    int rowindex = View.getTproduk().getSelectedRow();
                    Object[] data = semuadataproduk.get(rowindex);
                    idproduk = data[0].toString();
                    View.getTfnmproduk().setText(data[2].toString());
                    View.getTfhrgproduk().setText(data[3].toString());
                    View.getTfsatuan().setText(data[7].toString());
                    View.getTaKeterangan().setText(data[8].toString());
                    View.getBtnEdit().setText("Simpan");
                    View.getCbktgproduk().setSelectedIndex(semuadataktgproduk.indexOf(data));
                }
            }
        });
        View.getBtnDelete().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
        View.getBtnFile().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] extension = {"jpg", "jpeg", "png"};
                fileGambar = fm.getFile(extension);
                if(fileGambar != null){
                    View.getlStGambar().setText("(File Selected)");
                }else{
                    View.getlStGambar().setText("");
                }
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
                try {
                    setData();
                    setKtgProduk();
                } catch (SQLException ex) {
                    Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
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
                    Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        View.getCmbFilterKtg().addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    try {
                        setData();
                    } catch (SQLException ex) {
                        Logger.getLogger(MasterProduk.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                
                }
            }
        });
    }
    void createInputFilter(){
        View.getTfhrgproduk().getDocument().addDocumentListener(new InputValidation("Nomor", View.getTfhrgproduk(), true, false, true));
    }
    void refresh() throws SQLException{
        resetForm();
        setData();
        setKtgProduk();
    }
    
    @Override
    public JInternalFrame getView(){
        return (JInternalFrame) View;
    }
}
