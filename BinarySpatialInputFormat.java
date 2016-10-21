class IndexedRectangle extends Rectangle {
	int index;
	
abstract class BinarySpatialInputFormat<K extends Writable, V extends Writable>
    extends FileInputFormat<PairWritable<K>, PairWritable<V>> 