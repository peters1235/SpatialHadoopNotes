Partition extends CellInfo  extends Rectangle extends Object
	public String filename;
	/**Total number of records in this partition*/
	public long recordCount;
	/**Total size of data in this partition in bytes (uncompressed)*/
  	public long size;


    public void write(DataOutput out) throws IOException {
	    super.write(out);
	    out.writeUTF(filename);
	    out.writeLong(recordCount);
	    out.writeLong(size);
	  


