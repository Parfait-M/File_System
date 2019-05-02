/*
* DataFile.java
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* The DataFile class inherits from the BLOCK class and represents
* a user data file. It has an index that serves as a pointer to 
* the next byte to be read from or written to, as well as a
* data variable to hold its data. It is set to only be able to store
* up to its maximum size of DATA_SIZE
*/

package fileSys;
import java.util.ArrayList;
public class DataFile extends BLOCK{
	public static final int DATA_SIZE = 504;
	private int index;
	private String data;
	
	public DataFile() {
		super();
		data = "";
		setIndex(0);
	}
	
	public void add(String data) {
		if(isFull())return;
		this.data += data;
	}
	
	public String getData() {
		return data;
	}

	public void addIndex(int offset) {
		index += offset;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isFull() {
		return data.length() == DATA_SIZE;
	}

	public void delete() {
		data = "";
		setIndex(0);
	}

}
