Canvas implements Writable 
	/**The MBR of the this layer in input coordinates*/
	protected Rectangle inputMBR;
	
	/**Width of this layer in pixels*/
	protected int width;
	
	/**Height of this layer in pixels*/
	protected int height;

	public void write(DataOutput out) throws IOException {
	  inputMBR.getMBR().write(out);
	  out.writeInt(width);
	  out.writeInt(height);
	}

	// Project a point from input space to image space.
	public Point projectToImageSpace(double x, double y) {