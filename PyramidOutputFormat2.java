//plot ImageWritable to PNG image.
PyramidOutputFormat2  extends FileOutputFormat<TileIndex, Canvas> {
	static class ImageRecordWriter extends RecordWriter<TileIndex, Canvas> {
		ImageRecordWriter(FileSystem outFs, Path taskOutPath, TaskAttemptContext task)
			this.task = task;
			System.setProperty("java.awt.headless", "true");
			this.plotter = Plotter.getPlotter(task.getConfiguration());
			this.outPath = taskOutPath;
			this.outFS = outFs;
			this.vflip = task.getConfiguration().getBoolean("vflip", true);
			String outFName = outPath.getName();
			int extensionStart = outFName.lastIndexOf('.');
			extension = extensionStart == -1 ? ".png"
			    : outFName.substring(extensionStart);

		public void write(TileIndex tileIndex, Canvas r) {
		    if (vflip)
		        tileIndex.y = ((1 << tileIndex.level) - 1) - tileIndex.y;
		    Path imagePath = new Path(outPath, tileIndex.getImageFileName()+extension);
		    	return "tile-"+this.level+"-"+this.x+"-"+this.y;
		    // Write this tile to an image
		    FSDataOutputStream outFile = outFS.create(imagePath);
		    plotter.writeImage(r, outFile, this.vflip);
		    outFile.close();
		    task.progress();

		public void close(TaskAttemptContext context) throws IOException,InterruptedException {
		}

	RecordWriter<TileIndex, Canvas> getRecordWriter(TaskAttemptContext task)
		Path file = getDefaultWorkFile(task, "").getParent();
		FileSystem fs = file.getFileSystem(task.getConfiguration());
		return new ImageRecordWriter(fs, file, task);

	static class MultiLevelOutputCommitter extends FileOutputCommitter
		MultiLevelOutputCommitter(Path outputPath, TaskAttemptContext context)		    
		    super(outputPath, context);
		    this.outPath = outputPath;

		public void commitJob(JobContext context) throws IOException {
		    super.commitJob(context);
		    Configuration conf = context.getConfiguration();
		    FileSystem outFs = outPath.getFileSystem(conf);
  
		    // Write a default empty image to be displayed for non-generated tiles
		    int tileWidth = conf.getInt("tilewidth", 256);
		    int tileHeight = conf.getInt("tileheight", 256);
		    BufferedImage emptyImg = new BufferedImage(tileWidth, tileHeight,
		        BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g = new SimpleGraphics(emptyImg);
		    g.setBackground(new Color(0, 0, 0, 0));
		    g.clearRect(0, 0, tileWidth, tileHeight);
		    g.dispose();
  
		    OutputStream out = outFs.create(new Path(outPath, "default.png"));
		    ImageIO.write(emptyImg, "png", out);
		    out.close();
  
		    // Get the correct levels.
		    String[] strLevels = conf.get("levels", "7").split("\\.\\.");
		    int minLevel, maxLevel;
		    if (strLevels.length == 1) {
		      minLevel = 0;
		      maxLevel = Integer.parseInt(strLevels[0]);
		    } else {
		    minLevel = Integer.parseInt(strLevels[0]);
		    maxLevel = Integer.parseInt(strLevels[1]);
		  }

		  // Add an HTML file that visualizes the result using Google Maps
		  LineReader templateFileReader = new LineReader(getClass()
		      .getResourceAsStream("/zoom_view.html"));
		  PrintStream htmlOut = new PrintStream(outFs.create(new Path(outPath,
		      "index.html")));
		  Text line = new Text();
		  while (templateFileReader.readLine(line) > 0) {
		    String lineStr = line.toString();
		    lineStr = lineStr.replace("#{TILE_WIDTH}", Integer.toString(tileWidth));
		    lineStr = lineStr.replace("#{TILE_HEIGHT}",
		        Integer.toString(tileHeight));
		    lineStr = lineStr.replace("#{MAX_ZOOM}", Integer.toString(maxLevel));
		    lineStr = lineStr.replace("#{MIN_ZOOM}", Integer.toString(minLevel));
		    lineStr = lineStr.replace("#{TILE_URL}", "'tile-' + zoom + '-' + coord.x + '-' + coord.y + '.png'");

		    htmlOut.println(lineStr);
		  }
		  templateFileReader.close();
		  htmlOut.close();

	synchronized OutputCommitter getOutputCommitter(TaskAttemptContext context)
		Path jobOutputPath = getOutputPath(context);
			String name = job.getConfiguration().get(FileOutputFormat.OUTDIR);
			return name == null ? null: new Path(name);
		return new MultiLevelOutputCommitter(jobOutputPath, context);