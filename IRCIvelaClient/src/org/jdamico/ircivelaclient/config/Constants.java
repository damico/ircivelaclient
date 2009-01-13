package org.jdamico.ircivelaclient.config;

import org.jdamico.ircivelaclient.view.StaticData;

public class Constants {
	
	public static final String APPNAME = "IRCIvelaClient";
	public static final String APPVERSION = "0.2.5-Droideka";
	
	public static final String PARAM_SERVER = "server";
	public static final String PARAM_TEACHER = "teacher";
	public static final String PARAM_CHANNEL = "channel";
	public static final String PARAM_NICK = "nick";
	public static final String PARAM_BGCOLOR = "bgcolor";
	public static final String NICKSCOMBOBOX_FIRST_ELEMENT = "All";
	public static final String MAINCONTENT_CONTENT_TYPE = "html/text";
	public static final String SEND_BUTTON_NAME = "Send";
	public static final String BLANK_STRING = "";
	public static final String TEACHER_IDENTIFIER = "|";	
	public static final String LINE_BREAK = "\n";
	public static final String SERVLET_PATH = "http://200.17.41.212:8080/ivela-web/SaveObjectServlet";
	public static final String REMOTE_FILE_PATH= "http://200.17.41.212/public_content/"+StaticData.classFolder+"/chat.txt";
	public static final String CLASS_FOLDER= "folder";
	
	public static final int CONN_TIMEOUT = 30; /* 30 = 60 seconds*/
	public static final int UDP_PORT = 6668; 
	
}
