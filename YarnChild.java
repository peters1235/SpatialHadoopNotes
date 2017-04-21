//The main() for MapReduce task processes.
class YarnChild 
	static void main(String[] args) 
		Thread.setDefaultUncaughtExceptionHandler(new YarnUncaughtExceptionHandler());

		final JobConf job = new JobConf(MRJobConfig.JOB_CONF_FILE);//"Job.xml"

		// * Base class for tasks.
		Task task = null;
		task = myTask.getTask();
		YarnChild.taskid = task.getTaskID();

		MRApps.setJobClassLoader(job);
			setClassLoader(createJobClassLoader(conf), conf);
				ClassLoader jobClassLoader = null;
				if (conf.getBoolean(MRJobConfig.MAPREDUCE_JOB_CLASSLOADER, false)) {
				    String appClasspath = System.getenv(Environment.APP_CLASSPATH.key());
				    if (appClasspath == null) {
				      LOG.warn("Not creating job classloader since APP_CLASSPATH is not set.");
				    } else {
				      LOG.info("Creating job classloader");
				      if (LOG.isDebugEnabled()) {
				        LOG.debug("APP_CLASSPATH=" + appClasspath);
				      }
				      String[] systemClasses = getSystemClasses(conf);
				      jobClassLoader = createJobClassLoader(appClasspath,
				          systemClasses);
				    }
				}
				return jobClassLoader;

				if (classLoader != null) {
				    LOG.info("Setting classloader " + classLoader.getClass().getName() +
				        " on the configuration and as the thread context classloader");
				    conf.setClassLoader(classLoader);
				    Thread.currentThread().setContextClassLoader(classLoader);

		Task taskFinal = task;
		taskFinal.run(job, umbilical); 

Job job = new Job(params, "MultilevelPlot");
job.setJarByClass(SingleLevelPlot.class);
	ensureState(JobState.DEFINE);
	conf.setJarByClass(cls);
		String jar = ClassUtil.findContainingJar(cls);
		if (jar != null) {
		    setJar(jar);
		    	set(JobContext.JAR, jar); //"mapreduce.job.jar";

	public void setMapperClass(Class<? extends Mapper> theClass) {
		setClass("mapred.mapper.class", theClass, Mapper.class);