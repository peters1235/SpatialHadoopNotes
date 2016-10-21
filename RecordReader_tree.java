package org.apache.hadoop.mapreduce;

abstract class RecordReader<KEYIN, VALUEIN> implements Closeable
	public abstract void initialize(InputSplit split,TaskAttemptContext context)

	boolean nextKeyValue() 

	KEYIN getCurrentKey()

	VALUEIN getCurrentValue()

	//0 -1 
	getProgress()

	//Close the record reader.
	close()

	//子类
	SpatialHadoop：
		HDFRecordReader
		RTreeRecordReader3
		SampleRecordReaderFlat
		SampleRecordReaderGeneral
		SpatialRecordReader3
			//无子类




package org.apache.hadoop.mapred;
public interface RecordReader<K, V> {
	//先调用createKey createVlaue 生成两个对象，再把这两个对象传入next中，解析出新的值，
	//在外头再使用新生成的对象进行处理
	boolean next(K key, V value) throws IOException;

	K createKey();

	V createValue();

	long getPos() 

	void close()

	float getProgress()
 	
 	//子类
 	SpatialHadoop：
 		BinaryRecordReader 
 		BlockRecordReader 
 		RandomShapeGenerator
 		SpatialRecordReader
 			RTreeRecordReader
 			ShapeArrayRecordReader
 			ShapeIterRecordReader
 			ShapeLineRecordReader
 			ShapeRecordReader

 	Hadoop：
 		CombineFileRecordReader<K, V>
 		CombineFileRecordReaderWrapper<K, V>
 		DBRecordReader
 		DBRecordReaderWrapper<T extends DBWritable>
 		FixedLengthRecordReader
 		KeyValueLineRecordReader
 		LineRecordReader
 		PipesDummyRecordReader
 		SequenceFileAsBinaryRecordReader		
		SequenceFileAsTextRecordReader
		SequenceFileRecordReader
		TrackedRecordReader
		ComposableRecordReader