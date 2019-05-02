/*
* Directory.java
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* This class inherits from the BLOCK class and represents
* directories in this file system. The ROOT of the File System
* is also a Directory. Each directories contain a table of 
* Dir_Entry elements, which hold the entries of their subdirectories
* and files. 
*/

package fileSys;
import java.util.ArrayList;
public class Directory extends BLOCK{
	static final int DIR_TABLE_SIZE = 31;
	private Dir_Entry [] table = new Dir_Entry[DIR_TABLE_SIZE];
	private ArrayList<Integer> FREE = new ArrayList<>();
	
	public Directory() {
		super();
		for(int i = 0; i < DIR_TABLE_SIZE; ++i) {
			table[i] = new Dir_Entry();
		}
	}
	
	// Method to add a BLOCK from the Disk to the Root's free list
	// Program does not allow the user to add the Root directory,
	// i.e. DELETE ROOT
	public void addFree(int obj) {
		if(obj != 0 && !FREE.contains(obj))
			FREE.add(obj);
	}
	// Method to retrieve the next available Free memory block.
	public int getFree() {
		if(FREE.size() > 0) {
			int t = FREE.get(0);
			FREE.remove(0);
			return t;
		}
		return -1;
	}
	// Method to check if a directory is in the Free list
	public boolean isFree(int i) {
		if(FREE.contains(i))
			return true;
		return false;
	}
	// Method to return the nnumber of free blocks in the file system
	public int getNumFree() {
		return FREE.size();
	}
	
	public int getLink(String name) {
		int i = 0;
		while(i < DIR_TABLE_SIZE) {
			if(table[i].type != 'F' && table[i].name.equalsIgnoreCase(name))
				return table[i].link;
			++i;
		}
		
		return -1;
	}
	
	public boolean addEntry(char type, String name, int link, int size) {
		int i = 0;
		while(table[i].type != 'F' && i < DIR_TABLE_SIZE) ++i;
		if(i < DIR_TABLE_SIZE) {		
			table[i].setAll(type, name, link, size);
			return true;
		}
		return false;
	}
	
	public int find(String name) {
		int i;
		for(i = 0; i < DIR_TABLE_SIZE; ++i) {		
			if(table[i].type != 'F' && table[i].name.equalsIgnoreCase(name))
				return table[i].link;
		}
		return -1;
	}
	
	public boolean isDir(String name) {
		int i = 0;
		while(i < DIR_TABLE_SIZE) {
			if(table[i].type == 'D' && table[i].name.equals(name))
				return true;
			++i;
		}
		return false;	
	}
	
	public Dir_Entry [] getTable() {
		return this.table;
	}
}
