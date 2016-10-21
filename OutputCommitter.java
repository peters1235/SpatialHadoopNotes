/*
OutputCommitter describes the commit of task output for a Map-Reduce job.

The Map-Reduce framework relies on the OutputCommitter of the job to:

Setup the job during initialization. For example, create the temporary output directory for the job during the initialization of the job.
Cleanup the job after the job completion. For example, remove the temporary output directory after the job completion.
Setup the task temporary output.
Check whether a task needs a commit. This is to avoid the commit procedure if a task does not need commit.
Commit of the task output.
Discard the task commit.

The methods in this class can be called from several different processes and from several different contexts. 
It is important to know which process and which context each is called from. 
Each method should be marked accordingly in its documentation. 
It is also important to note that not all methods are guaranteed to be called once and only once. 
If a method is not guaranteed to have this property the output committer needs to handle this appropriately. 
Also note it will only be in rare situations where they may be called multiple times for the same task.
*/
abstract class OutputCommitter
	/*
	For the framework to setup the job output during initialization. 
	This is called from the application master process for the entire job. 
	This will be called multiple times, once per job attempt.
	*/
	abstract void setupJob(JobContext jobContext)


	/*
	Deprecated. Use commitJob(JobContext) and abortJob(JobContext, JobStatus.State) instead.

	For cleaning up the job's output after job completion. 
	This is called from the application master process for the entire job. 
	This may be called multiple times.
	*/
	public void cleanupJob(JobContext jobContext) throws IOException { }


	/*
	For committing job's output after successful job completion. 
	Note that this is invoked for jobs with final runstate as SUCCESSFUL. 
	This is called from the application master process for the entire job. 
	This is guaranteed to only be called once. If it throws an exception the entire job will fail.
	*/
	public void commitJob(JobContext jobContext) throws IOException {
	    cleanupJob(jobContext);

	/*
	For aborting an unsuccessful job's output. 
	Note that this is invoked for jobs with final runstate as JobStatus.State.FAILED or JobStatus.State.KILLED. 
	This is called from the application master process for the entire job. This may be called multiple times.
	*/
	public void abortJob(JobContext jobContext, JobStatus.State state) 	
	  cleanupJob(jobContext);
	
	  
	/*
	Sets up output for the task. 
	This is called from each individual task's process that will output to HDFS, 
	and it is called just for that task. 
	This may be called multiple times for the same task, but for different task attempts.
	*/
	public abstract void setupTask(TaskAttemptContext taskContext)

	/*
	Check whether task needs a commit. This is called from each individual task's process that will output to HDFS, 
	and it is called just for that task
	*/
	public abstract boolean needsTaskCommit(TaskAttemptContext taskContext)

	/*
	To promote the task's temporary output to final output location. 
	If needsTaskCommit(TaskAttemptContext) returns true and this task is the task that the AM determines finished first, 
	this method is called to commit an individual task's output. 
	This is to mark that tasks output as complete, 
	as commitJob(JobContext) will also be called later on if the entire job finished successfully. 
	This is called from a task's process. 
	This may be called multiple times for the same task, but different task attempts. 
	It should be very rare for this to be called multiple times and requires odd networking failures to make this happen. 
	In the future the Hadoop framework may eliminate this race.
	*/
	public abstract void commitTask(TaskAttemptContext taskContext)

	/*
	Discard the task output. 
	This is called from a task's process to clean up a single task's output that can not yet been committed. 
	This may be called multiple times for the same task, but for different task attempts.
	*/
	public abstract void abortTask(TaskAttemptContext taskContext)


	/*
	Deprecated. Use isRecoverySupported(JobContext) instead.

	Is task output recovery supported for restarting jobs? 
	If task output recovery is supported, job restart can be done more efficiently.
	*/
	public boolean isRecoverySupported() 


	/*
	Is task output recovery supported for restarting jobs? If task output recovery is supported, job restart can be done more efficiently.
	*/	
	public boolean isRecoverySupported(JobContext jobContext) throws IOException {
	  return isRecoverySupported();

	/*
	Recover the task output. 
	The retry-count for the job will be passed via the MRJobConfig.APPLICATION_ATTEMPT_ID key in 
	TaskAttemptContext.() for the OutputCommitter. 
	This is called from the application master process, but it is called individually for each task. 
	If an exception is thrown the task will be attempted again. 
	This may be called multiple times for the same task. But from different application attempts.
	*/  
	public void recoverTask(TaskAttemptContext taskContext)



  	  return false;
  