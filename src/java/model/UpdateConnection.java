/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author admin
 */
public class UpdateConnection {
    String cloneName;
    AsyncContext connection;

    public UpdateConnection(String cloneName, AsyncContext res) {
        this.cloneName = cloneName;
        this.connection = res;
    }

    public String getcloneName() {
        return cloneName;
    }

    public void setcloneName(String cloneName) {
        this.cloneName = cloneName;
    }

    public AsyncContext getConnection() {
        return connection;
    }

    public void setConnection(AsyncContext res) {
        this.connection = res;
    }
    
}
