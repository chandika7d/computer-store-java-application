/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import Helpers.DBConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jessica
 */
public class FileManager {
    public File getFile(String[] extension){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Allow Extension " + String.join(", ", extension), extension);
        File file = null;
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = new File(chooser.getSelectedFile().getPath());
        }
        return file;
    }
    public int insertImage(File file) throws SQLException, FileNotFoundException {
        Connection conn = (Connection) new DBConnection().connect();
        PreparedStatement statement = null;
        FileInputStream inputStream = null;
        int idfile = 0;
 
        try {
            inputStream = new FileInputStream(file);
            String filename = file.getName();
 
            statement = conn.prepareStatement("insert into file(filename, filedata) " + "values(?,?)",Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, filename);
            statement.setBinaryStream(2, (InputStream) inputStream, (int)(file.length()));
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            
            if (rs.next()){
                idfile = rs.getInt(1);
                System.out.println(idfile);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: - " + e);
        } catch (SQLException e) {
            System.out.println("SQLException: - " + e);
        }
        return idfile;
    }
}
