/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

//import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import model.User;

/**
 *
 * @author admin
 */
@WebServlet(name = "user", urlPatterns = {"/user"})
public class user extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet user</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet user at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        String op = request.getParameter("op");
//        if( op != null){
//            switch(op){
//                case "register":{
//                    request.setAttribute("op", "REGISTER");
//                    request.getRequestDispatcher("view/lr.jsp").forward(request,response);
//                    
//                    break;
//                }
//                case "login":{
//                    request.setAttribute("op", "LOGIN");
//                    request.getRequestDispatcher("view/lr.jsp").forward(request,response);
//                    
//                    break;
//                }
//                case "logout":{
//                    HttpSession session = request.getSession();
//                    session.invalidate();
//                    
//                    response.sendRedirect("");
//                }
//            }
//        }else{
//            response.setStatus(400);
//        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        String op = request.getParameter("op");
//        if( op != null){
//            UserDAO upb = new UserDAO();
//            String username = request.getParameter("username");
//            String password = request.getParameter("password");
//            
//            switch(op){
//                case "login":{
//                    User u = upb.checkUserInfo(username, password);
//                    if( u != null){
//                        setSession(request,username,u.getAvatar(),u.getRole());
//                        
//                        response.sendRedirect("");
//                    }else{
//                        request.setAttribute("op","LOGIN");
//                        request.setAttribute("error","Username or password not match");
//                        request.getRequestDispatcher("view/lr.jsp").forward(request, response);
//                    }
//                    
//                    break;
//                }
//                case "register":{
//                    username = request.getParameter("username");
//                    password = request.getParameter("password");
//                    String email = request.getParameter("email");
//                    int role = 1;
//                    String avatar = "default";
//                    
//                    if( !upb.checkUserInfo(username)){
//                        upb.addUser(username, password, email, avatar, role);
//                        setSession(request,username,avatar,role);
//                        response.sendRedirect("");
//                    }else{
//                        request.setAttribute("op","REGISTER");
//                        request.setAttribute("error","Register failed,please check your info again!");
//                        request.getRequestDispatcher("view/lr.jsp").forward(request, response);
//                    }
//                    
//                    break;
//                }
//                default:{
//                    response.sendError(400);
//                }
//            }
//        }else{
//            response.sendError(400);
//        }
    }

    
    void setSession(HttpServletRequest request,String username ,String avatar,int role){
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        session.setAttribute("avatar", avatar);
        session.setAttribute("role", role);
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
