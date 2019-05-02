/*
* FS_Driver.java
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* Driver for the File System. This class Searches for an input file
* when started. If it finds the input file, it builds the file system
* based on the input file. Once done, it prompts the user to continue
* using the system. All user entries are not saved, and at restart, 
* the file system is rebuilt based on it's default input.
*/

package fileSys;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class FS_Driver {
	private final String script_file = "/script.data";
	Directory root;
	BufferedReader buff;
	FS_Handler handle;
	Scanner kb;

	public void sopl(Object s) {
		System.out.println(s);
	}
	
	public void sop(Object s) {
		System.out.print(s);
	}
	
	public void init() {
		kb = new Scanner(System.in);
		root = new Directory();
		handle = new FS_Handler();
		sopl("Welcome to my file system. To view a list of help commands, type: --help");
		sopl("Type QUIT (any case) in order to exit. Enjoy...\n");
			
		try {
			
			File file = new File("script.data");
			buff = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = buff.readLine()) != null) {
				handle.command(line);
			}
			buff.close();
			
		}
		catch (Exception e) {
			sopl("\nNo input file detected! Starting a brand new console.\n");
			return;
		}
	}
	
	// Method to prompt the user to enter a command. The prompt is subtle, but
	// an effective way to have the user know where they currently are
	public void run() {
		sop(FS_Handler.location+" $  ");
		String cmd = kb.nextLine();
		while(handle.command(cmd) != 0) {
			sop(FS_Handler.location+" $  ");
			cmd = kb.nextLine();
		}
	}
	
	public FS_Driver() {
		init();
		run();				
	}
	
	public static void main(String[] args) {
		new FS_Driver();
	}

}
