/*
InputFormat describes the input-specification for a Map-Reduce job.

The Map-Reduce framework relies on the InputFormat of the job to:

Validate the input-specification of the job.
Split-up the input file(s) into logical InputSplits, each of which is then assigned to an individual Mapper.
Provide the RecordReader implementation to be used to glean input records from the logical InputSplit for processing by the Mapper.
The default behavior of file-based InputFormats, typically sub-classes of FileInputFormat, is to split the input into logical InputSplits based on the total size, in bytes, of the input files. However, the FileSystem blocksize of the input files is treated as an upper bound for input splits. A lower bound on the split size can be set via mapreduce.input.fileinputformat.split.minsize.

Clearly, logical splits based on input-size is insufficient for many applications since record boundaries are to respected. In such cases, the application has to also implement a RecordReader on whom lies the responsibility to respect record-boundaries and present a record-oriented view of the logical InputSplit to the individual task.
*/

InputFormat<K,V>
	/*
	Logically split the set of input files for the job.

	Each InputSplit is then assigned to an individual Mapper for processing.

	Note: The split is a logical split of the inputs and the input files are not physically split into chunks. 
	For e.g. a split could be <input-file-path, start, offset> tuple. 
	The InputFormat also creates the RecordReader to read the InputSplit.
	*/
	abstract List<InputSplit> getSplits(JobContext context

	/*
	Create a record reader for a given split. The framework will call RecordReader.
	initialize(InputSplit, TaskAttemptContext) before the split is used.
	*/
	abstract RecordReader<K,V> createRecordReader(InputSplit split,TaskAttemptContext context
