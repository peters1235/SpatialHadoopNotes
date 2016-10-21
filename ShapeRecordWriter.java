interface ShapeRecordWriter<S extends Shape>
	// Writes the given shape to the file to all cells it overlaps with
	write(NullWritable dummy, S shape)

	//将shape写入指定的分区
	write(int cellId, S shape)

	//另一种形式，仍然只写入一个分区
	write(CellInfo cellInfo, S shape) 

	/* Sets a stock object used to serialize/deserialize objects when written to
	stock object 类似于模板对象的概念，它提供类型，再用传入fromText的 Text
	就可以构造出指定类型的对象
	*/

	setStockObject(S shape);
	
	/**
	 * Closes this writer
	 * @param progressable To report the progress if the closing process takes
	 *  a very long time.
	*/
	close(Progressable progressable)