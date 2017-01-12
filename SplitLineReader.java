public class SplitLineReader extends org.apache.hadoop.util.LineReader {
    SplitLineReader(InputStream in, byte[] recordDelimiterBytes) {
        super(in, recordDelimiterBytes);
    
  
    SplitLineReader(InputStream in, Configuration conf,byte[] recordDelimiterBytes) throws IOException {
        super(in, conf, recordDelimiterBytes);
      
    boolean needAdditionalRecordAfterSplit() {
        return false;