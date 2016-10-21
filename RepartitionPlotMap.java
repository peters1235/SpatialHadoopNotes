RepartitionPlotMap
	setup(Context context) 
		super.setup(context);
	    Configuration conf = context.getConfiguration();
	    this.partitioner = Partitioner.getPartitioner(conf);

	map(Rectangle key, Iterable<? extends Shape> shapes, final Context context)
		final IntWritable partitionID = new IntWritable();
      int i = 0;
      for (final Shape shape : shapes) {
      	partitioner.overlapPartitions(shape, new ResultCollector<Integer>() {
      	  @Override
      	  public void collect(Integer r) {
      	    partitionID.set(r);
      	    try {
      	      context.write(partitionID, shape);
      	    if (((++i) & 0xff) == 0)
      	      context.progress();