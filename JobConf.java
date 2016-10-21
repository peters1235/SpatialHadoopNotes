/*
一般用于配置 Mapper, combiner (if any), Partitioner, Reducer, InputFormat and OutputFormat implementations to be used 
*/
JobConf extends configuration
	void setJarByClass(Class cls) 
	String jar = ClassUtil.findContainingJar(cls);
	setJar(jar);
		set(JobContext.JAR, jar);


/*
	默认会加载classPath里头的
	core-default.xml: Read-only defaults for hadoop. 
	core-site.xml: Site-specific configuration for a given hadoop installation.
	其它的配置文件随后加上

	final类型的参数后续不能改
*/
Configuration
	