package edu.cmu.mmdm.phase3.util;

import java.io.*;

/**
 * Description: Print to file 
 */
public class Printer {
	private FileOutputStream fos = null;
	private PrintStream ps = null;

	/** initialize a file **/
	public Printer(String file){
		try{
			this.fos = new FileOutputStream(file);
			this.ps = new PrintStream(this.fos);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** print a string to a file **/
	public void print(String str){
		this.ps.println(str);
	}
}
