//key 为null
//shpae 复制到相交的所有单元格中
class GridRecordWriter2<S extends Shape>
extends edu.umn.cs.spatialHadoop.core.GridRecordWriter<S> implements RecordWriter<NullWritable, S> 

