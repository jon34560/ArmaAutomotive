/* Copyright (C) 2019 by Jon Taylor

This program is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import java.util.*;
//import armadesignstudio.math.*;
//import armadesignstudio.object.*;
//import armadesignstudio.view.CanvasDrawer;
import java.io.*;
import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import buoy.widget.*;
import armadesignstudio.ui.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
//import javax.swing.JLabel;
//import java.awt.Font;
import javax.swing.SwingConstants;
//import javax.swing.JTextField;
//import javax.swing.JCheckBox;
//import java.awt.BorderLayout;
//import javax.swing.JDialog;
//import javax.swing.JProgressBar;
//import javax.swing.JFrame;
//import java.awt.Toolkit;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.util.HashMap;
import armadesignstudio.texture.*;
import java.net.*;
import java.net.URL;

public class Analytics extends Thread {
    boolean running = false;
    
    public Analytics(){
    }

    public boolean isRunning(){
        return running;
    }
    
    public void stopCFD(){
        running = false;
    }
    
    public void run(){
        running = true;
        
        while(running){
            // Ping server
            try {
            
                URL url = new URL("http://www.armaautomotive.com/contact.php");
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
                encoding = encoding == null ? "UTF-8" : encoding;
                //String body = IOUtils.toString(in, encoding);
                //
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                String body = new String(baos.toByteArray(), encoding);
                //System.out.println(body);
            
            
            
                Thread.sleep(1000 * 60 * 60);
            } catch(Exception e){
                
            }
        }
    }
    

}
