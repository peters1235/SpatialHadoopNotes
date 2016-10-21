/*
其中的方法基本都是重载GridRecordWriter，没有添加什么新的功能*/
class RTreeGridRecordWriter<S extends Shape> extends GridRecordWriter<S> 
	/*
	Whether to use the fast mode for building RTree or not. 
	RTree.bulkLoadWrite(byte [], int, int, int, java.io.DataOutput, Shape, boolean)
	*/
	boolean fastRTree;

	/**The maximum storage (in bytes) that can be accepted by the user*/
	protected int maximumStorageOverhead;

	RTreeGridRecordWriter(Path outDir, JobConf job, String prefix,CellInfo[] cells)
		super(outDir, job, prefix, cells);

		this.fastRTree = conf.get(SpatialSite.RTREE_BUILD_MODE, "fast").equals("fast");
		this.maximumStorageOverhead =
	
		     (int) (conf.getFloat(SpatialSite.INDEXING_OVERHEAD, 0.1f) * blockSize);
	/*
	写入记录之后还能放得下R树，就写，不然的话，就提前结束本分区，
	存在一个分区的数据要写入多个块的情况咯？ 怎么写？怎么读出来？*/	     
	synchronized void writeInternal(int cellIndex, S shape)
		if (cellIndex < 0) {
		    // This indicates a close cell command
		    super.writeInternal(cellIndex, shape);
		    return;

		text.clear();
		shape.toText(text);

		int new_data_size =
		    intermediateCellSize[cellIndex] + text.getLength() + NEW_LINE.length;

		int bytes_available = (int) (blockSize - 8 - new_data_size);
		if (bytes_available < maximumStorageOverhead) {
			int degree = 4096 / RTree.NodeSize;

			//Calculate the storage overhead required to build an RTree for the given number of nodes.
			int rtreeStorageOverhead = RTree.calculateStorageOverhead(intermediateCellRecordCount[cellIndex], degree);

			if (bytes_available< rtreeStorageOverhead) {
				LOG.info("Early flushing an RTree with data "+
				    intermediateCellSize[cellIndex]);
				// Writing this element will get the degree above the threshold
				// Flush current file and start a new file
				super.writeInternal(-cellIndex, null);

		super.writeInternal(cellIndex, shape);

	Path flushAllEntries(Path intermediateCellPath, OutputStream intermediateCellStream, Path finalCellPath)
		intermediateCellStream.close();

		//把中间结果文件的内容读回到内存中
		byte[] cellData = new byte[(int) new File(intermediateCellPath.toUri()
		    .getPath()).length()];
		InputStream cellIn = new FileInputStream(intermediateCellPath.toUri()
		    .getPath());
		cellIn.read(cellData);
		cellIn.close();

		//用内存中的数据建好R树之后，写入最终结果文件中，并返回文件的路径
		DataOutputStream cellStream = (DataOutputStream) createFinalCellStream(finalCellPath);
		cellStream.writeLong(SpatialSite.RTreeFileMarker);
		int degree = 4096 / RTree.NodeSize;
		RTree.bulkLoadWrite(cellData, 0, cellData.length, degree, cellStream, stockObject.clone(), fastRTree);
		cellStream.close();
		cellData = null; // To allow GC to collect it
		
		return finalCellPath;

	/*生成的文件的后缀是rtree*/
	OutputStream getIntermediateCellStream(int cellIndex)
		if (intermediateCellStreams[cellIndex] == null) {
		  // For grid file, we write directly to the final file
		  File tempFile = File.createTempFile(String.format("%05d", cellIndex), "rtree");
		  intermediateCellStreams[cellIndex] = new BufferedOutputStream(
		      new FileOutputStream(tempFile));
		  intermediateCellPath[cellIndex] = new Path(tempFile.getPath());
		}
		return intermediateCellStreams[cellIndex];

	/*同样是为了添加rtree后缀*/
	Path getFinalCellPath(int cellIndex) throws IOException {
	    Path finalCellPath = super.getFinalCellPath(cellIndex);
	    return new Path(finalCellPath.getParent(), finalCellPath.getName()+".rtree");