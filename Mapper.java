package org.apache.hadoop.mapreduce;
// 没有Reducer的话，Map的输出不会按 key排序，直接 用OutputFormat输出
public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {
	public abstract class Context
	    implements MapContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> {

	//Task开始之前调用一次，默认啥也不干
	protected void setup(Context context)

    //task结束之后调用一次，默认啥也不干
	protected void cleanup(Context context）

	//默认的map方法
    void map(KEYIN key, VALUEIN value, 
                       Context context) throws IOException, InterruptedException {
        context.write((KEYOUT) key, (VALUEOUT) value);

	//高级用户可重写此方法以更粒度地控制map任务的执行过程
	public void run(Context context)
		setup(context);
		try {
	        while (context.nextKeyValue()) {
	            map(context.getCurrentKey(), context.getCurrentValue(), context);
	        }
	    } finally {
	        cleanup(context);
    

