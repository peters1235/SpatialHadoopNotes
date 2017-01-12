FileSystem
	/*
	Return an array containing hostnames, offset and size of portions of the given file.
	For a nonexistent file or regions, null will be returned.
	This call is most helpful with DFS, where it returns hostnames of machines that contain the given file. 
	The FileSystem will simply return an elt containing 'localhost'.
	*/
	BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len)
		DistributedFileSystem
			//调用
			BlockLocation[] getFileBlockLocations(Path p, final long start, final long len) 

	/**根据 URI的 scheme 和 authority返回对应的文件系统.  The scheme
	 * of the URI determines a configuration property name,
	 * <tt>fs.<i>scheme</i>.class</tt> whose value names the FileSystem class.
	 * The entire URI is passed to the FileSystem instance's initialize method.
	 */
	public static FileSystem get(URI uri, Configuration conf) throws IOException {
	    String scheme = uri.getScheme();
	    String authority = uri.getAuthority();
  
	    if (scheme == null && authority == null) {     // use default FS
	        return get(conf);
	    
  
	    if (scheme != null && authority == null) {     // no authority
	        URI defaultUri = getDefaultUri(conf);
	        if (scheme.equals(defaultUri.getScheme())    // if scheme matches default
	              && defaultUri.getAuthority() != null) {  // & default has authority
	            return get(defaultUri, conf);              // return default
	         
	    
	    
	    String disableCacheName = String.format("fs.%s.impl.disable.cache", scheme);
	    if (conf.getBoolean(disableCacheName, false)) {
	        return createFileSystem(uri, conf); 

	    return CACHE.get(uri, conf);
 


	/*
	FileStatus:文件、文件夹的元数据：文件长度、块大小 、复制份数、修改时间、所有者、权限等	
	本方法取文件或者文件夹的FileStatus对象	
	*/
	abstract FileStatus getFileStatus
		DistributedFileSystem

