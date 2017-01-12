package org.apache.hadoop.mapred;
/*
属于旧API
一般用于配置 Mapper, combiner (if any), Partitioner, Reducer, InputFormat and OutputFormat implementations to be used 
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
	