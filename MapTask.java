package org.apache.hadoop.mapred;
public class MapTask extends Task {
	boolean isMapTask() {
	    return true;

	public void localizeConfiguration(JobConf conf)	  
	    super.localizeConfiguration(conf);

	public void write(DataOutput out) throws IOException {
	    super.write(out);
	    if (isMapOrReduce()) {
	        splitMetaInfo.write(out);
	        splitMetaInfo = null;

	//  /**
    This class wraps the user's record reader to update the counters and progress
        * as records are read.    */
	class TrackedRecordReader<K, V> implements RecordReader<K,V> {


	public static String normalizeStatus(String status, Configuration conf) {
	    // Check to see if the status string is too long
	    //  and truncate it if needed.

	public class TaskReporter 
	    extends org.apache.hadoop.mapreduce.StatusReporter
	    implements Runnable, Reporter {

	/**
	   * This class skips the records based on the failed ranges from previous 
	   * attempts.
	   *跳过上次尝试执行任务时失败的记录
	   */
	class SkippingRecordReader<K, V> extends TrackedRecordReader<K,V> {


	public void run(final JobConf job, final TaskUmbilicalProtocol umbilical)
	  
	    this.umbilical = umbilical;
  
	    if (isMapTask()) {
	        // 没有Reduce任务的话，整个Job的进度只取决于map任务
  
	        if (conf.getNumReduceTasks() == 0) {
	            mapPhase = getProgress().addPhase("map", 1.0f);
	        } else {
	            //有reduce任务的话，整个整个进度中map占2/3 排序占 1/3
	            mapPhase = getProgress().addPhase("map", 0.667f);
	            sortPhase  = getProgress().addPhase("sort", 0.333f);
	       
	    TaskReporter reporter = startReporter(umbilical);
	  
	    boolean useNewApi = job.getUseNewMapper();
	    initialize(job, getJobID(), reporter, useNewApi);
  
	    // check if it is a cleanupJobTask
	    if (jobCleanup) {
	        runJobCleanupTask(umbilical, reporter);
	        return;
	    
	    if (jobSetup) {
	      runJobSetupTask(umbilical, reporter);
	      return;
	    }
	    if (taskCleanup) {
	      runTaskCleanupTask(umbilical, reporter);
	      return;
	    }
  
	    if (useNewApi) {
	      runNewMapper(job, splitMetaInfo, umbilical, reporter);
	    } else {
	      runOldMapper(job, splitMetaInfo, umbilical, reporter);
	    }
	    done(umbilical, reporter);
	
	<T> T getSplitDetails(Path file, long offset) 

	<KEY, VALUE> MapOutputCollector<KEY, VALUE>   createSortingCollector(JobConf job, TaskReporter reporter)

	<INKEY,INVALUE,OUTKEY,OUTVALUE>	  void runOldMapper(final JobConf job,
	                    final TaskSplitIndex splitIndex,
	                    final TaskUmbilicalProtocol umbilical,
	                    TaskReporter reporter
	                     
	    InputSplit inputSplit = getSplitDetails(new Path(splitIndex.getSplitLocation()),
	           splitIndex.getStartOffset());

	    updateJobWithSplit(job, inputSplit);
	    reporter.setInputSplit(inputSplit);

	    RecordReader<INKEY,INVALUE> in = isSkipping() ? 
	        new SkippingRecordReader<INKEY,INVALUE>(umbilical, reporter, job) :
	          new TrackedRecordReader<INKEY,INVALUE>(reporter, job);
	    job.setBoolean(JobContext.SKIP_RECORDS, isSkipping());


	    int numReduceTasks = conf.getNumReduceTasks();
	    LOG.info("numReduceTasks: " + numReduceTasks);
	    MapOutputCollector<OUTKEY, OUTVALUE> collector = null;
	    //要输出的map任务要排序，否则的话，直接输出
	    if (numReduceTasks > 0) {
	        collector = createSortingCollector(job, reporter);
	    } else { 
	        collector = new DirectMapOutputCollector<OUTKEY, OUTVALUE>();
	            MapOutputCollector.Context context =
	                           new MapOutputCollector.Context(this, job, reporter);
	      collector.init(context);
	    }
	    MapRunnable<INKEY,INVALUE,OUTKEY,OUTVALUE> runner =  ReflectionUtils.newInstance(job.getMapRunnerClass(), job);	        
	    	org.apache.hadoop.mapred.MapRunner
	    		public void configure(JobConf job) {
	    		  this.mapper = ReflectionUtils.newInstance(job.getMapperClass(), job);

	    try {
	        runner.run(in, new OldOutputCollector(collector, conf), reporter);
	        mapPhase.complete();
	        // start the sort phase only if there are reducers
	        if (numReduceTasks > 0) {
	          setPhase(TaskStatus.Phase.SORT);
	        }
	        statusUpdate(umbilical);
	        collector.flush();
	        
	        in.close();
	        in = null;
	        
	        collector.close();
	        collector = null;
	    } finally {
	        closeQuietly(in);
	        closeQuietly(collector);
	
	void updateJobWithSplit(final JobConf job, InputSplit inputSplit) {
	    if (inputSplit instanceof FileSplit) {
	        FileSplit fileSplit = (FileSplit) inputSplit;
	        job.set(JobContext.MAP_INPUT_FILE, fileSplit.getPath().toString());
	        job.setLong(JobContext.MAP_INPUT_START, fileSplit.getStart());
	        job.setLong(JobContext.MAP_INPUT_PATH, fileSplit.getLength());
	    
	    LOG.info("Processing split: " + inputSplit);

	void runNewMapper(final JobConf job,
	                  final TaskSplitIndex splitIndex,
	                  final TaskUmbilicalProtocol umbilical,
	                  TaskReporter reporter
	    // make a task context

	    // make a mapper
	        org.apache.hadoop.mapreduce.Mapper<INKEY,INVALUE,OUTKEY,OUTVALUE> mapper =
	          (org.apache.hadoop.mapreduce.Mapper<INKEY,INVALUE,OUTKEY,OUTVALUE>)
	            ReflectionUtils.newInstance(taskContext.getMapperClass(), job);
	                JobContextImpl.getMapperClass() 
	                	return (Class<? extends Mapper<?,?,?,?>>) 
	                	    conf.getClass(MAP_CLASS_ATTR, Mapper.class);
								"mapreduce.job.map.class";

				Job job = new Job(params, "MultilevelPlot");
					Job extends JobContextImpl implements JobContext {  
				job.setMapperClass(PyramidPartitionMap.class);
					ensureState(JobState.DEFINE);
					conf.setClass(MAP_CLASS_ATTR, cls, Mapper.class);

	    // make the input format

	    // rebuild the input split

	    // get an output object
	    if (job.getNumReduceTasks() == 0) {
	      output = 
	          new NewDirectOutputCollector(taskContext, job, umbilical, reporter);
	    } else {
	        output = new NewOutputCollector(taskContext, job, umbilical, reporter);


	    org.apache.hadoop.mapreduce.MapContext<INKEY, INVALUE, OUTKEY, OUTVALUE> 
	    mapContext = 
	      new MapContextImpl<INKEY, INVALUE, OUTKEY, OUTVALUE>(job, getTaskID(), 
	          input, output, 
	          committer, 
	          reporter, split);

	    org.apache.hadoop.mapreduce.Mapper<INKEY,INVALUE,OUTKEY,OUTVALUE>.Context 
	        mapperContext = 
	          new WrappedMapper<INKEY, INVALUE, OUTKEY, OUTVALUE>().getMapContext(
	              mapContext);

	    try {
	        input.initialize(split, mapperContext);
	        mapper.run(mapperContext);
	        mapPhase.complete();
	        setPhase(TaskStatus.Phase.SORT);
	        statusUpdate(umbilical);
	        input.close();
	        input = null;
	        output.close(mapperContext);
	        output = null;
	    } finally {
	        closeQuietly(input);
	        closeQuietly(output, mapperContext);

	private class NewOutputCollector<K,V> extends org.apache.hadoop.mapreduce.RecordWriter<K,V> {
		final MapOutputCollector<K,V> collector;

	  	NewOutputCollector(org.apache.hadoop.mapreduce.JobContext jobContext,
	  	                   JobConf job,
	  	                   TaskUmbilicalProtocol umbilical,
	  	                   TaskReporter reporter
	  	                   )	  
	  		collector = createSortingCollector(job, reporter);

	  	public void write(K key, V value) throws IOException, InterruptedException {
	  	    collector.collect(key, value, partitioner.getPartition(key, value, partitions));
	  	


	<KEY, VALUE> MapOutputCollector<KEY, VALUE> createSortingCollector(JobConf job, TaskReporter reporter)

		Class<?>[] collectorClasses = job.getClasses(JobContext.MAP_OUTPUT_COLLECTOR_CLASS_ATTR, MapOutputBuffer.class);


	    MapOutputCollector<KEY, VALUE> collector =
	      ReflectionUtils.newInstance(subclazz, job);
	    collector.init(context);
	    LOG.info("Map output collector class = " + collector.getClass().getName());
	    return collector;
	    
