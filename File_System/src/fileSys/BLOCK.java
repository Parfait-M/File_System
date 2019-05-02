/*
* BLOCK.java
* Parfait Mwamba
* Operating Systems - Spring 2018
* Unix Variant File System
* 
* BLOCK Class serves as the Base class for the file system.
* It has two children, Directory and DataFile.
* The BLOCK class contains a BACK and FRWD variable to hold
* an index to a previous and forward BLOCK, as well as
* accessor and mutator functions 
*/

package fileSys;

public class BLOCK {


	protected int BACK;
	protected int FRWD;
	
	public BLOCK() {
		// -1 is the default null value chosen for indecies
		BACK = FRWD = -1;
	}
	
	public int getBACK() {
		return BACK;
	}

	public void setBACK(int bACK) {
		BACK = bACK;
	}

	public int getFRWD() {
		return FRWD;
	}

	public void setFRWD(int fRWD) {
		FRWD = fRWD;
	}
}
