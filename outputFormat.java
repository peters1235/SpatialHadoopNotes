/*
OutputFormat describes the output-specification for a Map-Reduce job.

The Map-Reduce framework relies on the OutputFormat of the job to:

Validate the output-specification of the job. For e.g. check that the output directory doesn't already exist.
Provide the RecordWriter implementation to be used to write out the output files of the job. Output files are stored in a FileSystem.
*/
abstract class OutputFormat<K, V>
	/*Get the RecordWriter for the given task
	RecordWriter writes the output <key, value> pairs to an output file
	*/	
	public abstract RecordWriter<K, V>  getRecordWriter(TaskAttemptContext context)


	/*
	Check for validity of the output-specification for the job.
	This is to validate the output specification for the job when it is a job is submitted. 
	Typically checks that it does not already exist, throwing an exception when it already exists, 
	so that output is not overwritten.
	*/
	abstract void checkOutputSpecs(JobContext context




	/**
	   * Get the output committer for this output format. This is responsible
	   * for ensuring the output is committed correctly.
	   */
	abstract OutputCommitter getOutputCommitter(TaskAttemptContext context