//SpatialHadoop 相关的配置, 与OperationsParam的关系？
SpatialSite
	//以常量的形式规定了许多参数的名字
	public static final String ColumnBoundaries = "SpatialSite.ReduceSpaceBoundaries";

	//读入参数配置文件
	static {
	  // Load configuration from files
	  Configuration.addDefaultResource("spatial-default.xml");
	  Configuration.addDefaultResource("spatial-site.xml");
	  
	生成R树文件的标志头

	//查找空间空间数据文件的空间索引
	GlobalIndex<Partition> getGlobalIndex(FileSystem fs, Path dir)
		FileStatus[] allFiles; //dir对应的所有文件
		if (OperationsParams.isWildcard(dir)) {
		    allFiles = fs.globStatus(dir);
		} else {
		    allFiles = fs.listStatus(dir);
		for (FileStatus fileStatus : allFiles) {
			//找到文件夹下的一个_master文件（有多个的话报错），或者nasa文件
			if (fileStatus.getPath().getName().startsWith("_master")) {
			  if (masterFile != null)
			    throw new RuntimeException("Found more than one master file in "+dir);
			  masterFile = fileStatus;
			} else if (fileStatus.getPath().getName().toLowerCase().matches(".*h\\d\\dv\\d\\d.*\\.(hdf|jpg|xml)")) {
			  // Handle on-the-fly global indexes imposed from file naming of NASA data
			  nasaFiles++;
		//用master文件找全局索引
		if (masterFile != null) {
	    	ShapeIterRecordReader reader = new ShapeIterRecordReader(fs.open(masterFile.getPath()), 0, masterFile.getLen());
	    	//reader的类继承自SpatialRecordReader，可从分片、流中逐行读入数据，解析成Shape
	    		this.in = in;
	    		this.start = offset;
	    		this.end = endOffset;
	    		this.pos = offset;
	    		this.cellMbr = new Rectangle();
	    		initializeReader();
	    	Rectangle dummy = reader.createKey();
	    		return new Rectangle();
	    	reader.setShape(new Partition());
	    		this.shape = shape;
	    	ShapeIterator values = reader.createValue();
	    		ShapeIterator shapeIter = new ShapeIterator();
	    		shapeIter.setShape(shape);
	    			//这里只给shapeIter赋了shape，没有Reader，所以不会用nextShape去解析文本
	    	ArrayList<Partition> partitions = new ArrayList<Partition>();
	    	while (reader.next(dummy, values)) {
			    		boolean element_read = nextShapeIter(shapeIter);
			    			iter.setSpatialRecordReader((SpatialRecordReader<?, ? extends Shape>) this);
			    				//到这里shapeIter有了shape，也有了Reader，可以用nextShape去循环解析文本了
			    			return iter.hasNext();
			    		key.set(cellMbr);// Set the cellInfo for the last block read
			    		return element_read;

	    	    for (Shape value : values) {
	    	    	partitions.add((Partition) value.clone());
	    	    }
	    	}
	    	GlobalIndex<Partition> globalIndex = new GlobalIndex<Partition>();
	    	globalIndex.bulkLoad(partitions.toArray(new Partition[partitions.size()]));
	    	String extension = masterFile.getPath().getName();
	    	extension = extension.substring(extension.lastIndexOf('.') + 1);
	    	globalIndex.setCompact(GridRecordWriter.PackedIndexes.contains(extension));
	    	globalIndex.setReplicated(GridRecordWriter.ReplicatedIndexes.contains(extension));
	    	return globalIndex;
		//读Nasa文件来获取全局索引
		  //略过


	//返回不以. _ 开头的文件	    	
	public static final PathFilter NonHiddenFileFilter 
