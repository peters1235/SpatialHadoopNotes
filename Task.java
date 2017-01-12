/**
 * Base class for tasks.
 */
abstract public class Task implements Writable, Configurable {
	// MapTask ReduceTassk 都从这个类派生

	public void initialize(JobConf job, JobID id, 
	                       Reporter reporter,
	                       boolean useNewApi) 
	    jobContext = new JobContextImpl(job, id, reporter);
	    taskContext = new TaskAttemptContextImpl(job, taskId, reporter);
	    if (getState() == TaskStatus.State.UNASSIGNED) {
	      setState(TaskStatus.State.RUNNING);
	    }
	    if (useNewApi) {
	        if (LOG.isDebugEnabled())  
	            LOG.debug("using new api for output committer");
	         
	        //新API的Committer来自OutptFormat，旧的来自Configuration
	        outputFormat =  ReflectionUtils.newInstance(taskContext.getOutputFormatClass(), job);
	        committer = outputFormat.getOutputCommitter(taskContext);
	    } else {
	        committer = conf.getOutputCommitter();
	     
	    Path outputPath = FileOutputFormat.getOutputPath(conf);
	    if (outputPath != null) {
	        if ((committer instanceof FileOutputCommitter)) {
	            FileOutputFormat.setWorkOutputPath(conf, 
	                ((FileOutputCommitter)committer).getTaskAttemptPath(taskContext));
	        } else {
	            FileOutputFormat.setWorkOutputPath(conf, outputPath);	         
	    
	    committer.setupTask(taskContext);
	    Class<? extends ResourceCalculatorProcessTree> clazz =
	        conf.getClass(MRConfig.RESOURCE_CALCULATOR_PROCESS_TREE,
	            null, ResourceCalculatorProcessTree.class);
	    pTree = ResourceCalculatorProcessTree
	            .getResourceCalculatorProcessTree(System.getenv().get("JVM_PID"), clazz, conf);
	    LOG.info(" Using ResourceCalculatorProcessTree : " + pTree);
	    if (pTree != null) {
	        pTree.updateProcessTree();
	        initCpuCumulativeTime = pTree.getCumulativeCpuTime();
