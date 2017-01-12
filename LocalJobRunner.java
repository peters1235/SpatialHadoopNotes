class LocalJobRunner implements ClientProtocol {
	private HashMap<JobID, Job> jobs = new HashMap<JobID, Job>();

	//这里头也有个Job，跟MapReduce的Job不一样
	private class Job extends Thread implements TaskUmbilicalProtocol {

	 
	public String getStagingAreaDir()  
	    Path stagingRootDir = new Path(conf.get(JTConfig.JT_STAGING_AREA_ROOT, 
	        "/tmp/hadoop/mapred/staging"));
	    UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
	    String user;
	    randid = rand.nextInt(Integer.MAX_VALUE);
	    if (ugi != null) {
	        user = ugi.getShortUserName() + randid;
	    } else {
	        user = "dummy" + randid;
	    }
	    //makeQualified：Make sure that a path specifies a FileSystem.
	    return fs.makeQualified(new Path(stagingRootDir, user+"/.staging")).toString();
	    // 类似 file:/tmp/hadoop-shadoop/mapred/staging/shadoop519817672/.staging
	 
	//JobSubmitter里真正进行提交工作，到这里了， 或者会到YarnRunner里的submitJob
	public org.apache.hadoop.mapreduce.JobStatus submitJob(
	     org.apache.hadoop.mapreduce.JobID jobid, String jobSubmitDir,
	     Credentials credentials) throws IOException {
	    //这个Job是LocalJobRunner 定义的Job
	    Job job = new Job(JobID.downgrade(jobid), jobSubmitDir);
	    job.job.setCredentials(credentials);
	    return job.status;

	/*
	TaskUmbilicalProtocol：
	Protocol that task child process uses to contact its parent process. 
	The parent is a daemon which which polls the central master for a new map or reduce task 
	and runs it as a child process. 
	All communication between child and parent is via this protocol.

	看着主要是执行任务的函数 向 父进程 请求 要执行的任务，并向父线程 或者是进程，返回 其进度，执行信息
	也包括Reducer任务去请求已经完成的 Map任务的输出位置 
	*/
	private class Job extends Thread implements TaskUmbilicalProtocol {
		Job(JobID jobid, String jobSubmitDir) throws IOException {

			// Manage the distributed cache.  If there are files to be copied,
			// this will trigger localFile to be re-written again.
			localDistributedCacheManager = new LocalDistributedCacheManager();
			localDistributedCacheManager.setup(conf);


			// Write out configuration file.  Instead of copying it from
			// systemJobFile, we re-write it, since setup(), above, may have
			// updated it. 重新生成配置文件而不是直接复制，因为上一步可能修改了配置文件 			
			OutputStream out = localFs.create(localJobFile);
			try {
			    conf.writeXml(out);
			} finally {
			    out.close();
			}
			this.job = new JobConf(localJobFile);



			jobs.put(id, this);

			this.start();


