/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Helpers.DBConnection;

/**
 *
 * @author jessica
 */
public class ModelPenjualan extends DBConnection{
    public ModelPenjualan(){
        table = "penjualan";
        primaryKey = "idpenjualan";
    }
}

