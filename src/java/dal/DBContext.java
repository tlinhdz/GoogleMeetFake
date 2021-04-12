///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package dal;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
///**
// *
// * @author admin
// */
//public class DBContext {
//    Connection con;
//
//     public Connection getConnection()throws Exception {
//        String url = "jdbc:sqlserver://"+serverName+":"+portNumber +";databaseName="+dbName;
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        return DriverManager.getConnection(url, userID, password);
//    }
//    
//    public DBContext(){
//        try{
//            this.con = getConnection();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//     
//    private final String serverName = "localhost";
//    private final String dbName = "wemeet";
//    private final String portNumber = "1433";
//    private final String userID = "sa";
//    private final String password = "tuanlinh";
//}
