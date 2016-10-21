RepartitionPlotReduce	extends Reducer<IntWritable, Shape, NullWritable, Canvas>
	
	/**The partitioner used to partitioner the data across reducers*/
	private Partitioner partitioner;
	
	/**The component that plots the shapes*/
	private Plotter plotter;

	/**MBR of the input file*/
	private Rectangle inputMBR;
	
	/**Generated image width in pixels*/
	private int imageWidth;
	/**Generated image height in pixels*/
	private int imageHeight;

	/**Whether the configured plotter defines a smooth function or not*/
	private boolean smooth;
	
	@Override
	protected void setup(Context context) 
		Configuration conf = context.getConfiguration();
		this.partitioner = Partitioner.getPartitioner(conf);
		this.plotter = Plotter.getPlotter(conf);
		this.smooth = plotter.isSmooth();
		this.inputMBR = (Rectangle) OperationsParams.getShape(conf, InputMBR);
		this.imageWidth = conf.getInt("width", 1000);
		this.imageHeight = conf.getInt("height", 1000);

	reduce(IntWritable partitionID, Iterable<Shape> shapes,Context context)
		CellInfo partition = partitioner.getPartition(partitionID.get());
		int canvasX1 = (int) Math.floor((partition.x1 - inputMBR.x1) * imageWidth / inputMBR.getWidth());
		int canvasX2 = (int) Math.ceil((partition.x2 - inputMBR.x1) * imageWidth / inputMBR.getWidth());
		int canvasY1 = (int) Math.floor((partition.y1 - inputMBR.y1) * imageHeight / inputMBR.getHeight());
		int canvasY2 = (int) Math.ceil((partition.y2 - inputMBR.y1) * imageHeight / inputMBR.getHeight());

		Canvas canvasLayer = plotter.createCanvas(canvasX2 - canvasX1, canvasY2 - canvasY1, partition);
		//第一个参数为宽，约等于 (partition.x2 - partition.x1) * imageWidth / inputMBR.getWidth()

		if (smooth) {
		  shapes = plotter.smooth(shapes);
		    /*
		    Smooth a set of records that are spatially close to each other 
		    and returns a new set of smoothed records.
		    This method is called on the original raw data before it is visualized. 		    
		    The results of this function are the records that are visualized.
		    */
		  context.progress();
		}

		int i = 0;
		for (Shape shape : shapes) {
		  plotter.plot(canvasLayer, shape);
		  if (((++i) & 0xff) == 0)
		    context.progress();

		context.write(NullWritable.get(), canvasLayer);