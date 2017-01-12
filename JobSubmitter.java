package org.apache.hadoop.mapreduce;
JobSubmitter

	private ClientProtocol submitClient;

	public interface ClientProtocol extends VersionedProtocol ,没了{
	/*
	向系统提交job，工作包括：
		检查job的输入、输出
		计算InputSplits
		需要的话，设置DistributedCache所需的账户信息
		Copying the job's jar and configuration to the map-reduce system directory on the distributed file-system.
		将job提交给JobTracker，有需要的话，监控其状态

	Internal method for submitting jobs to the system.

	The job submission process involves:


	Checking the input and output specifications of the job.
	Computing the InputSplits for the job.
	Setup the requisite accounting information for the DistributedCache of the job, if necessary.
	Copying the job's jar and configuration to the map-reduce system directory on the distributed file-system.
	Submitting the job to the JobTracker and optionally monitoring it's status.
	Parameters:
	job the configuration to submit
	cluster the handle to the Cluster
	*/
	JobStatus submitJobInternal(Job job, Cluster cluster) 

		//validate the jobs output specs 
		checkSpecs(job);

		Configuration conf = job.getConfiguration();
		//把 一个什么框架 添加 到Cache里，不知道框架干啥用的
		addMRFrameworkToDistributedCache(conf);
			DistributedCache.addCacheArchive(uri, conf);

		
		/*
		像是什么中间目录的东西
   		* Initializes the staging directory and returns the path. It also
		   * keeps track of all necessary ownership & permissions*/
		Path jobStagingArea = JobSubmissionFiles.getStagingDir(cluster, conf);

		//可以的话，设置
		conf.set(MRJobConfig.JOB_SUBMITHOST
		conf.set(MRJobConfig.JOB_SUBMITHOSTADDR

		JobID jobId = submitClient.getNewJobID();
		job.setJobID(jobId);

		//提交目录在Staging目录底下 的JobID文件夹下
		Path submitJobDir = new Path(jobStagingArea, jobId.toString());

		conf.set(MRJobConfig.MAPREDUCE_JOB_DIR, submitJobDir.toString());
		LOG.debug("Configuring job " + jobId + " with " + submitJobDir 
		    + " as the submit dir");

		//好像会把各种jar包 lib 都放到对应的位置去
		copyAndConfigureFiles(Job job, Path jobSubmitDir) 
			short replication = (short)conf.getInt(Job.SUBMIT_REPLICATION, 10);
			copyAndConfigureFiles(job, jobSubmitDir, replication);
				//这些好像不是SideData里添加的lib jar file 所对应的参数
				//可能是从命令行添加的文件才会通过 这个 方法添加到集群中


				String files = conf.get("tmpfiles");
				String libjars = conf.get("tmpjars");
				String archives = conf.get("tmparchives");

		//生成分片信息文件 
		int maps = writeSplits(job, submitJobDir);
		//map数== split数目
		conf.setInt(MRJobConfig.NUM_MAPS, maps);
		LOG.info("number of splits:" + maps);

		// Write job file to submit dir
		writeConf(conf, submitJobFile);
			FSDataOutputStream out = 
			    FileSystem.create(jtFs, jobFile, 
			                    new FsPermission(JobSubmissionFiles.JOB_FILE_PERMISSION));
			try {
			    conf.writeXml(out);
			} finally {
			    out.close();


		//真正进行提交 submitClient的类型是YarnRunner ，本地执行的话会是LocalJobRunner，
			    //两者都实现ClientProtocol接口  LocalJobRunner看着没怎么干活
		status = submitClient.submitJob(
		    jobId, submitJobDir.toString(), job.getCredentials());
		if (status != null) {
		    return status;
		} else {
		    throw new IOException("Could not launch job");



		  

	//生成分片
	int writeSplits(org.apache.hadoop.mapreduce.JobContext job,Path jobSubmitDir)  
	    JobConf jConf = (JobConf)job.getConfiguration();
	    int maps;
	    if (jConf.getUseNewMapper()) 
	    	//新API使用Job，Job自带一个Configuration对象，
	        maps = writeNewSplits(job, jobSubmitDir);
	    else
	    	//旧API从Configuration对象派生生一个JobConf对象用于表示 Job的相关配置
	        maps = writeOldSplits(jConf, jobSubmitDir);
	    
	    return maps;

	//writeNewSplits 和SplitComparator 类是用新API时调用的，
	    //writeOldSplits 看着是把用于比较分片大小的类放到writeOldSplits方法里了
	<T extends InputSplit>
	int writeNewSplits(JobContext job, Path jobSubmitDir) 
	    Configuration conf = job.getConfiguration();
	    InputFormat<?, ?> input =
	      ReflectionUtils.newInstance(job.getInputFormatClass(), conf);
  
	    List<InputSplit> splits = input.getSplits(job);
	    T[] array = (T[]) splits.toArray(new InputSplit[splits.size()]);
  
	    // sort the splits into order based on size, so that the biggest
	    // go first
	    Arrays.sort(array, new SplitComparator());
	    JobSplitWriter.createSplitFiles(jobSubmitDir, conf, 
	        jobSubmitDir.getFileSystem(conf), array);
	    return array.length;

	private static class SplitComparator implements Comparator<InputSplit> {
	  @Override
	  public int compare(InputSplit o1, InputSplit o2) {
	    try {
	      long len1 = o1.getLength();
	      long len2 = o2.getLength();
	      if (len1 < len2) {
	        return 1;
	      } else if (len1 == len2) {
	        return 0;
	      } else {
	        return -1;
	      }
	    } catch (IOException ie) {
	      throw new RuntimeException("exception in compare", ie);
	    } catch (InterruptedException ie) {
	      throw new RuntimeException("exception in compare", ie);
	    }
	  }
	}

	private int writeOldSplits(JobConf job, Path jobSubmitDir) 
	 
	    org.apache.hadoop.mapred.InputSplit[] splits =
	    job.getInputFormat().getSplits(job, job.getNumMapTasks());
	    // sort the splits into order based on size, so that the biggest
	    // go first
	    Arrays.sort(splits, new Comparator<org.apache.hadoop.mapred.InputSplit>() {
	      public int compare(org.apache.hadoop.mapred.InputSplit a,
	                         org.apache.hadoop.mapred.InputSplit b) {
	        try {
	          long left = a.getLength();
	          long right = b.getLength();
	          if (left == right) {
	            return 0;
	          } else if (left < right) {
	            return 1;
	          } else {
	            return -1;
	          }
	        } catch (IOException ie) {
	          throw new RuntimeException("Problem getting input split size", ie);
	        }
	      }
	    });
	    JobSplitWriter.createSplitFiles(jobSubmitDir, job, 
	        jobSubmitDir.getFileSystem(job), splits);
	    return splits.length;
  