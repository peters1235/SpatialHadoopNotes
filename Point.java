/*
没有空间参考
有MBR
*/
class Point implements Shape, Comparable<Point>
	public double x;
	public double y;

	public void write(DataOutput out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);.

	Rectangle getMBR() {
		return new Rectangle(x, y, x + Math.ulp(x), y + Math.ulp(y));

	Shape getIntersection(Shape s) {
		//用MBR：Rectangle 来求相交，结果还是个Rectangle，也没有空间参考
	    return getMBR().getIntersection(s);

	public void draw(Graphics g, Rectangle fileMBR, int imageWidth,int imageHeight, double scale) {

	public void draw(Graphics g, double xscale, double yscale) {