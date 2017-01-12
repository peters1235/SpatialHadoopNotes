class GeometricRasterizer extends Plotter
	Canvas createCanvas(int width, int height, Rectangle mbr) {
	      ImageCanvas imageCanvas = new ImageCanvas(mbr, width, height);
	      imageCanvas.setColor(strokeColor);
	      return imageCanvas;

	void plot(Canvas canvasLayer, Shape shape) {
	      ImageCanvas imgLayer = (ImageCanvas) canvasLayer;
	      imgLayer.drawShape(shape);

	Class<? extends Canvas> getCanvasClass() {
	    return ImageCanvas.class;

	void merge(Canvas finalLayer, Canvas intermediateLayer) {
	    ((ImageCanvas)finalLayer).mergeWith((ImageCanvas) intermediateLayer);

	void writeImage(Canvas layer, DataOutputStream out, boolean vflip 
	    BufferedImage img =  ((ImageCanvas)layer).getImage();
	    // Flip image vertically if needed
	    if (vflip) {
	      AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
	      tx.translate(0, -img.getHeight());
	      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	      img = op.filter(img, null);
	    }
	
	
	    ImageIO.write(img, "png", out);

