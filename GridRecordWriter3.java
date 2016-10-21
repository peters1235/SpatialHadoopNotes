//输出Rectangle 和 Shape
/**
 *This record writer does not replicate the
 * given shape to partition (i.e., write it only to the given partition). If
 * the provided rectangle (key) does not match any of the existing partitions,
 * a new partition is created with the given boundaries.
*/
class GridRecordWriter3<S extends Shape>
extends edu.umn.cs.spatialHadoop.core.GridRecordWriter<S> implements RecordWriter<Rectangle, S> {
