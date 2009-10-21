package org.jdamico.ircblackboardserver.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdamico.ircblackboardsever.config.ManageProperties;

public class SaveObjectServlet extends HttpServlet{

	private static final long serialVersionUID = 7408037342206617319L;
	public String PATH = ManageProperties.getInstance().read("coordPath");
    public String FOLDER = "/default/";
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
			response.setContentType("application/octet-stream");

			// read a String-object from applet
			// instead of a String-object, you can transmit any object, which
			// is known to the servlet and to the applet
			InputStream in = request.getInputStream();
			ObjectInputStream inputFromApplet = new ObjectInputStream(in);
			String echo = (String) inputFromApplet.readObject();

                        String[] split = echo.split("@");
                        FOLDER = split[0]+"/";
                        createFolder(FOLDER);
                        //System
                        
                        writeToAFile(split[1]);
                        //System.out.println("-->"+echo);
                        
			// echo it to the applet
			OutputStream outstr = response.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstr);
			oos.writeObject(echo);
			oos.flush();
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void writeToAFile(String text){
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(PATH+FOLDER+"bbxy.coo"));
	        out.write(text);
	        out.close();
	    } catch (IOException e) {
	    }
    }
    
    public void createFolder(String folderName){
        File f = new File(PATH+folderName);
        if(!f.exists()){
            f.mkdir();
        }
    }
}

