package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

public class Loggable {
	
	public OutputStream logger;
	
	public void setLogger(OutputStream out) {
		logger = out;
		log("Session: " + new Timestamp(System.currentTimeMillis()));
	}
	
	public void setLogger(File f) {
		try {
			setLogger(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			setLogger(System.out);
		}
	}
	
	public void log(String s) {
		if(logger == null)logger = System.out;
		
		s = "<" + new Timestamp(System.currentTimeMillis()) + ">\t" + s + "\n";
		
		try {
			logger.write(s.getBytes());
			logger.flush();
		} catch (IOException e) {
			System.err.println("Could not access logger, switching to command prompt.");
			logger = System.out;
			System.out.println(s);
		}
	}
}
