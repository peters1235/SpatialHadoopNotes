//两个Split，求笛卡尔积
abstract class BinaryRecordReader<K extends Writable, V extends Writable>    
    implements RecordReader<PairWritable<K>, PairWritable<V>>