RangeQuery
	main
		Rectangle[] queryRanges = params.getShapes("rect", new Rectangle());
			String[] values = getArray(key);
				String val = get(key);
				return val == null ? null : val.split("\n");
			S[] shapes = (S[]) Array.newInstance(stock.getClass(), values.length);
			for (int i = 0; i < values.length; i++) {
				shapes[i] = (S) stock.clone();
				shapes[i].fromText(new Text(values[i]));
			}
			return shapes;

		for (int i = 0; i < queryRanges.length; i++) {
		  	final OperationsParams queryParams = new OperationsParams(params);
		  	OperationsParams.setShape(queryParams, "rect", queryRanges[i]);
		  	//可以在本地算，
		  	//也可以在集群上算		  	
		  	queryParams.setBoolean("background", true);
		  	Job job = rangeQueryMapReduce(inPath, outPath, queryParams);
		  	jobs.add(job);

		  	//处理每个任务
		  	while (!jobs.isEmpty()) {
		  		Job firstJob = jobs.firstElement();
		  		firstJob.waitForCompletion(false);
		  		jobs.remove(0);

		  	//处理每个本地线程
		  	while (!threads.isEmpty()) {

	rangeQueryMapReduce(Path inFile, Path outFile,OperationsParams params) 
		params.set(SpatialInputFormat3.InputQueryRange, params.get("rect"));
		
		job.setInputFormatClass(SpatialInputFormat3.class);
		SpatialInputFormat3.setInputPaths(job, inFile);
		
		//只要Map，不要Reduce
		job.setNumReduceTasks(0);
		job.setMapperClass(RangeQueryMap.class);

		//调试的话不用输出，不调试的话应该有输出路径
		job.setOutputFormatClass(TextOutputFormat3.class);
		TextOutputFormat3.setOutputPath(job, outFile);

		if (!params.getBoolean("background", false)) {
		    job.waitForCompletion(false);
		} else {
  		    job.submit();


  	class RangeQueryMap extends Mapper<Rectangle, Iterable<Shape>, NullWritable, Shape> 
  		map(final Rectangle cellMBR, Iterable<Shape> value, final Context context)
  			NullWritable dummyKey = NullWritable.get();
  			for (Shape s : value) {
  			    context.write(dummyKey, s);
  			