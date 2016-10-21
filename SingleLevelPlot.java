SingleLevelPlot
	plot(inFiles, outFile, GeometricRasterizer.class, params)
	plot(Path[] inFiles, Path outFile,final Class<? extends Plotter> plotterClass,final OperationsParams params)
		if (OperationsParams.isLocal(params, inFiles)) {
	      	plotLocal(inFiles, outFile, plotterClass, params);
	      	return null;
    	} else {
      		return plotMapReduce(inFiles, outFile, plotterClass, params);

    public static void plotLocal(Path[] inFiles, Path outFile,final Class<? extends Plotter> plotterClass,final OperationsParams params) 
	      		// Retrieve desired output image size and keep aspect ratio if needed
	      		
	      		// Adjust width and height to maintain aspect ratio and store the adjusted
      			// values back in params in case the caller needs to retrieve them

	      		// Store width and height in final variables to make them accessible in parallel

	      		// Start reading input file
	      		List<InputSplit> splits = new ArrayList<InputSplit>();

	      		for (Path inFile : inFiles) {
	      			if (!OperationsParams.isWildcard(inFile) && inFs.exists(inFile) && !inFs.isDirectory(inFile)) {
	      			//如果inFile是单个的文件
	      				//如果inFile不是隐藏文件
	      					// Use the normal input format splitter to add this non-hidden file
					        Job job = Job.getInstance(params);
					        SpatialInputFormat3.addInputPath(job, inFile);
					        splits.addAll(inputFormat.getSplits(job));
					    //如果是隐藏文件
					        else {
					          // A hidden file, add it immediately as one split
					          // This is useful if the input is a hidden file which is automatically
					          // skipped by FileInputFormat. We need to plot a hidden file for the case
					          // of plotting partition boundaries of a spatial index
					          splits.add(new FileSplit(inFile, 0,inFs.getFileStatus(inFile).getLen(), new String[0]));
					          	/* 四个参数的含义， Split说明数据所在的机器、文件名、分片的起始位置、长度，不包含具体数据
					          	file the file name
								start the position of the first byte in the file to process
								length the number of bytes in the file to process
								hosts the list of hosts containing the block, possibly null
								*/
					else
						// Use the normal input format splitter to add this non-hidden file
				        Job job = Job.getInstance(params);
				        SpatialInputFormat3.addInputPath(job, inFile);
				        splits.addAll(inputFormat.getSplits(job));

				    // Copy splits to a final array to be used in parallel
    			
    			final FileSplit[] fsplits = splits.toArray(new FileSplit[splits.size()]);

    			//并行数
    			int parallelism = params.getInt("parallel",Runtime.getRuntime().availableProcessors());

    			List<Canvas> partialCanvases = Parallel.forEach(fsplits.length, new RunnableRange<Canvas>() {
    				@Override
			      	public Canvas run(int i1, int i2) {
			      	  Plotter plotter;
			      	  try {
			      	    plotter = plotterClass.newInstance();

			      	  Canvas partialCanvas = plotter.createCanvas(fwidth, fheight, inputMBR);
			      	  	/*
			      	  	Creates an empty canvas of the given width and height.
			      	  	Parameters:
						width - Width of the created layer in pixels
						height - Height of the created layer in pixels
						mbr - The minimal bounding rectangle of the layer in the input
						*/
					  for (int i = i1; i < i2; i++) {
					  	  RecordReader<Rectangle, Iterable<Shape>> reader =inputFormat.createRecordReader(fsplits[i], null);
					  	  reader.initialize(fsplits[i], params)
  
					  	  while (reader.nextKeyValue()) {
			                  Rectangle partition = reader.getCurrentKey();
			                  if (!partition.isValid())
			                      partition.set(inputMBR);
  
			                  Iterable<Shape> shapes = reader.getCurrentValue();
  
			                  // Run the plot step
				              plotter.plot(partialCanvas,				            	
				                  plotter.isSmooth() ? plotter.smooth(shapes) : shapes);
  
				          reader.close();

				      return partialCanvas;  
				    },
				    , parallelism);

    			boolean merge = params.getBoolean("merge", true);
    			// Whether we should vertically flip the final image or not
    			boolean vflip = params.getBoolean("vflip", true);
    			if (merge) {
    				// Create the final canvas that will contain the final image
			      	Canvas finalCanvas = plotter.createCanvas(fwidth, fheight, inputMBR);
			      	for (Canvas partialCanvas : partialCanvases)
			      	  plotter.merge(finalCanvas, partialCanvas);

			      	// Finally, write the resulting image to the given output path
				    LOG.info("Writing final image");
				    FileSystem outFs = outFile.getFileSystem(params);
				    FSDataOutputStream outputFile = outFs.create(outFile);
				    
				    plotter.writeImage(finalCanvas, outputFile, vflip);
				    outputFile.close();
				else {
	  		        // No merge
			        LOG.info("Writing partial images");
			        FileSystem outFs = outFile.getFileSystem(params);
			        for (int i = 0; i < partialCanvases.size(); i++) {
				        Path filename = new Path(outFile, String.format("part-%05d.png", i));
				        FSDataOutputStream outputFile = outFs.create(filename);
			        
				        plotter.writeImage(partialCanvases.get(i), outputFile, vflip);
				        outputFile.close();

    	  	return null;

 	/**
   * Generates a single level using a MapReduce job and returns the created job.
   * @param inFiles
   * @param outFile
   * @param plotterClass
   * @param params
   */
 	Job plotMapReduce(Path[] inFiles, Path outFile,Class<? extends Plotter> plotterClass, OperationsParams params)
 		Plotter plotter = plotterClass.newInstance();
 		Job job = new Job(params, "SingleLevelPlot");
	    job.setJarByClass(SingleLevelPlot.class);
	    job.setJobName("SingleLevelPlot");
	    // Set plotter
	    Configuration conf = job.getConfiguration();
	    Plotter.setPlotter(conf, plotterClass);
	    	conf.setClass(PlotterClass, plotterClass, Plotter.class);
	    Rectangle inputMBR = (Rectangle) params.getShape("mbr");
    	Rectangle drawRect = (Rectangle) params.getShape("rect");
    	OperationsParams.setShape(conf, InputMBR, inputMBR);
    	OperationsParams.setShape(conf, SpatialInputFormat3.InputQueryRange, drawRect);

    	// Adjust width and height if aspect ratio is to be kept.
    	boolean merge = conf.getBoolean("merge", true);
    	if (conf.getBoolean("output", true)) {
	      if (merge) {
	        job.setOutputFormatClass(CanvasOutputFormat.class);
	        conf.setClass("mapred.output.committer.class",
	            CanvasOutputFormat.ImageWriterOld.class,
	            org.apache.hadoop.mapred.OutputCommitter.class);
	      } else {
	        job.setOutputFormatClass(ImageOutputFormat.class);
	      }
	      CanvasOutputFormat.setOutputPath(job, outFile);
	    } else {
	      job.setOutputFormatClass(NullOutputFormat.class);
	    

	    // Set mapper and reducer based on the partitioning scheme
    	String partition = conf.get("partition", "none");
    	ClusterStatus clusterStatus = new JobClient(new JobConf()).getClusterStatus();

    	


