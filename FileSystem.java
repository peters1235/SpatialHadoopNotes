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


	/*
	FileStatus:文件、文件夹的元数据：文件长度、块大小 、复制份数、修改时间、所有者、权限等	
	本方法取文件或者文件夹的FileStatus对象	
	*/
	abstract FileStatus getFileStatus
		DistributedFileSystem

