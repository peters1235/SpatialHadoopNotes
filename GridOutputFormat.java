class GridOutputFormat<S extends Shape> extends FileOutputFormat<IntWritable, S>
	RecordWriter<IntWritable, S> getRecordWriter(FileSystem ignored,
	     JobConf job,
	     String name,
	     Progressable progress)


	     CellInfo[] cellsInfo = SpatialSite.getCells(job);
	     GridRecordWriter<S> writer = new GridRecordWriter<S>(job, name, cellsInfo);
	     return writer;


	GridOutputFormat2 
	 	返回 GridRecordWriter2

    GridOutputFormat3
	 	返回 GridRecordWriter3
