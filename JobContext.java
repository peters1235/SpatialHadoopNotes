//执行行任务task时，给任务提供job的一个只读视图
public interface JobContext extends MRJobConfig {
	public Configuration getConfiguration();
	public JobID getJobID();
	public int getNumReduceTasks();
	public Path getWorkingDirectory() throws IOException;
	Class<?> getOutputKeyClass();
	Class<?> getMapOutputKeyClass();
	Class<?> getMapOutputValueClass();
	String getJobName();
	Class<? extends InputFormat<?,?>> getInputFormatClass() 
	Class<? extends Reducer<?,?,?,?>> getReducerClass() 
	Path[] getArchiveClassPaths();
	URI[] getCacheFiles() 