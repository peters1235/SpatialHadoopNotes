FileOutputFormat
	/*
	Helper function to create the task's temporary output directory and 
	return the path to the task's output file.
	*/
	Path getTaskOutputPath(JobConf conf, String name) 

	//子类
	// Writes canvases to a binary output file
	CanvasOutputFormat
	DelaunayTriangulationOutputFormat
	// Writes canvases as images to the output file
	ImageOutputFormat
	IndexOutputFormat
	PyramidOutputFormat2