/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Jessica
 */
public class DBConnection {
    protected Connection conn = null;
    protected String table = "";
    protected String [] select = {"*"};
    protected String where = "";
    protected String primaryKey = "";
    protected Map<String, Object> dataSet = new HashMap<String, Object>();
    
    public DBConnection(){
        try {
            connect();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection connect() throws SQLException {
        if(conn == null){
            try {
                String DB = "jdbc:mysql://localhost:3306/tugas_kbp_penjualan";
                String user = "root";
                String pass = "root";
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                conn = (Connection) DriverManager.getConnection(DB,user,pass);
            } catch (SQLException e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(null,"Connection Failed");
            }
        }
        return conn;
    }
    
    public ResultSet get() throws SQLException{
        ResultSet sql;
        Statement stm = conn.createStatement();
        String query = "select ";
        query += String.join(",", select);
        query += " from " + table + " ";
        if(!where.isEmpty()){
            query += " where " + where;
        }
        sql = stm.executeQuery(query);
        return sql;
    }
    
    public int getCount() throws SQLException{
        int jml = 0;
        this.setSelect(new String[]{"count("+primaryKey+") as jml"});
        ResultSet sql = this.get();

        while(sql.next()){
            jml = sql.getInt("jml");
        }
        return jml;
    }
    
    public int insert() throws SQLException{
        int pk = 0;
        String[] dataString = new String[dataSet.size()];
        Statement stm = conn.createStatement();
        String query = "insert into " + table + " set ";
        int i = 0;
        for (Entry<String, Object> entry : dataSet.entrySet()) {
            dataString[i] = entry.getKey() + "=\"" + entry.getValue() + "\"";
            i++;
        }
        query += String.join(",", dataString);
        if(!where.isEmpty()){
            query += " where " + where;
        }
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        int affectedRows = stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs != null && rs.next()) {
          pk = rs.getInt(1);
        }
        return pk;
    }

    public int update() throws SQLException{
        int pk;
        Statement stm = conn.createStatement();
        String query = "update " + table + " set ";
        String[] dataString = new String[dataSet.size()];
        int i = 0;
        for (Entry<String, Object> entry : dataSet.entrySet()) {
            dataString[i] = entry.getKey() + "=\"" + entry.getValue() + "\"";
            i++;
        }
        query += String.join(",", dataString);
        if(!where.isEmpty()){
            query += " where " + where;
        }
        pk = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        return pk;
    }
    
    public boolean deleteByPk(int pK) throws SQLException{
        java.sql.Statement stm = conn.createStatement();
        return stm.executeUpdate("delete from "+table+" where "+primaryKey+" = "+pK+"") != Statement.EXECUTE_FAILED;
    }
    /**
     * @param aTable the table to set
     */
    public void setTable(String aTable) {
        table = aTable;
    }

    /**
     * @param aSelect the select to set
     */
    public void setSelect(String[] aSelect) {
        select = aSelect;
    }

    /**
     * @param aInsert the insert to set
     */
    public void setDataSet(HashMap<String, Object> aInsert) {
        dataSet = aInsert;
    }

    /**
     * @param aWhere the where to set
     */
    public void setWhere(String aWhere) {
        where = aWhere;
    }

    /**
     * @param aPrimaryKey the primaryKey to set
     */
    public void setPrimaryKey(String aPrimaryKey) {
        primaryKey = aPrimaryKey;
    }

}
