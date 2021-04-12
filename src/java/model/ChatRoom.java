/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class ChatRoom {
    String id;
    List<String> member = new ArrayList();
    int maxNumberOfMember=0;

    public ChatRoom() {
    }

    public ChatRoom(String id, int MaxNumberOfMember) {
        this.id = id;
        this.maxNumberOfMember = MaxNumberOfMember;
    }

    public void leaveRoom(String username){
        member.remove(username);
    }
    
    public boolean isFull(){
        if( member.size() >= maxNumberOfMember)
            return true;
        return false;
    }
    
    public void newMember(String username){
        member.add(username);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }

    public int getMaxNumberOfMember() {
        return maxNumberOfMember;
    }

    public void setMaxNumberOfMember(int MaxNumberOfMember) {
        this.maxNumberOfMember = MaxNumberOfMember;
    }
}
