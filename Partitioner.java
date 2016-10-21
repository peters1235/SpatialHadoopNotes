// An interface for spatially partitioning data into partitions.
/*
分区呢，就是，用Z曲线 ，希尔伯特曲线，或者STR算法 ，把空间相邻的数据找出来，
然后以多少个为一组，放到一个分区里，
分完区之后，一个分区里的数据都是空间相邻的，这就是基本的空间索引了，

在分区里，可以再使用R树或者R+树建局部索引
*/
public abstract class Partitioner :Writable
	//Partitioner类
	private static final String PartitionerClass = "Partitioner.Class";
	//Partitioner对象的序列化文件
	private static final String PartitionerValue = "Partitioner.Value";

	/*根据输入的参数 生成Partitioner
	* mbr - the minimal bounding rectangle of the input space
   *  points - the points to be partitioned
   *  capacity - maximum number of points per partition
   */
	abstract void createFromPoints(Rectangle mbr, Point[] points, int capacity)


	/*
	查找与输入的Shape相交的分区，并对每个分区调用matcher函数	
	*/
	abstract void overlapPartitions(Shape shape, ResultCollector<Integer> matcher);

	/**
   * Returns only one overlapping partition. If the given shape overlaps more
   * than one partitions, the partitioner returns only one of them according to
   * its own criteria. If it does not overlap any partition, it returns -1
   * which is an invalid partition ID.
   */
	abstract int overlapPartition(Shape shape);


	/*
	 按分区ID查找分区
	*/
	abstract CellInfo getPartition(int partitionID);


	/*
	   * Returns the detail of a partition given its index starting at zero
   * and ending at partitionCount() - 1
   */
	public abstract CellInfo getPartitionAt(int index);

	abstract int getPartitionCount();

	//Sets the class and value of a partitioner in the given job
	static void setPartitioner(Configuration conf, Partitioner partitioner)
		conf.setClass(PartitionerClass, partitioner.getClass(), Partitioner.class);			
		tempFile = new Path("cells_"+(int)(Math.random()*1000000)+".partitions");			
		FSDataOutputStream out = fs.create(tempFile);
		partitioner.write(out); //输入到临时文件，然后添加到Cache中
			GridPartitioner
				out.writeDouble(x);
    			out.writeDouble(y);
			    out.writeDouble(tileWidth);
			    out.writeDouble(tileHeight);
			    out.writeInt(numTiles);
		DistributedCache.addCacheFile(tempFile.toUri(), conf);
		conf.set(PartitionerValue, tempFile.getName());

	public static Partitioner getPartitioner(Configuration conf) {
		/*
		用PartitionerClass的值指向的类型构造对象实例
		用PartitionerValue的值 反序列化出 Partitioner对象的字段值
		*/