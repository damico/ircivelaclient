package org.jdamico.ircblackboardserver.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdamico.ircblackboardsever.config.ManageProperties;



public class GetFile extends HttpServlet{

	private static final long serialVersionUID = -8310887491544358353L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        
        response.setCharacterEncoding("ISO-8859-1");
        response.setDateHeader("Expires",System.currentTimeMillis(  ) + 24*60*60*1000);

        String filename = ManageProperties.getInstance().read("coordPath")+"/default/bbxy.coo";
        File file = null;
        Boolean err = false;
        String errMsg = "Incorrect file path or incorrect file name. ("+filename+") ";
        
        
            try{
                file = new File(filename);
                if(file.exists()){
                    ServletContext sc = getServletContext();


                    try {
                                BufferedReader in = new BufferedReader(new FileReader(filename));
                                String str;

                                
                                while ((str = in.readLine()) != null) {
                                    response.getWriter().println(str);
                                }
                                in.close();
                            } catch (IOException ioe) {
                                err = true;
                                errMsg = errMsg + ioe.getMessage();
                                ioe.printStackTrace();
                            }

                        
                    
                }else{
                    err = true;
                }
            } catch(NullPointerException npe){
                err = true;
                errMsg = errMsg + npe.getMessage();
                npe.printStackTrace();
            }
       

        if(err) response.getWriter().println(errMsg);
	}

}
