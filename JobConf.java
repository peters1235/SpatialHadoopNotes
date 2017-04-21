package org.apache.hadoop.mapred;
/*
属于旧API，就是个Configuration
用户主要使用JobConf来对任务进行配置，Hadoop框架尽量按照JobConf中的配置来执行任务，不过呢，有些参数默认是final的，不能改，
有些参数还跟别的参数有关联，不好改
一般用于配置执行任务所需的 Mapper, combiner (if any), Partitioner, Reducer, InputFormat and OutputFormat 等
也能进行高级的配置：使用哪个 Comparator、 把哪些文件放入DistributedCache

JobConf在Configuration在基础上添加了许多 get set 任务配置的方法
*/
JobConf extends org.apache.hadoop.conf.configuration
	//跟MapReduce更相关
	void setJarByClass(Class cls) 
	String jar = ClassUtil.findContainingJar(cls);
	setJar(jar);
		set(JobContext.JAR, jar);

	void setInputFormat(Class<? extends InputFormat> theClass) 

	void setOutputCommitter(Class<? extends OutputCommitter> theClass)




/*
	默认会加载classPath里头的
	core-default.xml: Read-only defaults for hadoop. 
	core-site.xml: Site-specific configuration for a given hadoop installation.
	其它的配置文件随后加上

	final类型的参数后续不能改
*/
public class Configuration implements Iterable<Map.Entry<String,String>>,
                                      Writable
    //主要是一些键值对相关的设置
	