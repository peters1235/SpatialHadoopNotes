Sampler
	sample(ins, resultCollector, params2);
		if (params.get("ratio") != null) {
			if (params.getBoolean("local", false))
				sampleLocalWithRatio(inputFiles, output, params);
			else{
				System.out.println("sampleMapReduceWithRatio");
				sampleMapReduceWithRatio(inputFiles, output, params);

			}
		} else if (params.get("size") != null) {
			sampleLocalWithSize(inputFiles, output, params);
		} else if (params.get("count") != null) {
			// The only way to sample by count is using the local sampler
			sampleLocalByCount(inputFiles, output, params);
		} else {
			throw new RuntimeException("Must provide one of three options 'size', 'ratio' or 'count'");
		}

	int sampleMapReduceWithRatio(Path[] files,final ResultCollector<T> output, OperationsParams params)
		outputPath //应该是采样输出路径，通过在后面加随机来实现唯一test.sample_99712

		JobConf job = new JobConf(params, Sampler.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormat(ShapeLineInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);

		ShapeLineInputFormat.setInputPaths(job, files);
		TextOutputFormat.setOutputPath(job, outputPath);

		//执行任务，并从Counter中获取采样结果：
		//采样生成的记录数，数据量大小，处理的数据量，等等
		// Submit the job
		RunningJob run_job = JobClient.runJob(job);

		Counters counters = run_job.getCounters();
		Counter outputRecordCounter = counters.findCounter(Task.Counter.MAP_OUTPUT_RECORDS);
		final long resultCount = outputRecordCounter.getValue();

		Counter outputSizeConter = counters.findCounter(Task.Counter.MAP_OUTPUT_BYTES);
		final long sampleSize = outputSizeConter.getValue();

		LOG.info("resultSize: " + sampleSize);
		LOG.info("resultCount: " + resultCount);

		Counter inputBytesCounter = counters.findCounter(Task.Counter.MAP_INPUT_BYTES);
		Sampler.sizeOfLastProcessedFile = inputBytesCounter.getValue();

		long desiredSampleSize = job.getLong("size", 0);//默认100M
		//selectRatio 的值为2 或者 是 想要的采样量 比 实际生成的采样量
		//如果值大于1 则说明采样生成的数据量 足够小，能满足要求，
		//直接用这个采样结果就行，如果小于1，则说明本次采样的结果集仍然过大，
		//需要继续采样
		float selectRatio = desiredSampleSize <= 0 ? 2.0f : (float) desiredSampleSize / sampleSize;


	public static class Map extends MapReduceBase implements Mapper<Rectangle, Text, IntWritable, Text>
		/** The key assigned to all output records to reduce shuffle overhead */
		private IntWritable key = new IntWritable((int) (Math.random() * Integer.MAX_VALUE));

		/** Ratio of lines to sample */
		private double sampleRatio;

		public void configure(JobConf job) {
			sampleRatio = job.getFloat("ratio", 0.01f);

			TextSerializable inObj = OperationsParams.getTextSerializable(job, "shape", new Text2());
			TextSerializable outObj = OperationsParams.getTextSerializable(job, "outshape", new Text2());

			//输入的Geometry类型必须实现 Shape 接口
			//输出的Geometry类型好像已经写死成Point了
			//输入类型不对的话会报异常
