//写入cellId 和 Shape,一个Shape 只属于一个 单元格

class GridRecordWriter<S extends Shape>
extends edu.umn.cs.spatialHadoop.core.GridRecordWriter<S> implements RecordWriter<IntWritable, S>
	public GridRecordWriter(JobConf job, String name, CellInfo[] cells) throws IOException {
	  super(null, job, name, cells);
	}
	
	@Override
	public void write(IntWritable key, S value) throws IOException {
	  super.write(key.get(), value);
	}

	@Override
	public void close(Reporter reporter) throws IOException {
	  super.close(reporter);