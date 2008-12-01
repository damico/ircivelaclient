package org.jdamico.ircivelaclient.util;

import java.awt.Color;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdamico.ircivelaclient.config.Constants;
import org.jdamico.ircivelaclient.view.StaticData;

public class IRCIvelaClientStringUtils {
	private static final IRCIvelaClientStringUtils getInstance = new IRCIvelaClientStringUtils();

	public static IRCIvelaClientStringUtils singleton() {
		return getInstance;
	}

	public String showVersion() {
		String formatedVersion = null;
		formatedVersion = "<font color='green'>" + Constants.APPNAME + " "
				+ Constants.APPVERSION + "</font><br>";
		return formatedVersion;
	}

	public String setMessage(String unformatedMessage, int row) {
		String formatedMessage = null;
		String fontColor = "'blue'";
		String bgColor = "'#FFFFFF'";

		if (unformatedMessage.contains("|" + StaticData.teacher)) {
			fontColor = "'red'";
		} else {
			fontColor = "'blue'";
		}

		if (row % 2 == 0)
			bgColor = "#EFEFEF";
		else
			bgColor = "#FFFFFF";

		String msgTB_a = "<table width=\"100%\" bgcolor='"
				+ bgColor
				+ "' border ='0' cellpadding='4'><tr valign='top'><td width='90'>";
		String msgTB_b = "</td><td>";
		String msgTB_c = "</td></tr></table>";

		formatedMessage = msgTB_a + getTime() + msgTB_b + "<font color="
				+ fontColor + ">" + unformatedMessage.replaceAll("|", "")
				+ "</font>" + msgTB_c;

		return formatedMessage;
	}
	
	public String setMyMessage(String unformatedMessage){
		String formatedMessage = null;
		formatedMessage = "<font color'#666666'>Me: "+ unformatedMessage+ "</font>";
		return formatedMessage;		
	}

	public  String getTime() {
		Format formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("HH:mm:ss");
		String stime = formatter.format(date);
		return "<font color'#efefef'>[" + stime + "]&nbsp</font>";
	}
	
	public Color getColorParameter(String strColor) {


	    	int hexConverted = 0;
			hexConverted = Integer.parseInt(strColor, 16);
			Color returnColor = Color.decode(String.valueOf(hexConverted));

	    return returnColor;
	}


}
