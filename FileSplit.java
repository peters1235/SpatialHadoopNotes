/*
	InputSplit represents the data to be processed by an individual Mapper. 
	Typically, it presents a byte-oriented view on the input and is the responsibility of RecordReader of the job to process this and present a record-oriented view.
*/

InputSplit 
	/*返回分片长度，可用它来对分片排序 */
	abstract long getLength() 


	/*Get the size of the split, so that the input splits can be sorted by size.*/
	abstract String[] getLocations() 

	/*
	Gets info about which nodes the input split is stored on and how it is stored at each location.

	list of SplitLocationInfos describing how the split data is stored at each location. 
	A null value indicates that all the locations have the data stored on disk.
	*/
	SplitLocationInfo[] getLocationInfo() throws IOException {
    	return null;


FileSplit extends InputSplit 
	private Path file;
	private long start;
	private long length;
	private String[] hosts;
	private SplitLocationInfo[] hostInfos; //不光记录块的位置，还记录位是否在内存中


SplitLocationInfo
	private boolean inMemory;
	private String location;

