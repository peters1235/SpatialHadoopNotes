interface Shape extends Writable, Cloneable, TextSerializable 
	Rectangle getMBR();

	double distanceTo(double x, double y);

	boolean isIntersected(final Shape s);

	Shape clone();

	void draw(Graphics g, Rectangle fileMBR, int imageWidth, int imageHeight, double scale);

	void draw(Graphics g, double xscale, double yscale);
