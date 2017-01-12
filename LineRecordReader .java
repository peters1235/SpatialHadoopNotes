public class LineRecordReader extends RecordReader<LongWritable, Text>
	SplitLineReader in;
	FSDataInputStream fileIn;
	Seekable filePosition;

	public LineRecordReader(byte[] recordDelimiter) {
	    this.recordDelimiterBytes = recordDelimiter;

	void initialize(InputSplit genericSplit, TaskAttemptContext context)
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();

		//输入的文件，可能是压缩了的，也可能是没压缩过的
		fileIn = fs.open(file);

		CompressionCodec codec = new CompressionCodecFactory(job).getCodec(file);
		//如果压缩过
		if (null!=codec) {
		    isCompressedInput = true;	
		    decompressor = CodecPool.getDecompressor(codec);
		    //如果是可分的压缩文件
		    if (codec instanceof SplittableCompressionCodec) {
			    final SplitCompressionInputStream cIn =  ((SplittableCompressionCodec)codec).createInputStream(
			        fileIn, decompressor, start, end,
			        SplittableCompressionCodec.READ_MODE.BYBLOCK);
			    in = new CompressedSplitLineReader(cIn, job,this.recordDelimiterBytes);
			    start = cIn.getAdjustedStart();
			    end = cIn.getAdjustedEnd();
			    filePosition = cIn;
		    //如果是不可分的压缩文件
		    } else {
		        in = new SplitLineReader(codec.createInputStream(fileIn,decompressor), job, this.recordDelimiterBytes);		
		        filePosition = fileIn;}		  
	    //非压缩文件
	    else {
		    fileIn.seek(start);
		    in = new SplitLineReader(fileIn, job, this.recordDelimiterBytes);
		    filePosition = fileIn;}

		// If this is not the first split, we always throw away first record
		// because we always (except the last split) read one extra line in
		// next() method.
		if (start != 0) {
		    start += in.readLine(new Text(), 0, maxBytesToConsume(start));		
		this.pos = start;

	private long getFilePosition() throws IOException {
	    long retVal;
	    if (isCompressedInput && null != filePosition) {
	        retVal = filePosition.getPos();
	    } else {
	        retVal = pos;	    
	    return retVal;

	private int skipUtfByteOrderMark() throws IOException {
	    // Strip BOM(Byte Order Mark)
	    // Text only support UTF-8, we only need to check UTF-8 BOM
	    // (0xEF,0xBB,0xBF) at the start of the text stream.
	    int newMaxLineLength = (int) Math.min(3L + (long) maxLineLength,Integer.MAX_VALUE);

	    int newSize = in.readLine(value, newMaxLineLength, maxBytesToConsume(pos));
	    // Even we read 3 extra bytes for the first line,
	    // we won't alter existing behavior (no backwards incompat issue).
	    // Because the newSize is less than maxLineLength and
	    // the number of bytes copied to Text is always no more than newSize.
	    // If the return size from readLine is not less than maxLineLength,
	    // we will discard the current line and read the next line.
	    pos += newSize;
	    int textLength = value.getLength();
	    byte[] textBytes = value.getBytes();
	    //只支持UTF8编码的文本，UTF8编码的文件的开头3个字节是(0xEF,0xBB,0xBF)
	    if ((textLength >= 3) && (textBytes[0] == (byte)0xEF) &&
	          (textBytes[1] == (byte)0xBB) && (textBytes[2] == (byte)0xBF)) {
	        // find UTF-8 BOM, strip it.
	        LOG.info("Found UTF-8 BOM and skipped it");
	        textLength -= 3;
	        newSize -= 3;
	        if (textLength > 0) {
	            // It may work to use the same buffer and not do the copyBytes
	            textBytes = value.copyBytes();
	            value.set(textBytes, 3, textLength);
	        } else {
	            value.clear();}}
	    return newSize;


	public boolean nextKeyValue() throws IOException {
	    if (key == null) {
	        key = new LongWritable();}
	    key.set(pos);
	    if (value == null) {
	        value = new Text();}
	    int newSize = 0;
	    // We always read one extra line, which lies outside the upper
	    // split limit i.e. (end - 1)
	    while (getFilePosition() <= end || in.needAdditionalRecordAfterSplit()) {
	        //下面应该用getFilePosition么？
	        if (pos == 0) {
	            newSize = skipUtfByteOrderMark();
	        } else {
	            newSize = in.readLine(value, maxLineLength, maxBytesToConsume(pos));
	            pos += newSize;}
    
	        if ((newSize == 0) || (newSize < maxLineLength)) {
	            break;}
    
	        // line too long. try again
	        LOG.info("Skipped line of size " + newSize + " at pos " + (pos - newSize));}

	    if (newSize == 0) {
	        key = null;
	    	value = null;
	    	return false;
	    } else {
	    	return true;}}

//支持 seeking的流
interface Seekable 
	//跳到指定位置，下次调用read广法会从跳到的位置开始读，不能跳到文件尾以后的位置
	void seek(long pos)

	//返回当前读入位置离文件头的偏移量
	long getPos() 

	//没看懂
	boolean seekToNewSource(long targetPos)