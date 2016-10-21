GeometricPlot
	//参数  levels  3..7  -pyramid 

	/*
	shadoop gplot roads.smaple roads.png shape:osm width:2000 height:2000 -keep-ratio color:red -vflip -fast -overwrite
	
	shadoop gplot roads roads_pyramid shape:osm tilewidth:128 tileheight:128 color:red -vflip -fast -overwrite -pyramid levels:1..5 -no-local

	要控制级别的话，在/home/shadoop/hadoop-2.7.2/etc/hadoop/spatial-site.xml 中添加 

	参数中添加没用，原因不明


shadoop gplot roads.smaple roads.png shape:osm  [ -pyramid tilewidth tileheight| width height]
	

	<configuration>
	<property>
	<name>MultilevelPlot.FlatPartitioningLevelThreshold</name>
	<value>2</value>
	</property>
	</configuration>


	*/
	public static class GeometricRasterizer extends Plotter{


	public static Job plot(Path[] inFiles, Path outFile, OperationsParams params)
	{
		if (params.getBoolean("pyramid", false)) 
			return MultilevelPlot.plot(inFiles, outFile, GeometricRasterizer.class, params);
		else {
      		return SingleLevelPlot.plot(inFiles, outFile, GeometricRasterizer.class, params);
	}

	/*
	 Combines images of different datasets into one image that is displayed
   * to users.
   * This method is called from the web interface to display one image for
    * @param files Paths to directories which contains the datasets
   * @param includeBoundaries Also plot the indexing boundaries of datasets
   * @param width
   * @param height
   * @return An image that is the combination of all datasets images
    */
	public static BufferedImage combineImages(Configuration conf, Path[] files,
      boolean includeBoundaries, int width, int height) {
	}

	public static void main(String[] args){
		System.setProperty("java.awt.headless", "true");
		Path[] inFiles = params.getInputPaths();
    	Path outFile = params.getOutputPath();
		plot(inFiles, outFile, params);
	}
}