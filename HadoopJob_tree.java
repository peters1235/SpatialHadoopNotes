HadoopJob

FileSystem.get(this.toUri(), conf):FileSystem
/*
Returns the FileSystem for this URI's scheme and authority. 
The scheme of the URI determines a configuration property name,
 fs.scheme.class whose value names the FileSystem class. 
 The entire URI is passed to the FileSystem instance's initialize method
 */

Job extends JobContextImpl implements JobContext 
JobConf extends Configuration
JobConf job = new JobConf(params, Sampler.class);
job.setJobName("Sample");
job.setMapOutputKeyClass(IntWritable.class);
job.setMapOutputValueClass(Text.class);

job.setMapperClass(Map.class);
job.setReducerClass(Reduce.class);
RunningJob run_job = JobClient.runJob(job);
	/*
	JobClient is the primary interface for the user-job to interact with the cluster. JobClient provides facilities to submit jobs, track their progress, access component-tasks' reports/logs, get the Map-Reduce cluster status information etc.

The job submission process involves:

Checking the input and output specifications of the job.
Computing the InputSplits for the job.
Setup the requisite accounting information for the DistributedCache of the job, if necessary.
Copying the job's jar and configuration to the map-reduce system directory on the distributed file-system.
Submitting the job to the cluster and optionally monitoring it's status.
Normally the user creates the application, describes various facets of the job via JobConf and then uses the JobClient to submit the job and monitor its progress.
Here is an example on how to use JobClient:

     // Create a new JobConf
     JobConf job = new JobConf(new Configuration(), MyJob.class);
     
     // Specify various job-specific parameters     
     job.setJobName("myjob");
     
     job.setInputPath(new Path("in"));
     job.setOutputPath(new Path("out"));
     
     job.setMapperClass(MyJob.MyMapper.class);
     job.setReducerClass(MyJob.MyReducer.class);

     // Submit the job, then poll for progress until the job is complete
     JobClient.runJob(job);
 

	*/
	JobClient jc = new JobClient(job);

    RunningJob rj = jc.submitJob(job);
    	/*RunningJob is the user-interface to query for details on a running Map-Reduce job.

		Clients can get hold of RunningJob via the JobClient and 
		then query the running-job for details such as name, configuration, progress etc.
   */


RunningJob
//RunningJob is the user-interface to query for details on a running Map-Reduce job.

JobClient.runJob(job);
	//Utility that submits a job, then polls for progress until the job is complete.


job.setJarByClass(Indexer.class);
	conf:JobConf.setJarByClass(cls);
		String jar = ClassUtil.findContainingJar(clazz=cls);
			ClassLoader loader = clazz.getClassLoader();
			/* 抽象类
				A class loader is an object that is responsible for loading classes.
				 The class ClassLoader is an abstract class. Given the binary name of a class, 
				 a class loader should attempt to locate or generate 
				 that constitutes a definition for the class. 
				A typical strategy is to transform the name into a 
				file name and then read a "class file" of that name from a file system.
			*/
				//类名换成路径名				
			String classFile = clazz.getName().replaceAll("\\.", "/") + ".class";
		if (jar != null) {
      		setJar(jar);
      			set(JobContext.JAR, jar);
      				Configuration.public void set(String name, String value) {
    					set(name, value, null);