/*本类实现的是旧版 RecordReader接口
 SpatialRecordReader3实现的则是新版的RecordReader接口
 两个版本的使用场合还没分清
 */
abstract class SpatialRecordReader<K, V> implements RecordReader<K, V>
	byte[] buffer //从输入中读到了，还没有完成解析的数据
	in //用于读入数据的流
	long pos start end //读数据的当前、起、止位置
	Rectangle cellMbr //当前分区的mbr

	// 要返回进度的时候用这个接口返回当前位置到文件开头的偏移量	
	private Seekable filePosition;
	//下次读入数据的位置
	
	SpatialRecordReader(Configuration job, long s, long l, Path p)
		this.start = s;
		this.end = s + l;
		this.path = p;
		//输入文件直接读入directIn ，再根据是否是压缩文件将directIn转到in中去

		this.directIn = fs.open(this.path);
		this.blockSize = fs.getFileStatus(this.path).getBlockSize();

		//如果要处理压缩文件，则取得解压流后计算start end等的值
		//代码略去

		initializeReader


	/*
	使用输入流或者文件初始化Reader，
	如果输入文件有全局索引的话，取该分区的MBR
	判断文件是R树索引的文件还是一般文件。R树索引的文件可以视情况跳过R树索引	
	*/
	boolean initializeReader()
		//如果输入的是文件，且该文件有全局索引的话，用全局索引和本分区的名字查得
		//分区的mbr，并记入cellMbr中

		//从输入中读取8个字节，与SpatialSite.RTreeFileMarkerB 比较，
		buffer = new byte[8];
		int bufferLength = in.read(buffer);
		//还考虑一下读到的小于8个字节

		//相等的话
		//则本文件是R树文件，设置blockType，并且跳过这8个字节
		if (buffer != null && Arrays.equals(buffer, SpatialSite.RTreeFileMarkerB)) {
			blockType = BlockType.RTREE;
			pos += 8;
			// Ignore the signature
			buffer = null;
		else
			否则本文件为一般堆文件，开头的8个字节还得用得上
			blockType = BlockType.HEAP;
			lineReader = new LineReader(in);

			// Skip the first line unless we are reading the first block in file
			// For globally indexed blocks, never skip the first line in the block
			//跳过非首块的首行是因为该行的内容在上一个块里已经处理了？
			boolean skipFirstLine = getPos() != 0;
			if (buffer != null && skipFirstLine) {
				//buffer 中有换行符的话，eol == 换行符"\r\n"(不知道只有\r 或者\n 可不可以)后面一个字符的索引
				//buffer 中没有换行符的话，eol == buffer.length
				//作判断的基本玩法是，对的，对的，。。。对的，错的，访问到一次错的之后，再回个头，才能取到全部的对的
				int eol = RTree.skipToEOL(buffer, 0);

				// If we found an end of line in the buffer, we do not need to skip
				// a line from the open stream. This happens if the EOL returned is
				// beyond the end of buffer and the buffer is not a complete line
				// by itself
				/*
				判断条件似乎有问题 \r \n 如果只有一个呢？
				**/
				boolean skip_another_line_from_stream = eol >= buffer.length && buffer[buffer.length - 1] != '\n';
				if (eol < buffer.length) {
					//buffer的中间有\r\n，而且后续还有内容，则把换行符前头的舍弃，
					//后头的内容放进buffer中
				else
					/*
					buffer中没换行符，或者最末尾才是换行符，
					则buffer中的内容全部舍弃
					pos += buffer.length;
					 buffer = null;
					*/
				if (skip_another_line_from_stream) {
					//缓存中没有找到换行符，则继续读入直到找到换行符
					pos += lineReader.readLine(tempLine, Integer.MAX_VALUE, (int)(end - pos));

					if (pos >= end) {
						//特殊情况，整个当前Split只是一个超级长的行的一部分
					  // Special case when the whole split is in the middle of a line
					  // Skip the split
					  // Increase position beyond end to ensure the next call to
					  // nextLine would return false
					  pos++;


	boolean nextLine(Text value)
		//R树的话，跳过索引往下读
		if (blockType == BlockType.RTREE && pos == 8) {
			pos += RTree.skipHeader(in);
			lineReader = new LineReader(in);
		//没到分片尾就一直往下读
		while (getFilePosition() <= end) {
			//不记了，就是读一行，读到value里头

	protected boolean nextShape(Shape s) throws IOException {
	    if (!nextLine(tempLine))
	      return false;
	    s.fromText(tempLine);
	    return true;

	/*
	用nextShape(Shape) 一直读Shape直到
		读到文件尾
		读到的Shape数目大于spatialHadoop.mapred.MaxShapesPerRead，该参数设为-1时，该限制失效
		解析的数据量大于spatialHadoop.mapred.MaxBytesPerRead，同样可通过置-1来取消此限制
	*/
	boolean nextShapes(ArrayWritable shapes) 
		Vector<Shape> vshapes = new Vector<Shape>(); //结果
		Shape stockObject = (Shape) shapes.getValueClass().newInstance();

		long readBytes = 0;
		
		// Read all shapes in this block
		while ((maxShapesInOneRead <= 0 || vshapes.size() < maxShapesInOneRead) &&
		    (maxBytesInOneRead <= 0 || readBytes < maxBytesInOneRead) &&
		    nextShape(stockObject)) {
		  vshapes.add(stockObject.clone());
		  readBytes = getPos() - initialReadPos;
		}

		// Store them in the return value
		shapes.set(vshapes.toArray(new Shape[vshapes.size()]));
		
		return !vshapes.isEmpty();

	protected boolean nextShapeIter(ShapeIterator iter) throws IOException {
	    iter.setSpatialRecordReader((SpatialRecordReader<?, ? extends Shape>) this);
	    return iter.hasNext();

	public static class ShapeIterator implements Iterator<Shape>, Iterable<Shape> {
		protected Shape shape;  //已经被传出来的，当前的Shape
		protected Shape nextShape; //只是读出来，还没有被传递出去的，下次要传出去的Shape
		private SpatialRecordReader<?, ? extends Shape> srr;

		//设置srr并且将nextShape赋好值
		public void setSpatialRecordReader(SpatialRecordReader<?, ? extends Shape> srr) {
			if (shape != null)
			    nextShape = shape.clone();
			if (nextShape != null && !srr.nextShape(nextShape))
			    nextShape = null;

		/*给shape赋值，再传给nextShape，nextShape 不为空时，再用nextShape和Reader去分片中去解析shape，
		然后nextShape传给shape， nextShape再去分片中解析，不断循环
		*/
		setShape(Shape shape) {
			this.shape = shape;
			this.nextShape = shape.clone();
			try {
			  if (srr != null && !srr.nextShape(nextShape))
			      nextShape = null;

		public boolean hasNext() {
			return nextShape != null;

		public Shape next() {
			//把nextShape的当前值传给shape，
			Shape temp = shape;
			shape = nextShape;
			nextShape = temp;
			
			//nextShape再解析一行文本
			if (!srr.nextShape(nextShape))
			  nextShape = null;
			return shape;

		public Iterator<Shape> iterator() {
	  	    return this;

	  	//从分片中读取R树对象
	  	boolean nextRTree(RTree<? extends Shape> rtree)
	  		if (blockType == BlockType.RTREE) {
	  		  if (getPos() != 8)
	  		    return false;
	  		  // Signature was already read in initialization.
	  		  buffer = null;
	  		  DataInput dataIn = in instanceof DataInput?
	  		      (DataInput) in : new DataInputStream(in);
	  		  rtree.readFields(dataIn);
	  		  pos++;
	  		  return true;


interface Seekable {
	seek(long pos)

	long getPos()
	/* Seeks a different copy of the data.  Returns true if 
	found a new source, false otherwise.*/
	boolean seekToNewSource(long targetPos)