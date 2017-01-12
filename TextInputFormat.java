//*用linefeed or carriage-return分隔行 Keys are
 * the position in the file, and values are the line of text*/
public class TextInputFormat extends FileInputFormat<LongWritable, Text>
	RecordReader<LongWritable, Text> createRecordReader(InputSplit split,TaskAttemptContext context) {
	    String delimiter = context.getConfiguration().get("textinputformat.record.delimiter");
	    byte[] recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
	    return new LineRecordReader(recordDelimiterBytes);