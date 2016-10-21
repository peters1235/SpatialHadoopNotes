Canvas implements Writable 
	//子类
	FrequencyMap
	HDFRasterLayer
	ImageCanvas	
	SVGCanvas

	/**The MBR of the this layer in input coordinates*/
	protected Rectangle inputMBR;
	
	/**Width of this layer in pixels*/
	protected int width;
	
	/**Height of this layer in pixels*/
	protected int height;

	void write(DataOutput out) throws IOException {
	  inputMBR.getMBR().write(out);
	  out.writeInt(width);
	  out.writeInt(height);
	}

	// Project a point from input space to image space.
	Point projectToImageSpace(double x, double y) {
		// Calculate the offset of the intermediate layer in the final canvas based on its MBR
		Rectangle finalMBR = this.getInputMBR();
			return inputMBR;
		int imageX = (int) Math.floor((x - finalMBR.x1) * this.getWidth() / finalMBR.getWidth());
		int imageY = (int) Math.floor((y - finalMBR.y1) * this.getHeight() / finalMBR.getHeight());
		return new  java.awt.Point.Point(imageX, imageY);

	void mergeWith(ImageCanvas another) {
	    Point offset = projectToImageSpace(another.getInputMBR().x1, another.getInputMBR().y1);
	    getOrCreateGrahics(false).drawImage(another.getImage(), offset.x, offset.y, null);

	BufferedImage getImage() {
		//清空对象什么信息表作用?
	    if (graphics != null) {
	      graphics.dispose();
	      graphics = null;

	    return image;