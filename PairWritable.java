//存储一对可序列化的对象
class PairWritable<T extends Writable> implements Writable 
	public T first;
	public T second;

	public void write(DataOutput out) throws IOException {
	    first.write(out);
	    second.write(out);