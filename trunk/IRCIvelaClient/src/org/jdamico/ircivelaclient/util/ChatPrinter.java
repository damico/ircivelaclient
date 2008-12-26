package org.jdamico.ircivelaclient.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ChatPrinter {
	public static void print(String msg){
		System.out.println(msg);
	}
	
	public static void writeToAFile(String text){
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("outfilename"));
	        out.write(text);
	        out.close();
	    } catch (IOException e) {
	    }

	}
}
