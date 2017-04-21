//执行task时，给task提供的job的一个只读视图
//接口继承接口用extends 不用implements
public interface JobContext extends MRJobConfig {
	//里面全是一些get方法
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