/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.json.simple.JSONObject;


/**
 *
 * @author admin
 */
public class PendingConnection {
    JSONObject message;
    String cloneName;

    public PendingConnection(String cloneName,JSONObject message) {
        this.message = message;
        this.cloneName = cloneName;
    }

    public JSONObject getMessage() {
        return message;
    }

    public void setMessage(JSONObject message) {
        this.message = message;
    }

    public String getcloneName() {
        return cloneName;
    }

    public void setcloneName(String cloneName) {
        this.cloneName = cloneName;
    }
    
}
