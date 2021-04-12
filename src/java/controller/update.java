/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PendingConnection;
import model.UpdateConnection;
import org.json.simple.JSONObject;

/**
 *
 * @author admin
 */
@WebServlet(name = "update", urlPatterns = {"/update"} , asyncSupported = true )
public class update extends HttpServlet {
    ArrayList<UpdateConnection> updateQueue = new ArrayList();
    ArrayList<PendingConnection> pendingUpdateQueue = new ArrayList();
    boolean isProcessing = false;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    public update(){
        Timer timer = new Timer();
        
        timer.schedule(new TimerTask(){
                public void run(){
                    synchronized(updateQueue){
                        for(int i=0 ; i < updateQueue.size() ; i++){
                            if( !updateQueue.get(i).getConnection().getResponse().isCommitted())
                                updateQueue.get(i).getConnection().complete();
                        }
                        
                        updateQueue.clear();
                    }
                }
            },new Date(), 100000);
        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet update</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet update at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void sendJsonMessage(HttpServletResponse response,JSONObject message){
        try{
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);
            PrintWriter out = response.getWriter();
            
            out.write( message.toString());
            out.flush();
            out.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    private UpdateConnection getUpdateConnection(String cloneName){
        for(int i=0 ; i< updateQueue.size(); i++){
            if( updateQueue.get(i).getcloneName().equals(cloneName)){
                return updateQueue.get(i);
            }
        }
        return null;
    }
    
     private PendingConnection getPendingUpdateConnection(String cloneName){
        for(int i=0 ; i< pendingUpdateQueue.size(); i++){
            if( pendingUpdateQueue.get(i).getcloneName().equals(cloneName)){
                return pendingUpdateQueue.get(i);
            }
        }
        return null;
    }
    
    private void updateQueue(HttpServletRequest request){
        if( updateQueue == null)
            updateQueue = (ArrayList<UpdateConnection>)request.getServletContext().getAttribute("updateQueue");
        
        if( pendingUpdateQueue == null)
            pendingUpdateQueue = (ArrayList<PendingConnection>)request.getServletContext().getAttribute("pendingUpdateQueue");
    }
     
    
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
        synchronized(this){
            while(isProcessing){
                try{
                    wait();
                }catch(InterruptedException e){
                    System.out.println(e);
                }
            }
            
            isProcessing = true;
            String cloneName = request.getParameter("clonename");
            UpdateConnection conn = getUpdateConnection(cloneName);

            if( conn != null){
                AsyncContext ac = request.startAsync(request,response);
                ac.setTimeout(200000);
                conn = new UpdateConnection(cloneName,ac);
            }else{
                PendingConnection pConn = getPendingUpdateConnection(cloneName);
                if( pConn != null){
                    sendJsonMessage(response, pConn.getMessage());
                    pendingUpdateQueue.remove(pConn);
                    System.out.println(pendingUpdateQueue.size()+ " pSize");
                }else{
                    AsyncContext ac = request.startAsync(request,response);
                    ac.setTimeout(200000);
                    updateQueue.add( new UpdateConnection(cloneName, ac));
                }
            }
            
            ServletContext context =request.getServletContext();
            if( context.getAttribute("updateQueue") == null ){
                context.setAttribute("updateQueue", updateQueue);
                context.setAttribute("pendingUpdateQueue", pendingUpdateQueue);
            }
            isProcessing = false;

            notify();
        }    
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
        processRequest(request, response);
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
