/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.sun.jndi.toolkit.ctx.Continuation;
import static controller.signalling.getBody;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ChatRoom;
import model.PendingConnection;
import model.UpdateConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author admin
 */
@WebServlet(name = "room", urlPatterns = {"/room"}, asyncSupported = true)
public class room extends HttpServlet {

    ArrayList<UpdateConnection> updateQueue;
    ArrayList<PendingConnection> pendingUpdateQueue;
    ArrayList<ChatRoom> room = new ArrayList();

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
        try {
            updateAppContext(request);

            String op = request.getParameter("op");
            String roomid = request.getParameter("roomid");
            String cloneName = request.getParameter("clonename");
            String nummem = request.getParameter("nummem");

            if (request.getMethod().equalsIgnoreCase("post")) {
                String body = getBody(request);
                JSONParser parser = new JSONParser();
                JSONObject reqBody = (JSONObject) parser.parse(body);

                op = (String) reqBody.get("op");
                roomid = (String) reqBody.get("roomid");
                cloneName = (String) reqBody.get("clonename");
            }

            if (op != null) {
                System.out.println(op + " room:" + roomid + " cloneName:" + cloneName);
                switch (op) {
                    case "justjoinroom":
                    case "justleaveroom": {
                        ChatRoom r = getChatRoom(roomid);

                        if (r != null) {
                            List<String> member;
                            if (op.equals("justjoinroom")) {
                                member = r.getMember();
                                System.out.println(r.getMaxNumberOfMember());
                                System.out.println(r.getMember().size());
                                if (!r.isFull()) {
                                    JSONObject message = new JSONObject();
                                    message.put("op", "newmember");
                                    message.put("clonename", cloneName);

                                    for (int i = 0; i < member.size(); i++) {
                                        sendUpdateMessage(member.get(i), message);
                                    }

                                    r.newMember(cloneName);
                                    message.clear();
                                    message.put("mem", r.getMember().size());
                                    sendJsonMessage(response, message);
                                } else {
                                    response.sendError(204);
                                }
                            } else {
                                r.leaveRoom(cloneName);
                                member = r.getMember();

                                if (member.size() == 0) {
                                    room.remove(r);
                                    r = null;
                                } else {
                                    JSONObject message = new JSONObject();
                                    message.put("op", "userleaveroom");
                                    message.put("clonename", cloneName);

                                    System.out.println("user leave room");
                                    for (int i = 0; i < member.size(); i++) {
                                        sendUpdateMessage(member.get(i), message);
                                    }
                                }
                            }
                        } else {
                            response.sendError(204);
                        }

                        break;
                    }
                    case "createroom": {
                        if (cloneName != null && nummem != null) {
                            int maxNumMem = checkCloneNameAndNummem(cloneName, nummem);
                            if (maxNumMem != -1) {
                                ChatRoom r = new ChatRoom(String.valueOf(System.currentTimeMillis()), maxNumMem);
                                room.add(r);
                                System.out.println("new room : " + r.getId());
                                PrintWriter out = response.getWriter();
                                out.print(r.getId());
                                out.flush();
                                out.close();

                                return;
                            }
                        }

                        response.sendError(400);
                        break;
                    }
                    case "check": {
                        ChatRoom r = getChatRoom(roomid);
                        if (r != null) {
                            if (!r.isFull()) {
                                response.setStatus(200);
                                return;
                            }
                        }

                        throw new Exception("LOI CUA SERVER");
                    }
                    default: {
                        throw new Exception();
                    }
                }
            } else {
                if (roomid != null && cloneName != null) {
                    ChatRoom r = getChatRoom(roomid);
                    if (r != null) {
                        List<String> member = r.getMember();
                        request.setAttribute("member", member);
                        request.setAttribute("clonename", cloneName);
                        request.setAttribute("roomid", roomid);

                        request.getRequestDispatcher("view/room.jsp").forward(request, response);
                    } else {
                        response.sendError(400);
                    }

                } else {
                    response.setStatus(400);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(204);
        }
    }

    int checkCloneNameAndNummem(String cloneName, String nummem) {
        int t;
        try {
            int maxNumMem = Integer.valueOf(nummem);
            if (maxNumMem > 32 || maxNumMem < 2 || !cloneName.matches("[\\d\\s\\w]+")) {
                t = -1;
            } else {
                System.out.println("dfssdf");
                t = maxNumMem;
            }
        } catch (Exception e) {
            t = -1;
        }

        return t;
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
        processRequest(request, response);
    }

    private void sendJsonMessage(HttpServletResponse response, JSONObject message) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(200);

            PrintWriter out = response.getWriter();

            out.print(message);
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void sendUpdateMessage(String cloneName, JSONObject message) {
        UpdateConnection conn = getUpdateConnection(cloneName);
        if (conn != null) {
            try {
                HttpServletResponse response = (HttpServletResponse) conn.getConnection().getResponse();

                sendJsonMessage(response, message);
                conn.getConnection().complete();
                System.out.println(message);

                updateQueue.remove(conn);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            PendingConnection pConn = new PendingConnection(cloneName, message);
            pendingUpdateQueue.add(pConn);
        }
    }

    private UpdateConnection getUpdateConnection(String cloneName) {
        for (int i = 0; i < updateQueue.size(); i++) {
            if (updateQueue.get(i).getcloneName().equals(cloneName)) {
                return updateQueue.get(i);
            }
        }
        return null;
    }

    private PendingConnection getPendingUpdateConnection(String cloneName) {
        for (int i = 0; i < pendingUpdateQueue.size(); i++) {
            if (pendingUpdateQueue.get(i).getcloneName().equals(cloneName)) {
                return pendingUpdateQueue.get(i);
            }
        }
        return null;
    }

    private void updateAppContext(HttpServletRequest request) {
        ServletContext context = request.getServletContext();
        if (updateQueue == null) {
            if (context.getAttribute("updateQueue") != null) {
                updateQueue = (ArrayList<UpdateConnection>) context.getAttribute("updateQueue");
            }
        }

        if (pendingUpdateQueue == null) {
            if (context.getAttribute("pendingUpdateQueue") != null) {
                pendingUpdateQueue = (ArrayList<PendingConnection>) context.getAttribute("pendingUpdateQueue");
            }
        }

        if (context.getAttribute("chatRoom") == null) {
            context.setAttribute("chatRoom", room);
        }
    }

    private ChatRoom getChatRoom(String id) {
        for (int i = 0; i < room.size(); i++) {
            if (room.get(i).getId().equals(id)) {
                return room.get(i);
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
