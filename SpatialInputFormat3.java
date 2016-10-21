public class SpatialInputFormat3<K extends Rectangle, V extends Shape>
    extends FileInputFormat<K, Iterable<V>>

    /**Query range to apply upon reading the input*/
    public static final String InputQueryRange = "rect";
    
    List<InputSplit> getSplits(JobContext job)
    	List<InputSplit> splits = super.getSplits(job);
    	//如果设置了参数要合并Splits的话，就合并，否则的话直接返回FileInputFormat生成的Splits
    	//Allows multiple splits to be combined to reduce number of mappers
    	if (jobConf.getInt(CombineSplits, 1) >１

    	else
    		return splits;

    RecordReader<K, Iterable<V>> createRecordReader(InputSplit split,
      TaskAttemptContext context)
    	//取文件的扩展名
    	//hdf文件
    		return (RecordReader)new HDFRecordReader();
    	//rtree文件 
    		return (RecordReader)new RTreeRecordReader3<V>();
    	//自定义的Reader
    		 // Check if a custom record reader is configured with this extension
      		Class<?> recordReaderClass = conf.getClass("SpatialInputFormat."
          		+ extension + ".recordreader", SpatialRecordReader3.class);
    		return (RecordReader<K, Iterable<V>>) recordReaderClass.newInstance();

    isSplitable
    	// HDF files are not splittable
    	
    	// To avoid opening the file and checking the first 8-bytes to look for
        // an R-tree signature, we never split a file read over HTTP

    	// ... and never split a file less than 150MB to perform better with many small files

    	return !SpatialSite.isRTree(fs, file);
    		/**
         * Checks whether a file is indexed using an R-tree or not. This allows
         * an operation to use the R-tree to speedup the processing if it exists.
         * This function opens the specified file and reads the first eight bytes
         * which include the R-tree signature. If the signatures matches with the
         * R-tree signature, true is returned. Otherwise, false is returned.
         * If the parameter is a path to a directory, only the first data file in that
         * directory is tested.
        */



FileStatus implements Writable, Comparabl
	//文件或文件夹的元数据
	//Interface that represents the client side information for a file.
	getLen()
		//单位 字节
	isFile()
	isDirectory()
	isSymlink()
	getBlockSize()
		//字节
	/**
    * Get the replication factor of a file.
    * @return the replication factor of a file.
    */
    public short getReplication() {

   	 /**
    * Get the modification time of the file.
    * @return the modification time of file in milliseconds since January 1, 1970 UTC.
    */
    public long getModificationTime() {

  	public long getAccessTime() {

  	FsPermission getPermission() {

  	isEncrypted()

  	String getOwner() 

  	getGroup() 

  	Path getPath()

  	setPath(final Path p)

