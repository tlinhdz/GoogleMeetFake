/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.parser.JSONParser;
import model.ChatRoom;
import model.PendingConnection;
import model.UpdateConnection;
import org.json.simple.JSONObject;

/**
 *
 * @author admin
 */
@WebServlet(name = "signalling", urlPatterns = {"/signalling"})
public class signalling extends HttpServlet {
    ArrayList<UpdateConnection> updateQueue;
    ArrayList<PendingConnection> pendingUpdateQueue;
    ArrayList<ChatRoom> room;
    
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet signalling</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet signalling at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        processRequest(request, response);
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
        try{
            updateAppContext(request);
            String body = getBody(request);
            

            JSONParser parser = new JSONParser();
            JSONObject reqBody  = (JSONObject) parser.parse(body);

            String roomid = (String)reqBody.get("roomid");
            String op = (String)reqBody.get("op");
            
            String cloneName = (String)reqBody.get("clonename");
            //System.out.println( request.getParameterMap());   
            if( roomid != null || cloneName != null){
                ChatRoom r = searchChatRoom(roomid);
                if( r != null && op != null){
                    
                    switch(op){
                        case "offer":
                        case "candidate":
                        case "checkingOffer":
                        case "message":
                        case "answer" :{
                            List<String> arr = new ArrayList();
                            JSONObject val = (JSONObject)reqBody.get("val");
                            if(val == null){
                                throw new Exception();
                            }
                            
                            if( op.equals("message") ){
                                arr =r.getMember();
                                
                            }else{
                                String receiver = (String)reqBody.get("to");
                                if(receiver != null){
                                    arr.add(receiver);
                                }else{
                                    throw new Exception();
                                }
                            }

                            for(int i=0 ; i<arr.size() ; i++){
                                JSONObject message = new JSONObject();
                                message.put("op", op);
                                message.put("val",val);
                                message.put("from",cloneName);
                                    
                                sendUpdateMessage(arr.get(i), message);
                            }
                            
                            break;
                        }
                        default:{
                            throw new Exception();
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e);
            response.sendError(400);
        }
    }
    
    public static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
    
     private void sendJsonMessage(HttpServletResponse response,JSONObject message){
        try{
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);
            out.write( message.toString());
            out.flush();
            out.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    private synchronized void sendUpdateMessage(String cloneName,JSONObject message ){
        while( isProcessing ){
            try { 
                wait(); 
            }catch(InterruptedException e) { 
                System.out.println(e);
            }
        }
 
        isProcessing = true;
        UpdateConnection conn = getUpdateConnection(cloneName);
        if( conn != null){
            try{
                ServletResponse response =  conn.getConnection().getResponse();
                sendJsonMessage((HttpServletResponse)response, message);
                conn.getConnection().complete();
                updateQueue.remove(conn);      
            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            PendingConnection pConn = new PendingConnection(cloneName, message);
            pendingUpdateQueue.add(pConn); 
            
        }

        isProcessing = false;
        notify();
    }
      
     private void updateAppContext(HttpServletRequest request){
        ServletContext context = request.getServletContext();
        if( updateQueue == null){
            if( context.getAttribute("updateQueue") != null ){
                updateQueue = (ArrayList<UpdateConnection>)context.getAttribute("updateQueue");
            }
        }
            
        
        if( pendingUpdateQueue == null){
            if( context.getAttribute("pendingUpdateQueue") != null ){
                pendingUpdateQueue = (ArrayList<PendingConnection>)context.getAttribute("pendingUpdateQueue");
            }
        }
        
        if( room == null){
            if( context.getAttribute("chatRoom") != null ){
                room = (ArrayList<ChatRoom>)context.getAttribute("chatRoom");
            }
        }
    }
     
     private ChatRoom searchChatRoom(String id){
        if( room != null){
            for(int i=0 ; i<room.size() ; i++){
                if( room.get(i).getId().equals(id)){
                    return room.get(i);
                }
            }
        }
        return null;
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
