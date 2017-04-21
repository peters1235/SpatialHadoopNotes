package org.apache.hadoop.mapred;

public class MapRunner<K1, V1, K2, V2>
    implements MapRunnable<K1, V1, K2, V2>

    public void configure(JobConf job) {
        this.mapper = ReflectionUtils.newInstance(job.getMapperClass(), job);
      		return getClass("mapred.mapper.class", IdentityMapper.class, Mapper.class);

    void run(RecordReader<K1, V1> input, OutputCollector<K2, V2> output,Reporter reporter)
    	try {
    	    // allocate key & value instances that are re-used for all entries
    	    K1 key = input.createKey();
    	    V1 value = input.createValue();
    	    
    	    while (input.next(key, value)) {
    	        // map pair to output
    	        mapper.map(key, value, output, reporter);
    	        if(incrProcCount) {
    	          reporter.incrCounter(SkipBadRecords.COUNTER_GROUP, 
    	              SkipBadRecords.COUNTER_MAP_PROCESSED_RECORDS, 1);
    	          
    	} finally {
    	    mapper.close();


JobConf job = new JobConf(params, FileMBR.class);
job.setMapperClass(FileMBRMapper.class);
	setClass("mapred.mapper.class", theClass, Mapper.class);