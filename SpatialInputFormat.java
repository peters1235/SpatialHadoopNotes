//用的旧版的接口，旧版的RecordReader
abstract class SpatialInputFormat<K, V> extends FileInputFormat<K, V> {
	RecordReader<K, V> getRecordReader(InputSplit split, JobConf job, Reporter reporter)
		//只能处理FileSplit别的类型不支持
		if (split instanceof FileSplit) {
			Constructor<? extends RecordReader> rrConstructor;
			rrConstructor = rrClass.getDeclaredConstructor(constructorSignature);
			rrConstructor.setAccessible(true);
			return rrConstructor.newInstance(new Object [] {job, fsplit});	
