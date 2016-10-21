package edu.umn.cs.spatialHadoop.core; 
/*
注释上说是所有RecordWriter的基类，事实上，就不是IndexRecordWriter的基类
跟IndexRecordWriter的功能很像
*/
GridRecordWriter<S extends Shape> implements ShapeRecordWriter<S>
	//子类 
	RTreeGridRecordWriter
		package edu.umn.cs.spatialHadoop.mapred;
			RTreeGridRecordWriter

	package edu.umn.cs.spatialHadoop.mapred;
		GridRecordWriter  
		GridRecordWriter2
		GridRecordWriter3

	/**The spatial boundaries for each cell
	每个cell（分区）的空间范围
	*/
	protected CellInfo[] cells;

	/**Expand MBR of each cell to totally cover all of its contents
	是否根据分区中的数据的空间范围来扩展分区本身的MBR？*/
	private boolean expand;

	/**Pack MBR of each cell around its content after it's written to disk
	如果分区中的要素比较小的话， 这个分区实际的MBR可以根据其内含的要素的MBR 缩小一些
				//expand是扩大分区的MBR， pack是缩小分区的MBR
	*/
  	protected boolean pack;

  	GridRecordWriter(Path outDir, JobConf job, String prefix,CellInfo[] cells)
  		if (job != null) {
  		    this.sindex = job.get("sindex", "heap");
  		    this.pack = PackedIndexes.contains(sindex);
  		    this.expand = ExpandedIndexes.contains(sindex);
  		this.prefix = prefix;
  		this.outDir = outDir;
  		this.jobConf = job;

  		if (cells != null) {
  			//传入了分区列表，不传入的话，看意思，就只用一个分区表示全部要输出的数据

  			int highest_index = 0； //对应最大的cell 的cellId
  			for (CellInfo cell : cells) {
  			  if (cell.cellId > highest_index)
  			    highest_index = (int) cell.cellId;

  			//也生成全局索引文件？跟那个啥一样的？
  			masterFile = fileSystem.create(getMasterFilePath());
  												String extension = sindex;
  												return getFilePath("_master."+extension);

  			this.cells = new CellInfo[highest_index + 1];
  			//传入的cell的cellId作为其在本对象的cell中的索引
  			for (CellInfo cell : cells)
  			    this.cells[(int) cell.cellId] = cell;

  			// Prepare arrays that hold cells information
  			intermediateCellStreams = new OutputStream[this.cells.length];
  			intermediateCellPath = new Path[this.cells.length];
  			cellsMbr = new Rectangle[this.cells.length];
  			// Initialize the counters for each cell
  			intermediateCellRecordCount = new int[this.cells.length];
  			intermediateCellSize = new int[this.cells.length];
  		else
  			//跟上面的if条件的最后5行类似，只是数组长都改为1
  			intermediateCellStreams = new OutputStream[1];
  			。。。

  		//初始化每个分区的范围，以后每往分区中写入一个要素，就调用分区范围的expand方法扩展一次分区的MBR
  		for (int i = 0; i < cellsMbr.length; i++) {
  		    cellsMbr[i] = new Rectangle(Double.MAX_VALUE, Double.MAX_VALUE,
  		        -Double.MAX_VALUE, -Double.MAX_VALUE);
  		}

	synchronized void write(NullWritable dummy, S shape)
		if (cells == null) {
			// No cells. Write to the only stream open to this file
			writeInternal(0, shape);
		else
			Rectangle mbr = shape.getMBR();
			//找到shape所属的分区（可能有多个），写入
			for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			    if (cells[cellIndex] != null && mbr.isIntersected(cells[cellIndex])) {
				    writeInternal(cellIndex, shape);

	/*
	* Write the given shape to a specific cell. The shape is not replicated to any other cells.
	* It's just written to the given cell. This is useful when shapes are already assigned
	* and replicated to grid cells another way, e.g. from a map phase that partitions.
	在别处已经确定了shape 应该属于哪个cell，调用这个方法将shape写入指定的cell
	不会复制到别的cell中
	*/
	write(CellInfo cellInfo, S shape)
		for (int i_cell = 0; i_cell < cells.length; i_cell++) {
		    if (cellInfo.equals(cells[i_cell]))
			    write(i_cell, shape); //就是调用writeInternal

	void write(int cellId, S shape) {
	    writeInternal(cellId, shape);

	synchronized void writeInternal(int cellIndex, S shape)
		if (cellIndex < 0) {
			//类似于建局部索引那样，来个close看是不是干的差不多的事
		    // A special marker to close a cell
		    closeCell(-cellIndex);
		    return;
		
		cellsMbr[cellIndex].expand(shape.getMBR());

		//把shape转text,text再写入文件流
		text.clear();
		shape.toText(text);

		OutputStream cellStream = getIntermediateCellStream(cellIndex);
		cellStream.write(text.getBytes(), 0, text.getLength());
		cellStream.write(NEW_LINE);
		intermediateCellSize[cellIndex] += text.getLength() + NEW_LINE.length;
		intermediateCellRecordCount[cellIndex]++;

	/*
	/**
	 * Returns an output stream in which records are written as they come before
	 * they are finally flushed to the cell file.
	要写入每个分区的数据先写入一个临时流，可能跟之前的IndexOutputFormat一样，
	 等close的时候再建个局部索引，把临时流的内容再写入 最终的输出文件夹
	 同样的 格网索引的文件 不需要使用临时流，直接写入最终的成果就行
	 */
	OutputStream getIntermediateCellStream(int cellIndex)
		if (intermediateCellStreams[cellIndex] == null) {
		    // For grid file, we write directly to the final file
		    //别的类型的文件应该要重写这个方法吧，把临时流和最终流分开
		    intermediateCellPath[cellIndex] = getFinalCellPath(cellIndex);
		    intermediateCellStreams[cellIndex] = createFinalCellStream(intermediateCellPath[cellIndex]);
		}
		return intermediateCellStreams[cellIndex];


	Path getFinalCellPath(int cellIndex)
		do{
			String filename = counter == 0 ? String.format("data_%05d", cellIndex)
			    : String.format("data_%05d_%d", cellIndex, counter);
			//如果是要压缩再输出的话，文件名还要加上压缩方式对应的扩展名
			boolean isCompressed = jobConf != null && FileOutputFormat.getCompressOutput(jobConf);
		    if (isCompressed) {
		       Class<? extends CompressionCodec> codecClass =
		           FileOutputFormat.getOutputCompressorClass(jobConf, GzipCodec.class);
		       // create the named codec
		       CompressionCodec codec = ReflectionUtils.newInstance(codecClass, jobConf);
		       filename += codec.getDefaultExtension();
		    }
			//并使用counter来确保文件不存在	
			path = getFilePath(filename);
			counter++;	
		} while (fileSystem.exists(path));
		return path;


	//给文件名加上前缀，如果指定了输出路径就在输出路径指定Path，否则在Task的临时目录指定位置
	Path getFilePath(String filename)
		if (prefix != null)
		    filename = prefix + "_" + filename;
		return outDir != null ? new Path(outDir, filename) : 
		    FileOutputFormat.getTaskOutputPath(jobConf, filename);

	//根据是否压缩，来创建文件输出流
	OutputStream createFinalCellStream(Path cellFilePath)
		OutputStream cellStream;
		boolean isCompressed = jobConf != null && FileOutputFormat.getCompressOutput(jobConf);
		
		if (!isCompressed) {
		  // Create new file
		  cellStream = fileSystem.create(cellFilePath, true,
		      fileSystem.getConf().getInt("io.file.buffer.size", 4096),
		      fileSystem.getDefaultReplication(cellFilePath), this.blockSize);
		} else {
		  Class<? extends CompressionCodec> codecClass =
		      FileOutputFormat.getOutputCompressorClass(jobConf, GzipCodec.class);
		  // create the named codec
		  CompressionCodec codec = ReflectionUtils.newInstance(codecClass, jobConf);

		  // Open a stream to the output file
		  cellStream = fileSystem.create(cellFilePath, true,
		      fileSystem.getConf().getInt("io.file.buffer.size", 4096),
		      fileSystem.getDefaultReplication(cellFilePath), this.blockSize);

		  // Encode the output stream using the codec
		  cellStream = new DataOutputStream(codec.createOutputStream(cellStream));
		}

		return cellStream;

//从这里开始是close相关的了，没有#region 真麻烦。。。

	synchronized void close(Progressable progressable)
		for (int cellIndex = 0; cellIndex < intermediateCellStreams.length; cellIndex++) {
		  	if (intermediateCellStreams[cellIndex] != null) {
		        closeCell(cellIndex);

		while (!closingThreads.isEmpty()) {		  
		    Thread t = closingThreads.get(0);
		    switch (t.getState()) {
			    case NEW: t.start(); break;
			    case TERMINATED: closingThreads.remove(0); break;
			    default:
			      // Use limited time join to indicate progress frequently
			      t.join(10000);
			      	  /*
						Waits at most millis milliseconds for this thread to die. A timeout of 0 means to wait forever. 

						This implementation uses a loop of this.wait calls conditioned on this.isAlive. .
						As a thread terminates the this.notifyAll method is invoked. 
						It is recommended that applications not use wait, notify, or notifyAll on Thread instances.
						在C#里见过这个，真怀念C#和VS
			      	  */
			    }
		    // Indicate progress. Useful if closing a single cell takes a long time
		    if (progressable != null)
		      progressable.progress();		  
		}
		
		if (masterFile != null)
		    masterFile.close();

	closeCell(int cellIndex)
		//cells== null时，为什么新建cellInfo 要用cellIndex+1 来做ID？
		CellInfo cell = cells != null? cells[cellIndex] : new CellInfo(cellIndex+1, cellsMbr[cellIndex]);
		if (expand)
		  	cell.expand(cellsMbr[cellIndex]);
		if (pack)
			//应该是分区里的要素的总的MBR与分区的MBR求交，取小的，
			//也就是说，如果分区中的要素比较小的话， 这个分区实际的MBR可以根据其内含的要素的MBR 缩小一些
			//expand是扩大分区的MBR， pack是缩小分区的MBR
		    cell = new CellInfo(cell.cellId, cell.getIntersection(cellsMbr[cellIndex]));

		closeCellBackground(intermediateCellPath[cellIndex],
		    getFinalCellPath(cellIndex), intermediateCellStreams[cellIndex],
		    masterFile, cell, intermediateCellRecordCount[cellIndex], intermediateCellSize[cellIndex]);
		//完成清理工作
		cellsMbr[cellIndex] = new Rectangle(Double.MAX_VALUE, Double.MAX_VALUE,
		    -Double.MAX_VALUE, -Double.MAX_VALUE);
		intermediateCellPath[cellIndex] = null;
		intermediateCellStreams[cellIndex] = null;
		intermediateCellRecordCount[cellIndex] = 0;
		intermediateCellSize[cellIndex] = 0;

	/*
	这个方法是什么时候调用的？
	*/
	closeCellBackground(final Path intermediateCellPath,
	      final Path finalCellPath, final OutputStream intermediateCellStream,
	      final OutputStream masterFile, final CellInfo cellMbr,
	      final long recordCount, final long cellSize)

		Thread closingThread = new Thread() {
			public void run() {
				Path finalfinalCellPath = flushAllEntries(intermediateCellPath,intermediateCellStream, finalCellPath);

				// Write an entry to the master file
				if (masterFile != null) {
					//似曾相识。。
					Partition partition = new Partition(finalfinalCellPath.getName(), cellMbr);
					partition.recordCount = recordCount;
					partition.size = cellSize;
					Text line = partition.toText(new Text());
					masterFile.write(line.getBytes(), 0, line.getLength());
					masterFile.write(NEW_LINE);

		closingThreads.add(closingThread);

		//移除已经结束的线程
		while (!closingThreads.isEmpty() &&
			    closingThreads.get(0).getState() == Thread.State.TERMINATED) {
		    closingThreads.remove(0);

		// Start first thread (if exists)
		if (!closingThreads.isEmpty() && closingThreads.get(0).getState() == Thread.State.NEW)
		  	closingThreads.get(0).start();


	/*
	将中间结果写入最终结果，应该还带一步建局部索引什么的吧
	*/
	Path flushAllEntries(Path intermediateCellPath,OutputStream intermediateCellStream, Path finalCellPath)
		//这没干活，应该是留到子类里去了吧。
		// For global-only indexed file, the intermediate file is the final file 这句话，指的是用格网索引的文件吧？
		//还有别的文件？		
		intermediateCellStream.close();
		return intermediateCellPath;

