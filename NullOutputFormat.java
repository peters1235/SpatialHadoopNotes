 class NullOutputFormat<K, V> extends OutputFormat<K, V> 
 	RecordWriter<K, V> getRecordWriter(TaskAttemptContext context) {
	 	return new RecordWriter<K, V>(){
	 	    public void write(K key, V value) { }
	 	    public void close(TaskAttemptContext context) { }

	public OutputCommitter getOutputCommitter(TaskAttemptContext context) {
	  return new OutputCommitter() {
	  	//空的，啥也不干
 	  