/*
语法
shadoop shahedindexer hdf hdf_index 砌筑砌筑time：dataset：
	time可不设，用来指定要索引的文件所属的日期

输入文件夹
输出文件夹

*/

AggregateQuadTree
	
	/**
	 * Creates a full spatio-temporal hierarchy for a source folder
	 */
	public static void main(String[] args) {
		directoryIndexer(params)
			intputDir
			destDir

			// Create daily indexes that do not exist
			TimeRange timeRange

			//逐日索引不存在的话，创建之

			final Path dailyIndexDir = new Path(destDir, "daily");

			//设置了日期选项的话，只对指定的日期建索引
			FileStatus[] mathcingDays = timeRange == null?
			    sourceFs.listStatus(inputDir) :
			      sourceFs.listStatus(inputDir, timeRange);
			Vector<Path> sourceFiles 

			//生成待索引的文件的列表
			for (FileStatus matchingDay : mathcingDays) {
				for (FileStatus matchingTile : sourceFs.listStatus(matchingDay.getPath()))
					sourceFiles.add(matchingTile.getPath());

			Collections.shuffle(sourceFiles);

			final String datasetName = params.get("dataset");

			Parallel.forEach(sourceFiles.size(), new RunnableRange<Object>() {
				public Object run(int i1, int i2) {
					for (int i = i1; i < i2; i++) {
					  Path sourceFile = sourceFiles.get(i);
					  //在输出文件夹/daily底下建 每日的索引
					  Path destFilePath = new Path(dailyIndexDir, relativeSourceFile);

					  AggregateQuadTree.build(params, sourceFile, datasetName,
					      tmpFile);
					  synchronized (destFs) {
					    Path destDir = destFilePath.getParent();
					    if (!destFs.exists(destDir))
					      destFs.mkdirs(destDir);
					  }

					  destFs.rename(tmpFile, destFilePath);


			//合并逐日索引为月索引
			Path monthlyIndexDir = new Path(destDir, "monthly");
			final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
			final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy.MM");
			mergeIndexes(destFs, dailyIndexDir, monthlyIndexDir, dayFormat, monthFormat, params);
			LOG.info("Done generating monthly indexes");

			// Merge daily indexes into monthly indexes
			Path yearlyIndexDir = new Path(destDir, "yearly");
			final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			mergeIndexes(destFs, monthlyIndexDir, yearlyIndexDir, monthFormat, yearFormat, params);
			LOG.info("Done generating yearly indexes");

	/*
	Constructs an aggregate quad tree for an input HDF file on a selected dataset identified by its name in the file.

	*/
	public static void build(Configuration conf, Path inFile, String datasetName,Path outFile) 
		FileSystem inFs = inFile.getFileSystem(conf);
		HDFFile hdfFile = new HDFFile(inFs.open(inFile));

		//Finds and returns the first group that matches the given name.
		DDVGroup dataGroup = hdfFile.findGroupByName(datasetName);