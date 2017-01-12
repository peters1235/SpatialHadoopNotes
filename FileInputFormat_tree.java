FileInputFormat
	
	FileSplit makeSplit(Path file, long start, long length, 
	                               String[] hosts, String[] inMemoryHosts) {
	    return new FileSplit(file, start, length, hosts, inMemoryHosts);

	//BloclLocation 存储了块开始的位置对应从整个文件开头处开始的偏移
	    //给定一个偏移量，可以由此推算出这个位置应该在第几个块里头
	int getBlockIndex(BlockLocation[] blkLocations, long offset) {
		// is the offset inside this block?
		if ((blkLocations[i].getOffset() <= offset) &&
		    (offset < blkLocations[i].getOffset() + blkLocations[i].getLength())){
		      return i;
		  }
		}

	List<InputSplit> getSplits(JobContext job) 
		long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));

    	long maxSize = getMaxSplitSize(job);

		List<InputSplit> splits = new ArrayList<InputSplit>();
		List<FileStatus> files = listStatus(job:JobContext);
			//Get the list of input {@link Path}s for the map-reduce job.
			Path[] dirs = getInputPaths(job);
				String dirs = context.getConfiguration().get(INPUT_DIR, "");
			    String [] list = StringUtils.split(dirs);
			    Path[] result = new Path[list.length];
			    for (int i = 0; i < list.length; i++) {
			      result[i] = new Path(StringUtils.unEscapeString(list[i]));
			    }
			    return result;
			//把hiddenFileFilter和用户定义的过滤器合起来
			PathFilter inputFilter = new MultiPathFilter(filters);
			//单线程的话
				result = singleThreadedListStatus(job, dirs, inputFilter, recursive);
					for (int i=0; i < dirs.length; ++i) {
			//多线程又不一样

			return result;
		for (FileStatus file: files) {
			Path path = file.getPath();
      		long length = file.getLen();
      		if (length != 0) {
      			//文件的块的位置
      			BlockLocation[] blkLocations;
      			//如果文件本身记了块位置的话，从文件取块位置
      			//没有的话从FS中去查
      			if (file instanceof LocatedFileStatus) {
			          blkLocations = ((LocatedFileStatus) file).getBlockLocations();
			        } else {
			          FileSystem fs = path.getFileSystem(job.getConfiguration());
			          blkLocations = fs.getFileBlockLocations(file, 0, length);
			    //******要分Split了
			    if (isSplitable(job, path)) {
			    	long blockSize = file.getBlockSize();
			    	long splitSize = computeSplitSize(blockSize, minSize, maxSize);
			    	long bytesRemaining = length;

			    	while (((double) bytesRemaining)/splitSize > SPLIT_SLOP) {
			            int blkIndex = getBlockIndex(blkLocations, offset：length-bytesRemaining);
			            	for (int i = 0 ; i < blkLocations.length; i++) {
						      // is the offset inside this block?
						      if ((blkLocations[i].getOffset() <= offset) &&
						          (offset < blkLocations[i].getOffset() + blkLocations[i].getLength())){
						        return i;
						      }
						    }

			            splits.add(makeSplit(path, length-bytesRemaining, splitSize,
			                        blkLocations[blkIndex].getHosts(),
			                        blkLocations[blkIndex].getCachedHosts()));
			            bytesRemaining -= splitSize;

			        //剩下的再放一个Split
			        if (bytesRemaining != 0) {
            			int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
            			splits.add(makeSplit(path, length-bytesRemaining, bytesRemaining,
            				blkLocations[blkIndex].getHosts(),blkLocations[blkIndex].getCachedHosts()));

			    else
			    	splits.add(makeSplit(path, 0, length, blkLocations[0].getHosts(),blkLocations[0].getCachedHosts()));
			    		BlockLocation.getHosts()
			    			//Get the list of hosts (hostname) hosting this block
			    		BlockLocation.getCachedHosts
			    			//Get the list of hosts (hostname) hosting a cached replica of the block
			    		return new FileSplit(file, start, length, hosts, inMemoryHosts);
			    			 private Path file;
							 private long start;
							 private long length;
							 private String[] hosts;

							 SplitLocationInfo //记录块所在的Host的信息，是否缓存之类的


      		else
      			//长为0的文件也记录



/**
   * Proxy PathFilter that accepts a path only if all filters given in the
   * constructor do. Used by the listPaths() to apply the built-in
   * hiddenFileFilter together with a user provided one (if any).
   */
  private static class MultiPathFilter implements PathFilter {



/*
* List input directories.
* Subclasses may override to, e.g., select only files matching a regular
* expression. 
*/
protected List<FileStatus> listStatus(JobContext job