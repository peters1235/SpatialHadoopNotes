class ShapeIterRecordReader extends SpatialRecordReader<Rectangle, ShapeIterator>
	/*在构造函数中
	this.shape = OperationsParams.getShape(conf, "shape");
	*/
	private Shape shape;

	next(Rectangle key, ShapeIterator shapeIter)
		boolean element_read = nextShapeIter(shapeIter);
		key.set(cellMbr); // Set the cellInfo for the last block read
		return element_read;

	public Rectangle createKey() {
	    return new Rectangle();


	public ShapeIterator createValue() {
		ShapeIterator shapeIter = new ShapeIterator();
		shapeIter.setShape(shape);
		return shapeIter;