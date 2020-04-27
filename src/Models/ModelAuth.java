/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;
import Helpers.DBConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author jessica
 */
public class ModelAuth extends DBConnection{
    public boolean checkLogin(String email, String password) throws SQLException{
        ResultSet sql;
        boolean stlogin = false;
        
        this.setTable("user");
        this.setWhere("email = \""+email+"\" AND password = \""+password+"\"");
        
        sql = this.get();
        if (sql.first()) {
            if (sql.getString("email").equals(email) && sql.getString("password").equals(password)) {
                stlogin = true;
            }
        }
        return stlogin;
    }
}
