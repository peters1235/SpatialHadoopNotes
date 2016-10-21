STRPartitioner extends Partitioner	
	private double[] xSplits; //数据在x方向分n个区的话，每个区的X方向的右边界
	//每个分区的y值上边界
	private double[] ySplits;

	createFromPoints(Rectangle mbr, Point[] points, int capacity)
		//先将点按X轴排序
		Arrays.sort(points, new Comparator<Point>() {
		  @Override
		  public int compare(Point a, Point b) {
		    return a.x < b.x? -1 : (a.x > b.x? 1 : 0);
		  }});
		//分区数目
		int numSplits = (int) Math.ceil((double)points.length / capacity);


		//计算格网的行列数
		GridInfo gridInfo = new GridInfo(mbr.x1, mbr.y1, mbr.x2, mbr.y2);
		gridInfo.calculateCellDimensions(numSplits);
		this.columns = gridInfo.columns;
		this.rows = gridInfo.rows;
		this.xSplits = new double[columns];
		this.ySplits = new double[rows * columns];

		int prev_quantile = 0;

		//从左下角开始，x向右，y向上 逐列存储 ，记录每个单元格的y值的上限
		this.ySplits = new double[rows * columns];

		/* 
			估算的索引之后的数据量的大小 / HDFS块的大小 = 分区的数目
		
		待索引的数据（原始数据或者采样后的数据）的条数 除以 分区数 = 每分区的记录数
		对于格网索引 
			数据条数 / 每个分区的记录数 = 单元格数  ， 
			单元格数 开根号 = 格网的行列数 ， 
			根据单元格行列数  和 数据的空间范围 可计算每个单元格的空间范围

			最终每个单元格的空间范围相等

		对于R树索引  
			数据条数 / 每个分区的记录数 = 单元格数，
			以单元格形状尽量接近正方形为标准来计算出 格网行列数 col row

			数据条数 / 列数 = 每列的数据数 -- 
			先将整个格网空间 按列数 分成一条一条 ，每条中的数据条数相等，在每条内 再 水平 切分成 row 行，且保证每一行的数据条数相等


			最终保证每个每个单元格的要素数目相等
			
			10000 个点 分100个区的话，先将点 按X排序，每1000条数据 一组， 这1000条数据内部 再按 Y坐标排序 ，每100条数据 一组，
			 这样就把10000 条数据 分到100个组里去了
		*/
		//逐列处理所有数据
		for (int column = 0; column < columns; column++) {
			int col_quantile = (column + 1) * points.length / columns;

			this.xSplits[column] = col_quantile == points.length ? mbr.x2 : points[col_quantile-1].x;


			 (prev_quantile * (rows - (row+1)) + col_quantile * (row+1)) / rows

			 prev_quantile  - prev_quantile*(row+1))/rows + col_quantile * (row+1) / rows


	void write(DataOutput out)

	void readFields(DataInput in)

	void overlapPartitions(Shape shape, ResultCollector<Integer> matcher)

	int overlapPartition(Shape shape)

	CellInfo getPartitionAt(int index) {


	CellInfo getPartition(int id) {