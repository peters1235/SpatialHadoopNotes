abstract class Plotter {
	//五个函数
	smooth,
	create-canvas, plot, merge,  write.

	//子类 
	GeometricRasterizer
	HDFRasterizer
	HeatMapRasterizer
	LakePlotter
	MagickPlotter

	final String PlotterClass = "SingleLevelPlot.Plotter";

	Rectangle inputMBR;

	int imageWidth, imageHeight;

	void setPlotter(Configuration job, Class<? extends Plotter> plotterClass) {
	    job.setClass(PlotterClass, plotterClass, Plotter.class);

	Plotter getPlotter(Configuration job) {	    
	    Class<? extends Plotter> plotterClass =
	        job.getClass(PlotterClass, null, Plotter.class);
	    if (plotterClass == null)
	      throw new RuntimeException("Plotter class not set in job");
	    Plotter plotter = plotterClass.newInstance();
	    plotter.configure(job);
	    return plotter;

	void configure(Configuration conf) {
	    this.inputMBR = (Rectangle) OperationsParams.getShape(conf, "mbr");
	    this.imageWidth = conf.getInt("width", 1000);
	    this.imageHeight = conf.getInt("height", 1000);

	<S extends Shape> Iterable<S> smooth(Iterable<S> r) {
	    throw new RuntimeException("Not implemented");

	abstract Canvas createCanvas(int width, int height, Rectangle mbr);

	abstract void plot(Canvas layer, Shape shape);

	abstract void merge(Canvas finalLayer, Canvas intermediateLayer);

	//将Canvas写入out中
	abstract void writeImage(Canvas layer, DataOutputStream out,boolean vflip)

	public boolean isSmooth() {	  
		try {
		    smooth(new Vector<Shape>());
		    return true;
		} catch (RuntimeException e) {
		    return false;

	Class<? extends Canvas> getCanvasClass() {
	    return this.createCanvas(0, 0, new Rectangle()).getClass();

	void plot(Canvas layer, Iterable<? extends Shape> shapes) {
	    for (Shape shape : shapes)
	        plot(layer, shape);