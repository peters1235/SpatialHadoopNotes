Indexing
	shadoop index test test.grid mbr:0,0,1000000,1000000 sindex:grid shape:rect -overwrite 2> indexlog	

Indexer.java
	PartitionerClasses.put("grid", GridPartitioner.class);
	PartitionerClasses.put("rtree", STRPartitioner.class);
    PartitionerClasses.put("r+tree", STRPartitioner.class);

    //只有R树不复制记录
    PartitionerReplicate.put("grid", true);
    PartitionerReplicate.put("rtree", false);
    PartitionerReplicate.put("r+tree", true);

    LocalIndexes.put("rtree", RTreeLocalIndexer.class);
    LocalIndexes.put("r+tree", RTreeLocalIndexer.class);
    // 格网索引不要？对，不要，格网索引在本地就是直接往堆里放东西，不像R树要在本地再建R树索引
    public static class PartitionerMap：
    	Mapper<Rectangle, Iterable<? extends Shape>, IntWritable, Shape>

    	setup
    		//从Configuration中取对应的Partitioner和是否复制参数
    		//Partitioner的角点、长、宽等参数 也在Configuration中？
    		this.partitioner = Partitioner.getPartitioner

    	map(Rectangle key, Iterable<? extends Shape> shapes, final Context context)
    		//key没用上，输出的是<分区ID，Shape>对
    		IntWritable partitionID
    		for (final Shape shape : shapes) 
    			if(replicate)//复制要素    			 
    				partitioner.overlapPartitions(shape, new ResultCollector<Integer>() {
						@Override
						public void collect(Integer r) {
							partitionID.set(r);
							try {
								context.write(partitionID, shape);
							} catch (IOException e) {
								LOG.warn("Error checking overlapping partitions", e);
							} catch (InterruptedException e) {
								LOG.warn("Error checking overlapping partitions", e);
							}
						}
					});    					
    			else
    				partitionID.set(partitioner.overlapPartition(shape));
    				if (partitionID.get() >= 0)
    					context.write(partitionID, shape);

    public static class PartitionerReduce<S extends Shape>
    	extends Reducer<IntWritable, Shape, IntWritable, Shape> 
    	reduce（IntWritable partitionID，Iterable<Shape> shapes  Context context）
    		for (Shape shape : shapes) 
        		context.write(partitionID, shape);
        		context.progress();
        	 context.write(new IntWritable(-partitionID.get()-1), null); //结束标记

    private static Job indexMapReduce(Path inPath, Path outPath,OperationsParams paramss)
    	  Job job = new Job(paramss, "Indexer");

      	Rectangle inputMBR = (Rectangle) OperationsParams.getShape(conf, "mbr");
      	if (inputMBR == null) 
    		    inputMBR = FileMBR.fileMBR(inPath, new OperationsParams(conf));
      	  	OperationsParams.setShape(conf, "mbr", inputMBR);
      	String index = conf.get("sindex");//grid|str|str+|quadtree|zcurve|kdtree)
    	  setLocalIndexer()
    		    //什么作用？格网索引没有，R树和R+树才有,格网索引不用建local索引，
    		    //直接在本地堆，R树和R+树要建要地索引，

    	  Partitioner partitioner = createPartitioner(inPath, outPath, conf, index);
            return createPartitioner(ins:new Path[] {in},Path out, Configuration job, partitionerName);
		    
        Partitioner.setPartitioner(conf, partitioner);

		    Shape shape = OperationsParams.getShape(conf, "shape");
		    job.setMapperClass(PartitionerMap.class);
		    job.setMapOutputKeyClass(IntWritable.class);
		    job.setMapOutputValueClass(shape.getClass());
		    job.setReducerClass(PartitionerReduce.class);
		    job.setInputFormatClass(SpatialInputFormat3.class);
		    SpatialInputFormat3.setInputPaths(job, inPath);
		    job.setOutputFormatClass(IndexOutputFormat.class);
		    IndexOutputFormat.setOutputPath(job, outPath);
		    // Set number of reduce tasks according to cluster status
	      ClusterStatus clusterStatus = new JobClient(new JobConf()).getClusterStatus();
	      job.setNumReduceTasks(Math.max(1, Math.min(partitioner.getPartitionCount(),
            (clusterStatus.getMaxReduceTasks() * 9) / 10)));
    
	      // Use multithreading in case the job is running locally
  	   	conf.setInt(LocalJobRunner.LOCAL_MAX_MAPS, Runtime.getRuntime().availableProcessors());
    
  	   	// Start the job
		    if (conf.getBoolean("background", false)) {
  		    	// Run in background
  		    	job.submit();
		    } else {
		    	  job.waitForCompletion(conf.getBoolean("verbose", false));
		    }
		    return job;
  
    Partitioner createPartitioner(Path[] ins, Path out, Configuration job, String partitionerName)       
        Class<? extends Partitioner> partitionerClass = PartitionerClasses.get(partitionerName.toLowerCase());
              
        if (PartitionerReplicate.containsKey(partitionerName.toLowerCase())) 
            boolean replicate = PartitionerReplicate.get(partitionerName.toLowerCase());
            job.setBoolean("replicate", replicate);
            
        Partitioner partitioner = partitionerClass.newInstance();
        Rectangle inputMBR = (Rectangle) OperationsParams.getShape(job, "mbr");
        inSize  //输入文件总大小
        for (Path in : ins) {
            inSize += FileUtil.getPathSize(in.getFileSystem(job), in);

        /*INDEXING_OVERHEAD： an overhead ratio which accounts for the overhead of replicating records and storing local indexes. 
        */
        long estimatedOutSize = (long) (inSize * (1.0 + job.getFloat(SpatialSite.INDEXING_OVERHEAD, 0.1f)));
        FileSystem outFS = out.getFileSystem(job);
        long outBlockSize = outFS.getDefaultBlockSize(out); 
        float sample_ratio = job.getFloat(SpatialSite.SAMPLE_RATIO, 0.01f);
        // SpatialSite  Combines all the configuration needed for SpatialHadoop 其中有各个参数的名字
            
        final List<Point> sample = new ArrayList<Point>();
        ResultCollector<Point> resultCollector = new ResultCollector<Point>() {
            @Override
            public void collect(Point p) {
              sample.add(p.clone());
           

        OperationsParams params2 = new OperationsParams(job);//Configuration 的另一层封装 OperationsParams extends Configuration
        params2.setFloat("ratio", sample_ratio);
        params2.setLong("size", sample_size);
        params2.set("shape", job.get("shape")); //rect
        params2.set("local", job.get("local"));
        params2.setClass("outshape", Point.class, Shape.class);.

        //**********采样************
        //对多边形有取点操作
        //实际是将采样完的结果存放在上面定义的sample中
        Sampler.sample(ins, resultCollector, params2);
          
        /*maximum number of points per partition
         实际是sample中样本数 除以分区数
        */
        int partitionCapacity = (int) Math.max(1, Math.floor((double)sample.size() * outBlockSize / estimatedOutSize));

        //分区数目
        int numPartitions = Math.max(1, (int) Math.ceil((float)estimatedOutSize / outBlockSize));
        partitioner.createFromPoints(inMBR, sample.toArray(new Point[sample.size()]), partitionCapacity);
            /*
              输入数据的MBR
              待分区的点
              每个分区的最多包含的点数
            */
            

        eturn partitioner;

  	main
  		OperationsParams params = new OperationsParams(new GenericOptionsParser(args));  
  			this(parser, true);
  				super(parser.getConfiguration());

  		index(inputPath, outputPath, params);
  			if (OperationsParams.isLocal(new JobConf(params), inPath)) {

  				indexLocal(inPath, outPath, params);
      			return null;
		    } else {
		      return indexMapReduce(inPath, outPath, params);

edu.umn.cs.spatialHadoop.core.Shape  //接口
	getMBR()
	distanceTo()
	isIntersected(Shape)
	clone()
	draw()

edu.umn.cs.spatialHadoop.core.Rectangle implements Shape, WritableComparable<Rectangle>
/*For predicate test functions
 * (e.g. intersection), the rectangle is considered open-ended. This means that
 * the right and top edge are outside the rectangle.
 */


