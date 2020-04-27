/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.JOptionPane;

/**
 *
 * @author jessica
 */
public class Common {
    
    public static String thousandSparator(int harga){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        return formatRupiah.format((double) harga);
    }
    
    public static String monthName(int numBulan){
        String nmbulan = "";
        switch(numBulan){
            case 1:
                nmbulan = "Januari";
                break;
            case 2:
                nmbulan = "Februari";
                break;
            case 3:
                nmbulan = "Maret";
                break;
            case 4:
                nmbulan = "April";
                break;
            case 5:
                nmbulan = "Mei";
                break;
            case 6:
                nmbulan = "Juni";
                break;
            case 7:
                nmbulan = "Juli";
                break;
            case 8:
                nmbulan = "Agustus";
                break;
            case 9:
                nmbulan = "September";
                break;
            case 10:
                nmbulan = "Oktober";
                break;
            case 11:
                nmbulan = "November";
                break;
            case 12:
                nmbulan = "Desember";
                break;
        }
        return nmbulan;
    }
    
    public static String dateFormatter(Date date, int tipe){
        SimpleDateFormat formatter;
        if(tipe == 0){
            formatter = new SimpleDateFormat("dd MMMM yyyy");
        }else{
            formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        }
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String stringDate = formatter.format(date);
        return stringDate;
        
    }
    
    public static boolean validation(String tipeData, String string){
        String regex = "";
        switch(tipeData){
            case "Email":
                regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                break;
            case "No HP":
                regex = "^(?:[0-9] ?){6,14}[0-9]$";
                break;
            default :
                regex = "^(?:[0-9] ?){0,11}[0-9]$";
        }
        boolean isValid = string.matches(regex);
        if(!isValid){
            JOptionPane.showMessageDialog(null, "Data yang dimasukan harus "+tipeData+"!");
        }
        return isValid;
    }
}
