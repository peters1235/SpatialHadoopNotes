MultilevelPlot    

    plot
        if (params.getBoolean("showmem", false)) {
            // Run a thread that keeps track of used memory
        String[] strLevels = params.get("levels", "7").split("\\.\\.");
            //只一个值的话，表示从0级到第level级，
            //2个值的话，分别代表最小和最大的级别

        // Create an output directory that will hold the output of the two jobs
        FileSystem outFS = outPath.getFileSystem(params);
        outFS.mkdirs(outPath);

        if (OperationsParams.isLocal(params, inPaths)) {
            // Plot local
            plotLocal(inPaths, outPath, plotterClass, params);
        } else {
             int maxLevelWithFlatPartitioning = params.getInt(FlatPartitioningLevelThreshold, 4);
                //The maximum level on which flat partitioning can be used
             if (minLevel <= maxLevelWithFlatPartitioning) {
                //头几级用Flat Partition
                OperationsParams flatPartitioning = new OperationsParams(params);
                flatPartitioning.set("levels", minLevel + ".." + Math.min(maxLevelWithFlatPartitioning, maxLevel));
                flatPartitioning.set("partition", "flat");
                LOG.info("Using flat partitioning in levels " + flatPartitioning.get("levels"));
                runningJob = plotMapReduce(inPaths, new Path(outPath, "flat"), plotterClass, flatPartitioning);

            //高级别用金字塔分区
            if (maxLevel > maxLevelWithFlatPartitioning) {
                OperationsParams pyramidPartitioning = new OperationsParams(params);
                pyramidPartitioning.set("levels",
                        Math.max(minLevel, maxLevelWithFlatPartitioning + 1) + ".." + maxLevel);
                pyramidPartitioning.set("partition", "pyramid");
                LOG.info("Using pyramid partitioning in levels " + pyramidPartitioning.get("levels"));
                runningJob = plotMapReduce(inPaths, new Path(outPath, "pyramid"), plotterClass, pyramidPartitioning);
                                                            //Resolve a child path against a parent path
            }

            //输出Html文件 
            // Write a new HTML file that displays both parts of the pyramid
            // Add an HTML file that visualizes the result using Google Maps
            LineReader templateFileReader = new LineReader(MultilevelPlot.class.getResourceAsStream("/zoom_view.html"));
            PrintStream htmlOut = new PrintStream(outFS.create(new Path(outPath, "index.html")));
            Text line = new Text();
            while (templateFileReader.readLine(line) > 0) {
                String lineStr = line.toString();
                lineStr = lineStr.replace("#{TILE_WIDTH}", Integer.toString(params.getInt("tilewidth", 256)));
                lineStr = lineStr.replace("#{TILE_HEIGHT}", Integer.toString(params.getInt("tileheight", 256)));
                lineStr = lineStr.replace("#{MAX_ZOOM}", Integer.toString(maxLevel));
                lineStr = lineStr.replace("#{MIN_ZOOM}", Integer.toString(minLevel));
                lineStr = lineStr.replace("#{TILE_URL}", "(zoom <= " + maxLevelWithFlatPartitioning
                        + "? 'flat' : 'pyramid')+('/tile-' + zoom + '-' + coord.x + '-' + coord.y + '.png')");

                htmlOut.println(lineStr);
            }
            templateFileReader.close();
            htmlOut.close();

        return runningJob;

    Job plotMapReduce(Path[] inFiles, Path outFile, Class<? extends Plotter> plotterClass,
                OperationsParams params)
        Plotter plotter= plotterClass.newInstance();
        Job job = new Job(params, "MultilevelPlot");
        job.setJarByClass(SingleLevelPlot.class);
        // Set plotter
        Configuration conf = job.getConfiguration();
        Plotter.setPlotter(conf, plotterClass);
        // Set input file MBR
        Rectangle inputMBR = (Rectangle) params.getShape("mbr");

        // Adjust width and height if aspect ratio is to be kept
        if (params.getBoolean("keepratio", true)) {
            //...

       OperationsParams.setShape(conf, InputMBR, inputMBR);

       // 配置输入输出
       job.setInputFormatClass(SpatialInputFormat3.class);
       SpatialInputFormat3.setInputPaths(job, inFiles);
       if (conf.getBoolean("output", true)) {
           job.setOutputFormatClass(PyramidOutputFormat2.class);
           PyramidOutputFormat2.setOutputPath(job, outFile);
       } else {
           job.setOutputFormatClass(NullOutputFormat.class);
       }

       // 设置 mapper, reducer and committer
       String partitionTechnique = params.get("partition", "flat");
       if (partitionTechnique.equalsIgnoreCase("flat")) {
           // Use flat partitioning
           job.setMapperClass(FlatPartitionMap.class);
           job.setMapOutputKeyClass(TileIndex.class);
           job.setMapOutputValueClass(plotter.getCanvasClass());
           job.setReducerClass(FlatPartitionReduce.class);
       } else if (partitionTechnique.equalsIgnoreCase("pyramid")) {
           // Use pyramid partitioning
           Shape shape = params.getShape("shape");
           job.setMapperClass(PyramidPartitionMap.class);
           job.setMapOutputKeyClass(TileIndex.class);
           job.setMapOutputValueClass(shape.getClass());
           job.setReducerClass(PyramidPartitionReduce.class);
       } else {
           throw new RuntimeException("Unknown partitioning technique '" + partitionTechnique + "'");
       }

       //  reducers任务数
       job.setNumReduceTasks(Math.max(1, new JobClient(new JobConf()).getClusterStatus().getMaxReduceTasks() * 7 / 8));
       // Use multithreading in case the job is running locally
       conf.setInt(LocalJobRunner.LOCAL_MAX_MAPS, Runtime.getRuntime().availableProcessors());

       // 启动任务
       if (params.getBoolean("background", false)) {
           job.submit();
       } else {
           job.waitForCompletion(false);
       }
       return job;

	public static class FlatPartitionMap extends Mapper<Rectangle, Iterable<? extends Shape>, TileIndex, Canvas> 
		/** Minimum and maximum levels of the pyramid to plot (inclusive and zero-based) */
        private int minLevel, maxLevel; // 从0 开始 

         /** The grid at the bottom level (i.e., maxLevel) */
		private GridInfo bottomGrid;
			//带行、列数的Rectangle

        /** The MBR of the input area to draw */
        private Rectangle inputMBR;

		/** The plotter associated with this job */
        private Plotter plotter;
        	//那5个抽象函数的接口

        private int tileWidth,tileHeight; //瓦片大小 

        /** 是否对输入数据做平滑处理 */
        private boolean smooth;

        protected void setup(Context context)
            //minLevel  maxLevel 赋值
        	this.inputMBR =	(Rectangle) OperationsParams.getShape(conf, InputMBR:"mbr");
        	this.bottomGrid = new GridInfo(inputMBR.x1, inputMBR.y1, inputMBR.x2, inputMBR.y2);
                //columns = 0 ,rows = 0;

            //在最大级别处的瓦片行列数
        	this.bottomGrid.rows = bottomGrid.columns = 1 << maxLevel;

        	this.tileWidth = conf.getInt("tilewidth", 256);
            this.tileHeight = conf.getInt("tileheight", 256);

            this.plotter = Plotter.getPlotter(conf);
            this.smooth = plotter.isSmooth();

        map(Rectangle partition, Iterable<? extends Shape> shapes, Context context)
            //需要的话，先对输入数据作平滑
        	if (smooth)
                shapes = plotter.smooth(shapes); 
            TileIndex key = new TileIndex();       
            	int level, x, y;
            Map<TileIndex, Canvas> canvasLayers = new HashMap<TileIndex, Canvas>();
            for (Shape shape : shapes)
            	Rectangle shapeMBR = shape.getMBR();
            	Rectangle overlappingCells = bottomGrid.getOverlappingCells(shapeMBR.buffer(bufferSizeXMaxLevel, bufferSizeYMaxLevel));
                    return new java.awt.Rectangle(col1, row1, col2 - col1, row2 - row1);

                	//bufferSizeXMaxLevel bufferSizeYMaxLevel
                    // 从下往上遍历
                    for (key.level = maxLevel; key.level >= minLevel; key.level--) {
                      //从左下角开始，往右上去，先上后右
                      for key.x
                       for  key.y
                        //有的话直接取，没有的话先创建，应了文章中说的，建好的数组缓存在内存中                        
                        Canvas canvasLayer = canvasLayers.get(key);                                                   
                        
                        //当前层级的金字塔中的瓦片的行/列数
                        int gridSize = 1 << key.level;

                        Rectangle tileMBR = new Rectangle();
                        tileMBR.x1 = (inputMBR.x1 * (gridSize - key.x) + inputMBR.x2 * key.x) / gridSize;
                        /*
                        换算成   inputMBR.x1  +  key.x  *  （inputMBR.x2-inputMBR.x1）/gridSize 就清楚了
                                    起点     | 横向的瓦片数 |      一个瓦片横跨的地理空间长度         |  
                        */

                    	tileMBR.x2 = (inputMBR.x1 * (gridSize - (key.x + 1)) + inputMBR.x2 * (key.x + 1)) / gridSize;
                            /*
                            -inputMBR.x1 * (gridSize - (key.x + 1)) /gridSize  + inputMBR.x2 * (key.x + 1)/gridSiz
                            =inputMBR.x1   +   inputMBR.x2 * (key.x + 1) -   inputMBR.x1*(key.x+1)/gridSize 
                            =inputMBR.x1  + (key.x+1) * (inputMBR.x2-inputMBR.x1)/gridSize
                            跟上面的式子一样的

                            */
                    	tileMBR.y1 =
                    	tileMBR.y2 =
                    	canvasLayer = plotter.createCanvas(tileWidth, tileHeight, tileMBR);
                    	canvasLayers.put(key.clone(), canvasLayer);

                    	plotter.plot(canvasLayer, shape);

                    	// Update overlappingCells for the higher level
                        //画完底下的一级之后，画上面的一级，
                        //上面一级的全部瓦片对应的空间范围不变
                        //但是瓦片数目成了下一级的1/4，纵横方向上都减半了

                        //格网索引从0开始，假设在底下一级X方向上的索引是3，则在上一级中对应的位置的索引应该是1
                        //如果下一级中的索引为4，则在上一级中索引应该是2 
                        //不管下级索引中索引值为单还是双数，换算到上一级都是 除以2 再取整
                        int updatedX1 = overlappingCells.x / 2;
                        int updatedY1 = overlappingCells.y / 2;

                        //算下一级右上角格网索引对应的上级索引值时，先求出 
                        //在下一级中 右上角格网的索引值，再除以2取整也就行了
                        int updatedX2 = (overlappingCells.x + overlappingCells.width - 1) / 2;                                                
                        int updatedY2 = (overlappingCells.y + overlappingCells.height - 1) / 2;

                        overlappingCells.x = updatedX1;
                        overlappingCells.y = updatedY1;
                        overlappingCells.width = updatedX2 - updatedX1 + 1;
                        overlappingCells.height = updatedY2 - updatedY1 + 1;

            //输出，得看看对应的OutputFormat，不用，这只是中间结果，OutputFormat是用来输出最终结果的
        	for (Map.Entry<TileIndex, Canvas> entry : canvasLayers.entrySet()) {
            	context.write(entry.getKey(), entry.getValue());

    public static class FlatPartitionReduce extends Reducer<TileIndex, Canvas, TileIndex, Canvas>
        private int minLevel, maxLevel;
        private GridInfo bottomGrid;
        private Rectangle inputMBR;
        private Plotter plotter;
        private int tileWidth ,tileHeight; //256

        setup
            //跟Map里差不多
            //不管平滑

    	reduce(TileIndex tileID, Iterable<Canvas> interLayers, Context context)
            int gridSize = 1 << tileID.level;
            Rectangle tileMBR = new Rectangle();
            //跟Map里一样的
            tileMBR.x1 = (inputMBR.x1 * (gridSize - tileID.x) + inputMBR.x2 * tileID.x) / gridSize;
            tileMBR.x2 =
            tileMBR.y1 =
            tileMBR.y2 = 

            Canvas finalLayer = plotter.createCanvas(tileWidth, tileHeight, tileMBR);
    		for (Canvas interLayer : interLayers) {
                plotter.merge(finalLayer, interLayer);
                context.progress();

            context.write(tileID, finalLayer);
 	
 	PyramidPartitionMap
        private int minLevel, maxLevel;         
        /** Maximum level to replicate to */
        private int maxLevelToReplicate;
        private Rectangle inputMBR;
        private GridInfo bottomGrid;
        private int maxLevelsPerReducer;

 		setup
            minLevel ,maxLevel
 			this.maxLevelsPerReducer = conf.getInt(MaxLevelsPerReducer, 3);
            // Adjust maxLevelToReplicate so that the difference is multiple of maxLevelsPerReducer
            //做什么用的？            
            this.maxLevelToReplicate = maxLevel - maxLevelsPerReducer + 1;
 			
 			this.bottomGrid = new GridInfo(inputMBR.x1, inputMBR.y1, inputMBR.x2, inputMBR.y2);
            this.bottomGrid.rows = bottomGrid.columns = (1 << maxLevelToReplicate); // 2 ^ maxLevel

 		map(Rectangle partition, Iterable<? extends Shape> shapes, Context context)
            TileIndex outKey = new TileIndex();

 			for (Shape shape : shapes) {
 				Rectangle shapeMBR = shape.getMBR();
 				Rectangle overlappingCells = bottomGrid.getOverlappingCells(shapeMBR);
 				outKey.level = maxLevelToReplicate;
 				do
 					for 
 						for
 							context.write(outKey, shape);

 					// Shrink overlapping cells to match the upper level
                    //类似PlotPartition，往上缩级
                    int updatedX1 = overlappingCells.x >> maxLevelsPerReducer;
                    int updatedY1 = overlappingCells.y >> maxLevelsPerReducer;
                    int updatedX2 = (overlappingCells.x + overlappingCells.width - 1) >> maxLevelsPerReducer;
                    int updatedY2 = (overlappingCells.y + overlappingCells.height - 1) >> maxLevelsPerReducer;
                    overlappingCells.x = updatedX1;
                    overlappingCells.y = updatedY1;
                    overlappingCells.width = updatedX2 - updatedX1 + 1;
                    overlappingCells.height = updatedY2 - updatedY1 + 1;

                    outKey.level -= maxLevelsPerReducer;
                while (outKey.level + maxLevelsPerReducer > minLevel);

    public static class PyramidPartitionReduce extends Reducer<TileIndex, Shape, TileIndex, Canvas>
        private int minLevel, maxLevel;
        /** Maximum level to replicate to */
        private int maxLevelToReplicate;
        private Rectangle inputMBR;
        /** The grid of the lowest (deepest) level of the pyramid */
        private GridInfo bottomGrid;
        /** The user-configured plotter */
        private Plotter plotter;
        /** Maximum levels to generate per reducer */
        private int maxLevelsPerReducer;
        /** Size of each tile in pixels */
        private int tileWidth, tileHeight;
        /** Whether the configured plotter defines a smooth function or not */
        private boolean smooth;

        setup(Context context)
            //类似Map的
            minLevel ,maxLevel
            this.maxLevelsPerReducer = conf.getInt(MaxLevelsPerReducer, 3);
            // Adjust maxLevelToReplicate so that the difference is multiple of maxLevelsPerMachine
            this.maxLevelToReplicate = maxLevel - (maxLevel - minLevel) % maxLevelsPerReducer;
            this.bottomGrid.rows = bottomGrid.columns = (1 << maxLevelToReplicate); // 2 ^ maxLevel
            this.plotter = Plotter.getPlotter(conf);
            this.smooth = plotter.isSmooth();
            this.tileWidth = conf.getInt("tilewidth", 256);
            this.tileHeight = conf.getInt("tileheight", 256);

        protected void reduce(TileIndex tileID, Iterable<Shape> shapes, Context context)
            //一般情况下，tileID中的level规定了本Reduce任务要绘制的最上层瓦片的级别
            //根据tileID中的 x ，y 以及整个JOb的 MBR 与金字塔的设置则可确定 tileID所确定的瓦片对应的空间范围 ，称之为R
            //包括tileID本层在内，往下一共maxLevelsPerReducer 的瓦片在 R范围内的 要素都由本Reduce任务绘制 
            //如果tileID中的级别接近minLevel 或者 maxLevel，则可能本任务不需要绘制maxLevelsPerReducer层的瓦片
            //具体开始、结束绘制的级别由level1 level2 决定       
            //从tileID所在级开始，到最下一级，虽然瓦片数目以4的倍数递增，比如画3级的话，第一级的1张瓦片与第3级的16张瓦片
            //覆盖的“空间范围”是一样的 
            //空间范围一样，对应的瓦片的像素范围却不一样，这就是金字塔/瓦片了

            int level1 = Math.max(tileID.level, minLevel);
            //这计算应该是有点多余的，level2 的值应该就是tileID.level + maxLevelsPerReducer - 1
            int level2 = Math.max(minLevel, Math.min(tileID.level + maxLevelsPerReducer - 1, maxLevel));
            if (tileID.level < 0)
                tileID.level = 0;

            // Portion of the bottom grid that falls under the given tile
            // The bottom grid is the deepest level of the sub-pyramid that
            // falls under the given tile and will be plotted by this reducer
            
            // First, calculate the MBR of the given tile in the input space
            // This only depends on the tile ID (level and position) and the MBR of the input space
            
            //本Reduce任务在最底下一级绘制时，所负责的空间范围,其实这个空间范围和在最上一级时是一样的
            GridInfo bottomGrid = new GridInfo();

            int gridSize = 1 << tileID.level;
            bottomGrid.x1 = (inputMBR.x1 * (gridSize - tileID.x) + inputMBR.x2 * tileID.x) / gridSize;
            /*
                inputMBR.x1  + tileID.x * (inputMBR.x2-inputMBR.x1)/gridSize
            */
            bottomGrid.x2 = (inputMBR.x1 * (gridSize - (tileID.x + 1)) + inputMBR.x2 * (tileID.x + 1)) / gridSize;
            /*
                inputMBR.x1  + (tileID.x+1) * (inputMBR.x2-inputMBR.x1)/gridSize
            */
            bottomGrid.y1 = (inputMBR.y1 * (gridSize - tileID.y) + inputMBR.y2 * tileID.y) / gridSize;
            bottomGrid.y2 = (inputMBR.y1 * (gridSize - (tileID.y + 1)) + inputMBR.y2 * (tileID.y + 1)) / gridSize;

            // Second, calculate number of rows and columns of the bottom grid
            bottomGrid.columns = bottomGrid.rows = (1 << (level2 - tileID.level)); 
            //maxLevelsPerReducer == 3 的话，应该 行、列都是2^2 = 4

            // The offset in terms of tiles of the bottom grid according to
            // the grid of this level for the whole input file
            //本Reduce任务要绘制的最下一级的那几个瓦片，
            //在它们所属的那一级的从左下角往右上数过来的索引 
            int tileOffsetX = tileID.x << (level2 - tileID.level);
            int tileOffsetY = tileID.y << (level2 - tileID.level);


            Map<TileIndex, Canvas> canvasLayers = new HashMap<TileIndex, Canvas>();

            TileIndex key = new TileIndex();

            context.setStatus("Plotting");
            if (smooth) {
                shapes = plotter.smooth(shapes);
                context.progress();
            }
            
            int i = 0;
            for (Shape shape : shapes) {
                Rectangle shapeMBR = shape.getMBR();

                java.awt.Rectangle overlappingCells = bottomGrid.getOverlappingCells(shapeMBR);
                    //以本任务中左下角那张为原点，算出来
                    //计算当前的Shape与本任务要绘制的最底下一级的瓦片组成的格网（一般是4*4的）中的哪些单元格相交，
                    //相交的单元格的索引从 这个4*4的格网的左下角往右上算索引

                // Shift overlapping cells to be in the full pyramid rather than
                // the sub-pyramid rooted at tileID
                overlappingCells.x += tileOffsetX;
                overlappingCells.y += tileOffsetY;
                    //将上一步的“局部索引”拓展为在 level2级的全部瓦片中的 “全局索引”，整个金字塔代表“全局”

                // Iterate over levels from bottom up
                for (key.level = level2; key.level >= level1; key.level--) {
                    for (key.x = overlappingCells.x; key.x < overlappingCells.x + overlappingCells.width; key.x++) {
                        for (key.y = overlappingCells.y; key.y < overlappingCells.y
                                + overlappingCells.height; key.y++) {
                            Canvas canvasLayer = canvasLayers.get(key);
                            if (canvasLayer == null) {
                                Rectangle tileMBR = new Rectangle();
                                gridSize = 1 << key.level;
                                tileMBR.x1 = (inputMBR.x1 * (gridSize - key.x) + inputMBR.x2 * key.x) / gridSize;
                                tileMBR.x2 = (inputMBR.x1 * (gridSize - (key.x + 1)) + inputMBR.x2 * (key.x + 1))
                                        / gridSize;
                                tileMBR.y1 = (inputMBR.y1 * (gridSize - key.y) + inputMBR.y2 * key.y) / gridSize;
                                tileMBR.y2 = (inputMBR.y1 * (gridSize - (key.y + 1)) + inputMBR.y2 * (key.y + 1))
                                        / gridSize;
                                canvasLayer = plotter.createCanvas(tileWidth, tileHeight, tileMBR);
                                canvasLayers.put(key.clone(), canvasLayer);
                            }
                            plotter.plot(canvasLayer, shape);

                            


