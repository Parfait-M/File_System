/*
* Dir_Entry.java
*
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* The Dir_Entry class serves to hold directory entries
* in the Directory Class. Unfortunately I was unable to 
* create structs as in C++, and just decided to create a 
* standalone class as opposed to an aggregated one
* 
* Key thing to notice is that the link variable is an integer
* and not a pointer to a BLOCK. This integer will hold the
* index of the BLOCK where the file will reside.
*/

package fileSys;

public class Dir_Entry {
	public char type;
	public String name;
	public int link;
	public int size;
	
	public Dir_Entry()
	{
		setAll('F',"",-1,0);
	}
	
	public void setAll(char t, String n, int l, int s) {
		this.type = t;
		this.name = n;
		this.link = l;
		this.size = s;
	}
	
}
