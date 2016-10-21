GridPartitioner
	//格网的左下角点空间坐标
	protected double x, y;
	
	//单元格数
	protected int numTiles;
	
	//单元格行列数
	protected int numColumns, numRows;
	
	//瓦片宽高（空间范围）
	protected double tileWidth, tileHeight;

	numTiles = (int) Math.ceil(points.length / capacity);
		GridInfo gridInfo = new GridInfo(mbr.x1, mbr.y1, mbr.x2, mbr.y2);
		//tileWidth tileHeight 是每个小格子的大小

	public GridPartitioner(Rectangle mbr, int columns, int rows) {
	    this.x = mbr.x1;
	    this.y = mbr.y1;
	    this.numTiles = rows * columns;
	    this.numColumns = columns;
	    this.numRows = rows;
	    this.tileWidth = mbr.getWidth() / columns;
	    this.tileHeight = mbr.getHeight() / rows;

	void createFromPoints(Rectangle mbr, Point[] points, int capacity)
		GridInfo gridInfo = new GridInfo(mbr.x1, mbr.y1, mbr.x2, mbr.y2);
		x = mbr.x1;
		y = mbr.y1;
		numTiles = (int) Math.ceil(points.length / capacity);
			int cols = (int)Math.round(Math.sqrt(numTiles));   
		this.numColumns = gridInfo.columns = Math.max(1, cols);
		this.numRows = gridInfo.rows = (int) Math.ceil(numTiles / gridInfo.columns);
		tileWidth = mbr.getWidth() / gridInfo.columns;
		tileHeight = mbr.getHeight() / gridInfo.rows;

	public void write(DataOutput out) throws IOException {
	    out.writeDouble(x);
	    out.writeDouble(y);
	    out.writeDouble(tileWidth);
	    out.writeDouble(tileHeight);
	    out.writeInt(numTiles);

	//Overlap a shape with partitions and calls a matcher for each overlapping
	void overlapPartitions(Shape shape, ResultCollector<Integer> matcher) {
		Rectangle shapeMBR = shape.getMBR();

		col1 = (int)Math.floor((shapeMBR.x1 - x) / tileWidth);
	    col2 = (int)Math.ceil((shapeMBR.x2 - x) / tileWidth);
	    row1 = (int)Math.floor((shapeMBR.y1 - y) / tileHeight);
	    row2 = (int)Math.ceil((shapeMBR.y2 - y) / tileHeight);

	    for (int col = col1; col < col2; col++)
      		for (int row = row1; row < row2; row++)
        		matcher.collect(getCellNumber(col, row));

    //瓦片序号从左下角，往右上，逐行递增
    int getCellNumber(int col, int row) {
    	return row * numColumns + col;

    //根据中心点的位置，判断Shape应该属于哪个单元格
    int overlapPartition(Shape shape) {    	
    	Rectangle shapeMBR = shape.getMBR();
    	Point centerPoint = shapeMBR.getCenterPoint();
    	int col = (int)Math.floor((centerPoint.x - x) / tileWidth);
    	int row = (int)Math.floor((centerPoint.y - y) / tileHeight);
    	return getCellNumber(col, row);

    public CellInfo getPartition(int partitionID) {
        // Retrieve column and row of the given partition
        int col = partitionID % numColumns;
        int row = partitionID / numColumns;
        return new CellInfo(partitionID, x + col * tileWidth, y + row * tileHeight,
            x + (col + 1) * tileWidth, y + (row + 1) * tileHeight);

    //干同样的事，换个名字而已
    public CellInfo getPartitionAt(int index) {
        return getPartition(index);