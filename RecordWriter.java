/*
RecordWriter writes the output <key, value> pairs to an output file
RecordWriter implementations write the job outputs to the FileSystem.
*/
RecordWriter
	/*
	Writes a key/value pair
	*/	
	abstract void write(K key, V value

	/*
	Close this RecordWriter to future operations
	*/
	abstract void close(TaskAttemptContext context