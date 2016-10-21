class GlobalIndex<S extends Shape> implements Writable, Iterable<S>
	S stockShape; //用于反序列化的“模板对象”
	S[] shapes;  //All underlying shapes in no specific order
	boolean compact //本全局索引中的分区是否是其中的要素的最小外包
	boolean replicated //同一要素是否会复制到多个分区？

	public void bulkLoad(S[] shapes) {
	    //先浅拷贝
	    this.shapes = shapes.clone();
	    //再深拷贝
	    for (int i = 0; i < this.shapes.length; i++) {
	    	this.shapes[i] = (S) this.shapes[i].clone();

	void write(DataOutput out)
	    out.writeInt(shapes.length);
	    for (int i = 0; i < shapes.length; i++) {
		    shapes[i].write(out);

	int rangeQuery(Shape queryRange, ResultCollector<S> output) {
	    int result_count = 0;
	    for (S shape : shapes) {
	        if (shape.isIntersected(queryRange)) {
	        	result_count++;
	        	if (output != null) {
	          		output.collect(shape);	         
	    return result_count;

	static<S1 extends Shape, S2 extends Shape>
	      int spatialJoin(GlobalIndex<S1> s1, GlobalIndex<S2> s2,
	          final ResultCollector2<S1, S2> output)

	class SimpleIterator implements Iterator<S> {.

	Iterator<S> iterator() {
	    return new SimpleIterator();

	Rectangle getMBR() {

    int knn(final double qx, final double qy, int k, ResultCollector2<S, Double> output)
    