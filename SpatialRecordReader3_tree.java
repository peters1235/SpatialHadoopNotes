SpatialRecordReader3<V extends Shape> extends RecordReader<Partition, Iterable<V>>

    /**The offset to start reading the raw (uncompressed) file*/
	private long start;
  	/**The last byte to read in the raw (uncompressed) file*/
  	private long end;
  
  	/**The path of the input file to read*/  	
  	private Path path;

	/** The boundary of the partition currently being read */
	protected Partition cellMBR;

  /**
  * The input stream that reads directly from the input file.
  * If the file is not compressed, this stream is the same as #in.
  * Otherwise, this is the raw (compressed) input stream. This stream is used
  * only to calculate the progress of the input file.
  */
  private FSDataInputStream directIn;
  /** Input stream that reads data from input file */
  private InputStream in;
  /**Determine current position to report progress*/
  private Seekable progressPosition;

  /**Used to read text lines from the input*/
  private LineReader lineReader;

/**The shape used to parse input lines*/
  private V stockShape;

  /**The MBR of the input query. Used to apply duplicate avoidance technique*/
	private Rectangle inputQueryMBR;

  /**
   * Number of bytes read from the input so far. This is used to determine when
   * to stop when reading from the input directly. We canno simply rely on the
   * position of the input file because LineReader might buffer some data in
   * memory without actually processing it.
   */
  private long bytesRead;

  private Counter inputRecordsCounter;

  public void initialize(InputSplit split, TaskAttemptContext context)
  	Configuration conf = context != null ? context.getConfiguration() : new Configuration();
  	
  	initialize(split, conf);
  		FileSplit fsplit = (FileSplit) split;

	    this.path = fsplit.getPath();
	    this.start = fsplit.getStart();
	    this.end = this.start + split.getLength();
	    this.fs = this.path.getFileSystem(conf);
	    this.directIn = fs.open(this.path);
	    codec = compressionCodecFactory.getCodec(this.path);
	    if (codec != null) {
	    	decompressor = CodecPool.getDecompressor(codec);
	    	if (codec instanceof SplittableCompressionCodec) {
	    		// A splittable compression codec, can seek to the desired input pos
      			final SplitCompressionInputStream cIn =
      				((SplittableCompressionCodec)codec).createInputStream(
             			 directIn, decompressor, start, end,
            			  SplittableCompressionCodec.READ_MODE.BYBLOCK);
      			in = cIn;
      			/*After calling createInputStream, the values of start or end might change.
      			 So this method can be used to get the new value of start
      			 */
      			start = cIn.getAdjustedStart();
      			end = cIn.getAdjustedEnd();
      			// take pos from compressed stream as we adjusted both start and end
		        // to match with the compressed file
		        progressPosition = cIn;
		    else
		    	// Non-splittable input, need to start from the beginning
		    	CompressionInputStream cIn = codec.createInputStream(directIn, decompressor);
      			in = cIn;
      			progressPosition = cIn;   
    	else 
    		// Non-compressed file, seek to the desired position and use this stream
    		// to get the progress and position
    		directIn.seek(start);
    		in = directIn;
  			progressPosition = directIn;
  		//到这一步，可分的压缩文件，不可分的，非压缩文件，都能用in来读了
    		
  		//文件里存的GeometryType
  		this.stockShape = (V) OperationsParams.getShape(conf, "shape");
  		this.tempLine = new Text();
  		this.lineReader = new LineReader(in);
  			this(in, DEFAULT_BUFFER_SIZE： 64 * 1024 [0x10000]);	
			    this.in = in;
		    this.bufferSize = bufferSize：DEFAULT_BUFFER_SIZE：;
		    this.buffer = new byte[this.bufferSize];
		    this.recordDelimiterBytes = null;

  		bytesRead = 0;
  		if (this.start != 0) {
        /*start!=0 表示 当前分区不是从文件的开头处开始的，
          那么这个分片的开头有可能是 上一个分片的最后一行的 结尾部分
          这部分，应该在上一个分片已经处理好了，在本分片中不需要处理
        */

  			// Skip until first end-of-line reached
        //读入一行，存入tempLine中，返回读入的字节数
  			bytesRead += lineReader.readLine(tempLine);    			    				
  			
  		//用全局索引做初步过滤用的
  		if (conf.get(SpatialInputFormat3.InputQueryRange) != null) {
	    	// Retrieve the input query range to apply on all records
	    	this.inputQueryRange = OperationsParams.getShape(conf,
	    	    SpatialInputFormat3.InputQueryRange);
	    	this.inputQueryMBR = this.inputQueryRange.getMBR();

      // Check if there is an associated global index to read cell boundaries
	    GlobalIndex<Partition> gindex = SpatialSite.getGlobalIndex(fs, path.getParent());
	    if (gindex == null) {
		    cellMBR = new Partition();
		    cellMBR.filename = path.getName();
		    cellMBR.invalidate();
		    	this.x1 = Double.NaN;
		  } else {
		    // Set from the associated partition in the global index
		    for (Partition p : gindex) {
		      if (p.filename.equals(this.path.getName()))
		        cellMBR = p;
		    }
		  }

		  this.value = new ShapeIterator<V>();
		  value.setShape(stockShape);
		  	//stockShape : rect 之类的
		  	this.shape = shape;
  			this.nextShape = (V) shape.clone();
				if (srr != null && !srr.nextShape(nextShape))
        		nextShape = null;

  /*
  Reads next shape from input and returns true. 
  If no more shapes are left in the split, a false is returned. 
  This function first reads a line by calling the method nextLine(Text) 
  then parses the returned line by calling Shape.fromText(Text) on that line. 
  If no stock shape is set, a NullPointerException is thrown.

  Parameters:
  s A mutable shape object to update with the next value
  Returns:
  true if an object was read; false if end-of-file was reached
  只从shapeIterator中调用这个方法，传入的是shapeIterator中的nextShape，
  这样，就使用SpatialRecordReader读取记录，却把读到的结果存到ShapeIterator中去了

  */
  protected boolean nextShape(V s) 
  	do {
      //Reads the next line from input and return true if a line was read.
      // If no more lines are available in this split, a false is returned.      
      if (!nextLine(tempLine))
     	  //读到的行放入templine
   			while (getPos() <= end) {
				  value.clear();
				  if ((lineLength = lineReader.readLine(value)) <= 0) {
					  return false;
				  // Append the part read from stream to the part extracted from buffer
      		bytesRead += lineLength;   				
      		if (value.getLength() > 1) {
       			// Read a non-empty line. Note that end-of-line character is included
        		return true;
    
        return false;
      s.fromText(tempLine);
    } while (!isMatched(shape:s));
    		// Match with the query
			  if (inputQueryRange != null && (shape == null || !shape.isIntersected(inputQueryRange)))
			    return false;
			  // Check if we need to apply a duplicate avoidance step or not
			  if (!cellMBR.isValid() || inputQueryMBR == null)
			   		return !Double.isNaN(x1);?
			    return true;
			  // Apply reference point duplicate avoidance technique
        /*
        去重的过程是取要素与QueryRange相交区域的左上角点所在的分区 为包含要素的分区
        分区彼此间不重叠，故该点只会落在一个分区中，只输出这个分区对应的记录就完成了去重工作
        */
			  Rectangle shapeMBR = shape.getMBR();
			  double reference_x = Math.max(inputQueryMBR.x1, shapeMBR.x1);
			  double reference_y = Math.max(inputQueryMBR.y1, shapeMBR.y1);
			  return cellMBR.contains(reference_x, reference_y);
          //cellMBR:The boundary of the partition currently being read
    return true; 

  public boolean nextKeyValue()
  	value.setSpatialRecordReader(this);  		
  	return value.hasNext();
			return nextShape != null;

	public Partition getCurrentKey() throws IOException, InterruptedException {
  	return cellMBR;

  public Iterable<V> getCurrentValue() 
	    return value;


public static class ShapeIterator<V extends Shape> implements Iterator<V>, Iterable<V> {    
    public void setSpatialRecordReader(SpatialRecordReader3<V> srr) {
    	this.srr = srr;
    	if (shape != null)
        nextShape = (V) shape.clone();
        //nextShape 得先有一个值，才能传到SpatialRecrodReader的nextShape方法中去取下一个对象
        //所以才非要先给nextShape赋个值，这有点别扭，可能还有别的原因
      if (nextShape != null && !srr.nextShape(nextShape))
        nextShape = null;

    public boolean hasNext() {    	
    	return nextShape != null;

    public V next() {
    	//返回nextShape的当前值，并把nextShape的值往前推一个，
    	if (nextShape == null)
          return null;
      	// Swap Shape and nextShape and read next
      	//为什么要交换？
      	if (!srr.nextShape(nextShape))
          nextShape = null;

      	srr.inputRecordsCounter.increment(1);

      	return shape;


      	Iterator it = null;
      	while(it.hasNext()){
      		Pet p = it.next();
      		//.....
      	}