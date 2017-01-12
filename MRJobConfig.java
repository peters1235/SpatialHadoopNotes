//各种字符串常量
org.apache.hadoop.mapreduce
public interface MRJobConfig {
	public static final String INPUT_FORMAT_CLASS_ATTR = "mapreduce.job.inputformat.class";

	public static final String MAP_CLASS_ATTR = "mapreduce.job.map.class";

	public static final String MAP_OUTPUT_COLLECTOR_CLASS_ATTR
	                                = "mapreduce.job.map.output.collector.class";

mapreduce.MRJobConfig
	/* A read-only view of the job that is provided to the tasks while they are running.
	给正在执行的task提供的 job的 只读视图*/
	interface mapreduce.JobContext  
		
		interface mapreduce.TaskAttemptContext extends Progressable
			class mapreduce.task.TaskAttemptContextImpl extends mapreduce.task.JobContextImpl
				class mapred.TaskAttemptContextImpl  implements  mapred.TaskAttemptContext
				class mapreduce.task.TaskInputOutputContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT>   
							implements mapreduce.TaskInputOutputContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
							extends mapreduce.task.TaskAttemptContextImpl
			//旧接口实现新接口
			interface mapred.TaskAttemptContext
				class mapred.TaskAttemptContextImpl extends mapreduce.task.TaskAttemptContextImpl

			interface mapreduce.TaskInputOutputContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 	 
				class mapreduce.task.TaskInputOutputContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 

				interface mapreduce.MapContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
					class mapreduce.lib.chain.ChainMapContextImpl
					class mapreduce.Mapper.Context
						class mapreduce.lib.map.WrappedMapper.Context
						//还有一个类没有源码 IllustratorContext
					class mapreduce.task.MapContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
				interface mapreduce.ReduceContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT>
					class mapreduce.lib.chain.ChainReduceContextImpl<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
					class mapreduce.Reducer.Context
						class mapreduce.lib.WrappedReducer.Context
						//还有一个类没有源码 IllustratorContext
					class ReduceContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT>

		//比父类多一个setJobID方法，其它方法都是get。。。	
		class mapreduce.task.JobContextImpl
			//建job时用的类
			class mapreduce.Job implements mapreduce.JobContext { //这还得写一次实现 JobContext接口么？不多余么?

			//旧类继承新类
			class mapred.JobContextImpl implements mapred.JobContext

			class mapreduce.task.TaskAttemptContextImpl  implements mapreduce.TaskAttemptContext
				//旧类继承新类
				class mapred.TaskAttemptContextImpl implements mapred.TaskAttemptContext

				class mapreduce.task.TaskInputOutputContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 				      
				       implements mapreduce.TaskInputOutputContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT>

				    class mapreduce.task.MapContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
				    	implements mapreduce.MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT>

				    class mapreduce.task.ReduceContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT>
				         
				        implements mapreduce.ReduceContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {




		//旧接口继承新接口
		interface mapred.JobContext		
			class mapred.JobContextImpl extends mapreduce.task.JobContextImpl 

		 
		


public interface util.Progressable {
  /**
   * Report progress to the Hadoop framework.
   */
  public void progress();