IndexOutputFormat
	public RecordWriter<IntWritable, S> getRecordWriter(TaskAttemptContext task)	  
	    Path file = getDefaultWorkFile(task, "").getParent();
	    return new IndexRecordWriter<S>(task, file);

	static class IndexRecordWriter<S extends Shape> extends RecordWriter<IntWritable, S>
		public IndexRecordWriter(TaskAttemptContext task, Path outPath)
			public IndexRecordWriter(TaskAttemptContext task, String name, Path outPath,Progressable progress)
				//Reducer自己的全局索引文件
				Path masterFilePath = name == null ?
				    new Path(outPath, String.format("_master.%s", sindex)) :
				      new Path(outPath, String.format("_master_%s.%s", name, sindex));
				this.masterFile = outFS.create(masterFilePath);

		void write(IntWritable partitionID, S value)
			int id = partitionID.get();
			if (id < 0) {
				//当前分区已经结束 ？ 什么时候传进来-1？Reducer里，可以看到，处理完一个分区，便传一条这个
				int partitionToClose = -id - 1;
				this.closePartition(partitionToClose);
					//一个分区的数据完结了，应该会这里头生成局部索引
					//全局索引是什么时候由谁生成的？
			else
				OutputStream output = getOrCreateDataOutput(id);

				//写入对象
				tempText.clear(); //将对象写入output文件之前用于将对象序列化的临时对象
				value.toText(tempText);
				byte[] bytes = tempText.getBytes();
				output.write(bytes, 0, tempText.getLength());
				output.write(NEW_LINE);

				//更新分区的信息
				Partition partition = partitionsInfo.get(id);
				partition.recordCount++;
				partition.size += tempText.getLength() + NEW_LINE.length;
				partition.expand(value);

				if (shape == null) //Type of shapes written to the output. Needed to build local indexes
					shape = (S) value.clone();

		/*每个分区用一个OutputStream来完成定稿工作
		  这个流创建之后就保存到列表中，后面再往这个分区中写入数据时直接使用原有的流就行了
		  */
		OutputStream getOrCreateDataOutput(int id)
			OutputStream out = partitionsOutput.get(id);
			if (out == null) {
				//没找到的话新创建一个
				Partition partition = new Partition();

				if (localIndexer == null) {
					//不用建局部索引的话直接在输出文件夹中创建分区文件
					Path path = getPartitionFile(id);
						String format = "part-%05d";
						if (localIndexer != null)
						  format += "."+localIndexer.getExtension();
						Path partitionPath = new Path(outPath, String.format(format, id));
						//还要考虑路径已经存在的情况

						return partitionPath;


					out = outFS.create(path);					
					partition.filename = path.getName();
				else
					//输出到临时文件中，稍后索引之后，再写入输出文件夹
					File tempFile = File.createTempFile(String.format("part-%05d", id), "lindex");
					out = new BufferedOutputStream(new FileOutputStream(tempFile));
					tempFiles.put(id, tempFile);

				//记录分区的数据
				partition.cellId = id;
				// Set the rectangle to the opposite universe so that we can keep
				// expanding it to get the MBR of this partition
				partition.set(Double.MAX_VALUE, Double.MAX_VALUE,
				    -Double.MAX_VALUE, -Double.MAX_VALUE);
				// Store in the hashtables for further user
				partitionsOutput.put(id,  out);
				partitionsInfo.put(id, partition);

			return out;


		void closePartition(final int id)
			final Partition partitionInfo = partitionsInfo.get(id);
			final OutputStream outStream = partitionsOutput.get(id);
			final File tempFile = tempFiles.get(id);

			Thread closeThread = new Thread() {
				public void run() {
					outStream.close();
					if (localIndexer != null) {
						//生成局部索引，然后删除分区的临时文件
						Path indexedFilePath = getPartitionFile(id);
						partitionInfo.filename = indexedFilePath.getName();
						localIndexer.buildLocalIndex(tempFile, indexedFilePath, shape);
						// Temporary file no longer needed
						tempFile.delete();


						//直接截掉了？没看懂
						if (replicated) {
							// If data is replicated, we need to shrink down the size of the
							// partition to keep partitions disjoint
							partitionInfo.set(partitionInfo.getIntersection(partitioner.getPartition(id)));

						//写入全局索引
						synchronized (masterFile) {
						    // Write partition information to the master file
						    masterFile.write(partitionText.getBytes(), 0, partitionText.getLength());
						    masterFile.write(NEW_LINE);
						}

						//作什么用？
						if (!closingThreads.remove(Thread.currentThread())) {
						    throw new RuntimeException("Could not remove closing thread");

						// Start more background threads if needed
						int numRunningThreads = 0;
						for (int i_thread = 0; i_thread < closingThreads.size() &&
						                  numRunningThreads < MaxClosingThreads; i_thread++) {
							//10来行没看懂

			closeThread.setUncaughtExceptionHandler(

			//关闭分区的输出流，停止写入
			partitionsInfo.remove(id);
			partitionsOutput.remove(id);
			tempFiles.remove(id);


			//好像挺重要
			if (closingThreads.size() < MaxClosingThreads) {
			  // Start the thread in the background and make sure it started before
			  // adding it to the list of threads to avoid an exception when other
			  // thread tries to start it after it is in the queue
			  closeThread.start();
			  try {
			    while (closeThread.getState() == State.NEW) {
			      Thread.sleep(1000);
			      LOG.info("Waiting for thread #"+closeThread.getId()+" to start");
			    }
			  } catch (InterruptedException e) {}
			}
			closingThreads.add(closeThread);

	static class IndexerOutputCommitter extends FileOutputCommitter
		void commitJob(JobContext context)
			super.commitJob(context);

			//将所有的master文件合成一个

			//查找master文件			
			FileStatus[] resultFiles = outFs.listStatus(outPath, new PathFilter() {
			  @Override
			  public boolean accept(Path path) {
			    return path.getName().contains("_master");
			  }
			});

			//合并的master文件的存储位置及输出流
			String sindex = conf.get("sindex");
			Path masterPath = new Path(outPath, "_master." + sindex);
			OutputStream destOut = outFs.create(masterPath);

			//master文件有wkt表示方式
			Path wktPath = new Path(outPath, "_"+sindex+".wkt");
			PrintStream wktOut = new PrintStream(outFs.create(wktPath));
			wktOut.println("ID\tBoundaries\tRecord Count\tSize\tFile name");

			Text tempLine = new Text2();
			Partition tempPartition = new Partition();
			final byte[] NewLine = new byte[] {'\n'};

			for (FileStatus f : resultFiles) {
				LineReader in = new LineReader(outFs.open(f.getPath()));
				while (in.readLine(tempLine) > 0) {
					//合并全局索引
					destOut.write(tempLine.getBytes(), 0, tempLine.getLength());
					destOut.write(NewLine);

					//输出分区的WKT字符串 
					tempPartition.fromText(tempLine);
					wktOut.println(tempPartition.toWKT());

				in.close();
				//删掉已合并的文件
				outFs.delete(f.getPath(), false); // Delete the copied file


			x1 y1 x2 y2 cellId recordCount size fileName




