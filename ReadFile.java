ReadFile
	main(String[] args)
		Path inFile = new Path(args[0]);
		FileSystem fs = inFile.getFileSystem(conf);
		GlobalIndex<Partition> gindex = SpatialSite.getGlobalIndex(fs, inFile);
		//没有索引的话输出本文件包含的块的数目
		if (gindex == null) {			
			BlockLocation[] locations = cla.getInt("offset", 0) == -1 ?
			    fs.getFileBlockLocations(fs.getFileStatus(inFile), 0, length) :
			      fs.getFileBlockLocations(fs.getFileStatus(inFile), cla.getInt("offset", 0), 1);
			System.out.println(locations.length+" heap blocks");
		} else {
		//否则的话按全局索引输出每个分区的信息
		 	for (Partition p : gindex) {
		 		long partition_length = fs.getFileStatus(new Path(inFile, p.filename)).getLen();
		 		System.out.println(p+" --- "+partition_length);	