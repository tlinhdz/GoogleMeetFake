///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package dal;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import model.User;
//
///**
// *
// * @author admin
// */
//public class UserDAO extends DBContext {
//    public User checkUserInfo(String username,String password){
//        String query = "SELECT * FROM [USER] where username=? and password=?";
//        
//        try{
//            PreparedStatement ps = con.prepareStatement(query);
//            ps.setString(1, username);
//            ps.setString(2, password);
//            
//            ResultSet rs = ps.executeQuery();
//            if(rs.next()){
//                return new User(rs.getString("username"),rs.getString("password"),rs.getString("email"),rs.getString("created_at"),rs.getString("avatar"),rs.getInt("role"));
//            }
//        }catch(Exception e){
//            System.out.println(e);
//        }
//        return null;
//    }
//    
//    
//    public boolean checkUserInfo(String username ){
//        String query = "SELECT * FROM [USER] where username=?";
//        
//        try{
//            PreparedStatement ps = con.prepareStatement(query);
//            ps.setString(1, username);
//            
//            ResultSet rs = ps.executeQuery();
//            if(rs.next()){
//                return true;
//            }
//        }catch(Exception e){
//            System.out.println(e);
//        }
//        return false;
//    }
//    
//    public void addUser(String username,String password,String email,String avatar,int role){
//        String query = "INSERT INTO [USER] VALUES(?,?,?,?,?,?)";
//        
//        try{
//            java.util.Date d = new java.util.Date();
//            java.sql.Date date = new java.sql.Date(d.getTime());
//            
//            PreparedStatement ps = con.prepareStatement(query);
//            ps.setString(1, username);
//            ps.setString(2, password);
//            ps.setString(3,email);
//            ps.setDate(4, date);
//            ps.setString(5, avatar);
//            ps.setInt(6, role);
//            
//            ps.executeUpdate();
//        }catch(Exception e){
//            System.out.println(e);
//        }
//    }
//}
