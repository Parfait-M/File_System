/*
* FS_Handler.java
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* This is the Model class of the File System, if we follow
* a software engineering Model, Controller design.
* It contains the Disk of sectors on which the file system is mounted
*/

package fileSys;
import java.util.Scanner;
import java.util.Stack;
public class FS_Handler {
	private final static int DISK_SIZE = 500;
	
	// open file modes: I, O, U
	private final static String INPUT = "I";
	private final static String OUTPUT = "O";
	private final static String UPDATE = "U";
	private final static int ROOT = 0;

	private static String curMode;
	private String openFileName;
	static String location;
	private Stack<String> oldLoc = new Stack<>();
	private Stack<Integer> opFiles = new Stack<>();
	private Stack<String> opModes = new Stack<>();
	private int numDir;
	private int numFile;
	
	
	private BLOCK [] Disk = new BLOCK[DISK_SIZE];
	private static int CUR; 
	static Scanner kb;
	public FS_Handler() {
		initialize();
	}
	
	private void initialize() {
		curMode = "";
		openFileName = "";
		location = "ROOT";
		numDir = 1;
		numFile = 0;
		CUR = ROOT;
		kb = new Scanner(System.in);
		Disk[ROOT] = new Directory();	// assign the root dir
		for(int i = 1; i < DISK_SIZE; ++i)
			Disk[i] = new BLOCK();
		for(int i = 1; i < DISK_SIZE; ++i) {
			((Directory) Disk[ROOT]).addFree(i);
		}
	}

	public void sopl(Object s) {
		System.out.println(s);
	}
		
	public void add(String name, char type) {
		delete(name);
			
		int link = ((Directory) Disk[ROOT]).getFree();
		if(link == -1) {
			sopl("Not enough memmory on disk");
			return;
		}
		if(type == 'D')
			Disk[link] = new Directory();
		else
			Disk[link] = new DataFile();
		boolean bool = ((Directory) Disk[CUR]).addEntry(type, name, link, 0);
		int cur = CUR;
		while(!bool) {
			int next = Disk[cur].getFRWD();
			if(next == -1) {
				link = ((Directory) Disk[ROOT]).getFree();
				if(link == -1) {
					sopl("Not enough memmory on disk");
					return;
				}
				Disk[CUR].setFRWD(next);
				Disk[next] = new Directory();
				Disk[next].setBACK(CUR);
				bool = ((Directory) Disk[next]).addEntry(type, name, link, 0);
			}
			else {
				bool = ((Directory) Disk[next]).addEntry(type, name, link, 0);
			}
			cur = next;
		}
		if(type == 'D')
			++numDir;
		else
			++numFile;
	}
	
	public void delete(String name) {
		int cur = CUR;
		do{
			Dir_Entry [] arr = ((Directory) Disk[cur]).getTable();
			for(int i = 0; i < Directory.DIR_TABLE_SIZE; ++i) {
				if(arr[i].type != 'F' && arr[i].name.equals(name)) {
					delete(arr[i].link,arr[i].type);
					((Directory) Disk[ROOT]).addFree(arr[i].link);
					if(arr[i].type == 'D')
						numDir--;						
					arr[i].type = 'F';
					return;
				}
			}
			cur = Disk[cur].getFRWD();
		}while(cur != -1);
	}
	
	public void delete(int index, char type) {
		if(index == -1) return;
		if(type == 'U') {
			((DataFile)Disk[index]).delete();
			delete(Disk[index].FRWD, type);
			Disk[index].setBACK(-1);
			Disk[index].setFRWD(-1);
			numFile--;
		}
		else if(type == 'D') {
			Dir_Entry [] arr = ((Directory) Disk[index]).getTable();
			for(int i = 0; i < Directory.DIR_TABLE_SIZE; ++i) {
				delete(arr[i].link, arr[i].type);
				if(arr[i].type == 'D')
					numDir--;
				arr[i].type = 'F';
			}
			delete(Disk[index].getFRWD(),type);
			Disk[index].setBACK(-1);
			Disk[index].setFRWD(-1);
		}
		((Directory) Disk[ROOT]).addFree(index);
	}
	
	public boolean isValidName(String name) {
		if(name.contains("/")) {
			String [] arr = name.split("/");
			for(String s : arr)
				if(s.length() > 9)
					return false;
			return true;
		}
		return name.length() <= 9;
	}
	
	public int command(String cmd) {
		String [] arr = cmd.split(" ");
		if(arr.length == 1) {
			if(arr[0].equalsIgnoreCase("--help"))
				helpCommand();
			else if(arr[0].equalsIgnoreCase("QUIT")) {
				sopl("\nClosing File System. Printing Current System:\n");
				printHandler();
				return 0;
			}
			else if(arr[0].equalsIgnoreCase("print")) {
				printHandler();
			}
			else if(arr[0].equals("CLOSE")) {
				closeCommand();
			}
		}
		else {
			switch(arr[0]) {
			case "CREATE" : 
				if(arr.length == 3)
					createCommand(arr[1], arr[2]);
				else
					defaultAction(cmd);
				break;
			case "OPEN" : 
				if(arr.length == 3)
					openCommand(arr[2],arr[1]);
				else
					defaultAction(cmd);
				break;
			case "DELETE" : 
				if(arr.length == 2)
					delete(arr[1]);
				else
					defaultAction(cmd);
				break;
			case "READ":
				if(arr.length == 2) {
					readCommand(Integer.parseInt(arr[1]));
				}
				break;
			case "WRITE":
					writeCommand(Integer.parseInt(arr[1]), cmd);
				break;
			case "SEEK":
				seekCommand(arr[1],Integer.parseInt(arr[2]));
				break;
			default: defaultAction(cmd);
			}
		}
		return 1;
	}

	public void helpCommand() {
		sopl("\n\nList of commands: ");
		sopl("CREATE type name");
		sopl("OPEN name");
		sopl("CLOSE");
		sopl("DELETE name");
		sopl("READ n");
		sopl("WRITE n 'data'");
		sopl("SEEK base offset");
	}
	
	public void createCommand(String type, String name) {
		if(!isValidName(name)) {
			defaultAction(name);
			return;
		}
		if(type.equals("U")) {
			if(name.contains("/")) {
				String [] arr = name.split("/");
				for(int i = 0; i < arr.length - 1; ++i) {
					createCommand("D",arr[i]);
				}
				createCommand("U",arr[arr.length-1]);
			}
			else {
				add(name,'U');
				openCommand(name, OUTPUT);
			}
		}
		else if(type.equals("D")) {
			add(name,'D');
			openCommand(name,OUTPUT);
		}
		
	}
	
	public void openCommand(String name,String mode) {
		int cur = CUR, index = -1;
		if(!isValidName(name)) {
			defaultAction(name);
			return;
		}
		do {
			index = ((Directory) Disk[cur]).find(name);
			if(index == -1)
				cur = Disk[cur].getFRWD();
			else {
				if(!mode.equals(INPUT) && !mode.equals(OUTPUT) 
						&& !mode.equals(UPDATE)) {
					break;
				}
				if(!((Directory) Disk[CUR]).isDir(name)) {
					if(mode.equals(OUTPUT))
						setOutputMode();
					else
						setInputMode();
				}
				opFiles.push(CUR);
				opModes.push(curMode);
				oldLoc.push("/"+name);
				openFileName = name;
				location += oldLoc.peek();
				curMode = mode;
				CUR = index;
				return;
			}
		}while(cur != -1);
		sopl("Could not open: "+name);
	}
	
	public void closeCommand() {
		if(opFiles.isEmpty())return;
		CUR = opFiles.pop();
		curMode = opModes.pop();
		location = location.replace(oldLoc.pop(), "");
		if(!((Directory) Disk[CUR]).isDir(openFileName)) {
			setSize(openFileName, getSize(((Directory) Disk[CUR])
					.getLink(openFileName)));
		}
	}

	public void readCommand(int bytes) {
		int cur = CUR;
		String str = "";
		if(curMode == OUTPUT) {
			sopl("Cannot read in output mode");
			return;
		}
		do
		{
			String data = ((DataFile) Disk[cur]).getData();
			int i, index = ((DataFile) Disk[cur]).getIndex();
			for(i = 0; i < bytes; ++i) {
				if(i + index < DataFile.DATA_SIZE && i+index < data.length() )
				{
					str += data.charAt(i+index);
				}
				else
					break;
			}
			((DataFile) Disk[cur]).setIndex(index + i);
			bytes -= i;
			cur = Disk[cur].getFRWD();
		}while(cur != -1 && bytes > 0);
		sopl(str);
		if(bytes > 0)
			sopl("End of file reached before everything could be read");
	}

	public void writeCommand(int n, String data) {
		int cur = CUR;
		if(curMode == INPUT) {
			sopl("Cannot write in input mode");
			return;
		}
		
		data = data.replace("WRITE ","");
		data = (data.split(" ", 2)[1]).replace("'","");
		
		do 
		{
			int i = 0;
			while(!((DataFile) Disk[cur]).isFull() && i < n) {
				if(i >= data.length()) {
					((DataFile) Disk[cur]).add(" ");
				}
				else
					((DataFile) Disk[cur]).add(""+data.charAt(i));
				++i;
			}
			if (i < n) {
				cur = Disk[cur].getFRWD();
				if(cur == -1) {
					int lnk = ((Directory) Disk[ROOT]).getFree();
					if(lnk == -1) {
						sopl("Disk is full");
						return;
					}
					Disk[cur].setFRWD(lnk);
					Disk[lnk] = new DataFile();
					Disk[lnk].setBACK(cur);
					cur = lnk;
				}
			}
			else
				return;
		}while(cur != -1);
	}

	public void seekCommand(String base, int offset) {
		if(curMode == OUTPUT) return;
		if(base.equals("-1")) {
			setInputMode();
		}
		else if(base.equals("+1")) {
			setOutputMode();
		}
		((DataFile) Disk[CUR]).addIndex(offset);
	}

	private void setOutputMode() {
		int cur = CUR;
		do {
			if(Disk[cur].getFRWD() != -1) {
				((DataFile) Disk[cur]).setIndex(DataFile.DATA_SIZE);
			}
			else {
				break;
			}
			cur = Disk[cur].getFRWD();
		}while(cur != -1);
		try {
			((DataFile) Disk[cur]).setIndex(((DataFile) Disk[cur])
					.getData().length());
		} catch (ClassCastException e) {

		}
	}
	
	private void setInputMode() {
		int cur = CUR;
		do {
			if(Disk[cur].getBACK() != -1) {
				((DataFile) Disk[cur]).setIndex(0);
			}
			else
				break;
			cur = Disk[cur].getBACK();
		}while(cur != -1);
		try {
			((DataFile) Disk[cur]).setIndex(0);
		} catch (Exception e) {
			
		}
	}
	
	private void setSize(String name, int size) {
		int cur = CUR;
		do {
			Dir_Entry [] arr = ((Directory) Disk[CUR]).getTable();
			for(int i = 0; i < Directory.DIR_TABLE_SIZE; ++i) {
				if(arr[i].type == 'U' && arr[i].name.equals(name)) {
					arr[i].size = size;
					return;
				}
			}
			cur = Disk[cur].getFRWD();
		}while(cur != -1);
	}

	private int getSize(int link) {
		try {
			while(Disk[link].getFRWD() != -1) {
				link = Disk[link].getFRWD();
			}
			return ((DataFile) Disk[link]).getData().length();
		} catch (Exception e) {
		
		}
		return 0;
	}

	private void printHandler() {
		sopl("\n\nROOT");
		print(ROOT, "  ");
		sopl("\nNumber of Free Blocks: "+
		((Directory) Disk[ROOT]).getNumFree());
		sopl("\nNumber of Directories: "+numDir);
		sopl("\nNumber of User Data Blocks: "+numFile);
		sopl("\n");
	}

	public void defaultAction(String cmd) {
		sopl("'"+cmd+"' is not recognized as internal command. \n"
				+ "Please type --help for a list of commands");
	}
	
	public void print(int index, String s) {
		if(((Directory) Disk[ROOT]).isFree(index)) return;
		do {
			Dir_Entry [] table = ((Directory) Disk[index]).getTable();
			for(int i = 0; i < Directory.DIR_TABLE_SIZE; ++i) {
				if(table[i].type != 'F'){
					String size = table[i].type == 'U' ? ""+table[i].size : "";
					sopl(s+table[i].type+" "+table[i].name+" "+size);
					if(table[i].type == 'D')
						print(table[i].link, s+"  ");
				}
			}
			
			index = Disk[index].getFRWD();
		}while(index != -1);
		
	}
}