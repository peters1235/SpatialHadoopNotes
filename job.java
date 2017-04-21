package org.apache.hadoop.mapreduce;
/*
提交job的用户眼中的 job
The job submitter's view of the Job.

可以通过这个类配置、提交任务，控制任务的执行，并查询其执行状态
The set methods only work until the job is submitted, afterwards they will throw an IllegalStateException.

Normally the user creates the application, 
describes various facets of the job via Job and then submits the job and monitor its progress.

Here is an example on how to submit a job:

     // Create a new Job
     Job job = Job.getInstance();
     job.setJarByClass(MyJob.class);
     
     // Specify various job-specific parameters     
     job.setJobName("myjob");
     
     job.setInputPath(new Path("in"));
     job.setOutputPath(new Path("out"));
     
     job.setMapperClass(MyJob.MyMapper.class);
     job.setReducerClass(MyJob.MyReducer.class);

     // Submit the job, then poll for progress until the job is complete
     job.waitForCompletion(true);
 */
public class Job extends JobContextImpl implements JobContext { 

	public static enum JobState {DEFINE, RUNNING};

	//用于获取集群信息
	private Cluster cluster;

	setJarByClass(Class<?> cls) {
		ensureState(JobState.DEFINE);
		JobConf:conf.setJarByClass(cls);

	boolean waitForCompletion(boolean verbose)
		if (state == JobState.DEFINE) {
		    submit();
		if (verbose) {
		    monitorAndPrintJob();
		} else {
		    // get the completion poll interval from the client.
		    int completionPollIntervalMillis = 
		        Job.getCompletionPollInterval(cluster.getConf());
		    while (!isComplete()) {		      
		        Thread.sleep(completionPollIntervalMillis);		      		    
		
		return isSuccessful();


	/*把任务提交到集群然后立即返回*/
	public void submit() 
		/*要求JobState必须为Define，不知道什么用
		State枚举要么为Define要么为Running
		应该是要区分定义任务和执行任务
		*/
		ensureState(JobState.DEFINE);
			if (state != this.state) {
			  throw new IllegalStateException("Job in state "+ this.state + 
			                                  " instead of " + state);
			}

			if (state == JobState.RUNNING && cluster == null) {
			  throw new IllegalStateException
			    ("Job in state " + this.state
			     + ", but it isn't attached to any job tracker!");
		
		/*
		应该是默认使用新的API
		Default to the new APIs unless they are explicitly set or the old mapper or reduce attributes are used.*/
		setUseNewAPI();

		connect();

		final JobSubmitter submitter = getJobSubmitter(cluster.getFileSystem(), cluster.getClient());
			return new JobSubmitter(fs, submitClient);
				this.submitClient = submitClient;
				this.jtFs = submitFs;

		status = ugi.doAs(new PrivilegedExceptionAction<JobStatus>() {
		    public JobStatus run()  
		        return submitter.submitJobInternal(Job.this, cluster);
		   
		   	});
		state = JobState.RUNNING;
		LOG.info("The url to track the job: " + getTrackingURL());

	//取得集群对象的实例
	synchronized void connect()
		if (cluster == null) {
		  cluster = 
		    ugi.doAs(new PrivilegedExceptionAction<Cluster>() {
		               public Cluster run()
		                      throws IOException, InterruptedException, 
		                             ClassNotFoundException {
		                 return new Cluster(getConfiguration());
		               }
		             });
		}

	boolean monitorAndPrintJob() 	
	    String lastReport = null;
	    Job.TaskStatusFilter filter;
	    Configuration clientConf = getConfiguration();
	    filter = Job.getTaskOutputFilter(clientConf);
	    JobID jobId = getJobID();
	    LOG.info("Running job: " + jobId);
	    int eventCounter = 0;
	    boolean profiling = getProfileEnabled();
	    IntegerRanges mapRanges = getProfileTaskRange(true);
	    IntegerRanges reduceRanges = getProfileTaskRange(false);
	    int progMonitorPollIntervalMillis = Job.getProgressPollInterval(clientConf);
	    /* make sure to report full progress after the job is done */
	    boolean reportedAfterCompletion = false;
	    boolean reportedUberMode = false;
	    while (!isComplete() || !reportedAfterCompletion) {
	        if (isComplete()) {
	            reportedAfterCompletion = true;
	        } else 
	            Thread.sleep(progMonitorPollIntervalMillis);
	         
	        if (status.getState() == JobStatus.State.PREP) {
	            continue;
	            
	        //uber 模式  
	        if (!reportedUberMode)  
	            reportedUberMode = true;
	            LOG.info("Job " + jobId + " running in uber mode : " + isUber());
	          
	        // 得到类似 map 0% reduce 0%  的进度串
	        String report = 
	            (" map " + StringUtils.formatPercent(mapProgress(), 0)+
	              " reduce " + 
	              StringUtils.formatPercent(reduceProgress(), 0));

	        //进度跟上次不一样才更新
	        if (!report.equals(lastReport)) {
	            LOG.info(report);
	            lastReport = report;
	        }
    
	        TaskCompletionEvent[] events = getTaskCompletionEvents(eventCounter, 10); 
	        eventCounter += events.length;
	        printTaskEvents(events, filter, profiling, mapRanges, reduceRanges);
	    }
	    boolean success = isSuccessful();
	    if (success) {
	        LOG.info("Job " + jobId + " completed successfully");
	    } else {
	        LOG.info("Job " + jobId + " failed with state " + status.getState() + 
	            " due to: " + status.getFailureInfo());
	    }
	    Counters counters = getCounters();
	    if (counters != null) {
	        LOG.info(counters.toString());
	    }
	    return success;
    
    //非阻塞式调用
    public boolean isComplete() 
        ensureState(JobState.RUNNING);
        updateStatus();
        return status.isJobComplete();

    public boolean isUber() throws IOException, InterruptedException {
        ensureState(JobState.RUNNING);
        updateStatus();
        return status.isUber();

    synchronized void updateStatus() throws IOException {
        
        this.status = ugi.doAs(new PrivilegedExceptionAction<JobStatus>() {
            @Override
            public JobStatus run() throws IOException, InterruptedException {
            	return cluster.getClient().getJobStatus(status.getJobID());
            }
        }); 
       
        
        if (this.status == null) {
            throw new IOException("Job status not available ");
        }
        this.statustime = System.currentTimeMillis();