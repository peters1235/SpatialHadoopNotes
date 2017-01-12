package org.apache.hadoop.mapred;

public class JobClient extends CLI 
//在旧API中使用，转了一圈还是调用的Job来完成提交工作 ，新API中直接用Job
	//里面有submitJobInternal方法

	//新的API中的Job类里头用到的JobSubmitter类里也有submitInternal方法

	/** 
	 * Utility that submits a job, then polls for progress until the job is
	 * complete.	 
	 */
	static RunningJob runJob(JobConf job) {
	    JobClient jc = new JobClient(job);
	    RunningJob rj = jc.submitJob(job);
	    try {
	    	//Monitor a job and print status in real-time as progress is made and tasks fail.
	        if (!jc.monitorAndPrintJob(job, rj)) {
	            throw new IOException("Job failed!");
	        }
	    } catch (InterruptedException ie) {
	      	    Thread.currentThread().interrupt();
	    }
	    return rj;}

	boolean monitorAndPrintJob(JobConf conf, RunningJob job	
	    return ((NetworkedJob)job).monitorAndPrintJob();

	/**
	 * A NetworkedJob is an implementation of RunningJob.  It holds
	 * a JobProfile object to provide some info, and interacts with the
	 * remote service to provide certain functionality.
	 */
	static class NetworkedJob implements RunningJob {
		org.apache.hadoop.mapreduce.Job job;

		boolean monitorAndPrintJob() throws IOException, InterruptedException {
		     return job.monitorAndPrintJob();
	}

	/**
	 *把job提交到mr 系统里，返回一个RunningJob对象，可用它跟踪Job的状态
	 * Submit a job to the MR system.
	 * This returns a handle to the {@link RunningJob} which can be used to track	 
	 * @return a handle to the RunningJob which can be used to track the
	 *         running-job.
	 */
	RunningJob submitJob(final JobConf conf) 
	    return submitJobInternal(conf);

	public RunningJob submitJobInternal(final JobConf conf)
	     
	 
	    conf.setBooleanIfUnset("mapred.mapper.new-api", false);
	    conf.setBooleanIfUnset("mapred.reducer.new-api", false);
	    Job job = clientUgi.doAs(new PrivilegedExceptionAction<Job> ()  
	        @Override
	        public Job run()  
	            Job job = Job.getInstance(conf);
	            job.submit();
	            return job;
	        
	   
	    // update our Cluster instance with the one created by Job for submission
	    // (we can't pass our Cluster instance to Job, since Job wraps the config
	    // instance, and the two configs would then diverge)
	    cluster = job.getCluster();
	    return new NetworkedJob(job);
	 



package org.apache.hadoop.mapreduce.tools;
public class CLI extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.conf.Tool {