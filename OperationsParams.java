OperationsParams extends Configuration
	OperationsParams(GenericOptionsParser parser)
		this(parser, true);			
			super(parser.getConfiguration());
				// A new configuration with the same settings cloned from another.
				public Configuration(Configuration other) {
			initialize(parser.getRemainingArgs());
				Vector<Path> paths = new Vector<Path>();
				// TODO if the argument shape is set to a class in a third party jar
				// file add that jar file to the archives
				//好像已经做了的
				for (String arg : args) {
					String argl = arg.toLowerCase();
					if (arg.startsWith("-no-")) {
						this.setBoolean(argl.substring(4), false);
					} else if (argl.startsWith("-")) {
						this.setBoolean(argl.substring(1), true);
					} else if (argl.contains(":") && !argl.contains(":/")) {						
						String[] parts = arg.split(":", 2);
						String key = parts[0].toLowerCase();
						String value = parts[1];
						String previousValue = this.get(key);
						if (previousValue == null)
							this.set(key, value);
						else
							this.set(key, previousValue + "\n" + value);
			  		else {
						paths.add(new Path(arg));
				this.allPaths = paths.toArray(new Path[paths.size()]);

	isLocal(Configuration jobConf, Path... input)
		// Whatever is explicitly set has the highest priority
		jobConf.get("local")

		// If any of the input files are hidden, use local processing

		if (input.length > MaxSplitsForLocalProcessing（1）) {
			LOG.info("Too many files. Using MapReduce");
			return false;

		Job job = new Job(jobConf); 
		SpatialInputFormat3<Partition, Shape> inputFormat = new 
		List<InputSplit> splits = inputFormat.getSplits(job);

		if (splits.size() > MaxSplitsForLocalProcessing)
			return false;

		long totalSize = 0;
		for (InputSplit split : splits)
			totalSize += split.getLength();
		if (totalSize > MaxSizeForLocalProcessing) {
			LOG.info("Input size is too large. Using MapReduce");
			return MapReduceProcessing;

	public Path getOutputPath() {
		return allPaths.length > 1 ? allPaths[allPaths.length - 1] : null;
	getInputPaths
	getInputPaths
		//allPaths中，先输入文件夹，再输出文件夹，输入可能有多个，输出只有一个

	/*
	根据传入的字符串，反推实际的Geometry类型，并用这个类型，实例化一个对象，最后返回这个对象。
	*/
	public static Shape getShape(Configuration job, String key) {
		return getShape(job, key, null);
			TextSerializable t = getTextSerializable(conf, key, defaultValue);
				String shapeType = conf.get(key);
				if (shapeType == null)
					return defaultValue;

				int separatorIndex = shapeType.indexOf(ShapeValueSeparator: "//" ); //示例里的写法是rect:500,500,1000,1000，
				//应该是:才对啊？
				Text shapeValue = null;
				//有值的话，将类型和值分别存入shapeValue和shapeType中
				if (separatorIndex != -1) {
					shapeValue = new Text(shapeType.substring(separatorIndex + ShapeValueSeparator.length()));
					shapeType = shapeType.substring(0, separatorIndex);
				}

				TextSerializable shape;

				try {
					Class<? extends TextSerializable> shapeClass = conf.getClassByName(shapeType)
							.asSubclass(TextSerializable.class);
					shape = shapeClass.newInstance();
				} catch (Exception e) {
					// shapeClass is not an explicit class name
					String shapeTypeI = shapeType.toLowerCase();
					if (shapeTypeI.startsWith("rect")) {
						shape = new Rectangle();
					} else if (shapeTypeI.startsWith("point")) {
						shape = new Point();
					} else if (shapeTypeI.startsWith("osm")) {
						shape = new OSMPolygon();
					//还考虑直接从传入的Geometry的值 推断Geometry的类型

				//有值的话把值也反序列化出来
				if (shapeValue != null)
					shape.fromText(shapeValue);
				if (shape instanceof CSVOGC) {
					//处理一下
				return shape;

			return t instanceof Shape ? (Shape) t : null;

	getShape(key)
		getShape(key,null)
			if (defaultValue == null)
				autoDetectShape();
					if (this.get("shape") != null)
						return true; // A shape is already configured
					/*
						下面还有好多	
					*/
			return getShape(this, key, defaultValue);

 			