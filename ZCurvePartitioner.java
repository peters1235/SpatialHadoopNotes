class ZCurvePartitioner extends Partitioner {
	final int Resolution = Integer.MAX_VALUE;

	void createFromPoints(Rectangle mbr, Point[] points, int capacity) 
		this.mbr.set(mbr);
		long[] zValues = new long[points.length];
		for (int i = 0; i < points.length; i++)
		  	zValues[i] = computeZ(mbr, points[i].x, points[i].y);
		createFromZValues(zValues, capacity);

	long computeZ(Rectangle mbr, double x, double y) {
	    int ix = (int) ((x - mbr.x1) * Resolution / mbr.getWidth()); //用全部的整数作一个整数格网？
	    int iy = (int) ((y - mbr.y1) * Resolution / mbr.getHeight());
	    //将空间位置映射到 2147483647  *2147483647 　的格网上去
	    return computeZOrder(ix, iy);

	//天书你好
	long computeZOrder(long x, long y)
		long morton = 0;

		for (long bitPosition = 0; bitPosition < 32; bitPosition++) {
		　　 long mask = 1L << bitPosition;
		　　 morton |= (x & mask) << (bitPosition + 1);
		　　 morton |= (y & mask) << bitPosition;
		　　
		return morton;

	void createFromZValues(final long[] zValues, int capacity)
		Arrays.sort(zValues); //每个点都有一z值，按z值大小排序、

		int numSplits = (int) Math.ceil((double)zValues.length / capacity);
		this.zSplits = new long[numSplits];
		long maxZ = computeZ(mbr, mbr.x2, mbr.y2);

		for (int i = 0; i < numSplits; i++) {
		    int quantile = (int) ((long)(i + 1) * zValues.length / numSplits);
		    this.zSplits[i] = quantile == zValues.length ? maxZ : zValues[quantile];

	//需要继续写，现在只会返回一个分区
	void overlapPartitions(Shape shape, ResultCollector<Integer> matcher) {
	    // TODO match with all overlapping partitions instead of only one
	    int partition = overlapPartition(shape);
	    if (partition >= 0)
	        matcher.collect(partition);

	int overlapPartition(Shape shape) {
	    if (shape == null)
	        return -1;
	    Rectangle shapeMBR = shape.getMBR();
	    if (shapeMBR == null)
	        return -1;
	    // Assign to only one partition that contains the center point
	    Point center = shapeMBR.getCenterPoint();
	    long zValue = computeZ(mbr, center.x, center.y);
	    int partition = Arrays.binarySearch(zSplits, zValue);
	    if (partition < 0)
	        partition = -partition - 1;
	    return partition;