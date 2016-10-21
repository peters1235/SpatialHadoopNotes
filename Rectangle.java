Rectangle
	//x1 y1 x2 y2 代表 左下 和右上角点	
	
	public Rectangle getIntersection(Shape s) {
	    if (!s.isIntersected(this))
	      return null;
	    Rectangle r = s.getMBR();
	    double ix1 = Math.max(this.x1, r.x1);
	    double ix2 = Math.min(this.x2, r.x2);
	    double iy1 = Math.max(this.y1, r.y1);
	    double iy2 = Math.min(this.y2, r.y2);
	    return new Rectangle(ix1, iy1, ix2, iy2);
	
	double getMaxDistanceTo(double px, double py)

	double getMinDistanceTo(double px, double py) {

	public boolean contains(Point p) {	

	/*跟另一个Shape的MBR合，合出大的来*/
	Rectangle union(final Shape s) {

	/*扩大自己，以容下S*/
	void expand(final Shape s) {

	Point getCenterPoint() {

	/*一条线的两个端点p1 在矩形内，p2在矩形外，求这条线与矩形的交点
	*/	
	Point intersectLineSegment(Point p1, Point p2) {

	boolean isValid() {
    	return !Double.isNaN(x1);

    void invalidate() {
       this.x1 = Double.NaN;

    /*比4个角点的坐标*/
    int compareTo(Rectangle r2) {

   	//两个draw都有

    public Rectangle buffer(double dw, double dh) {

    //平移	
    public Rectangle translate(double dx, double dy) {


    public String toWKT() {
        return String.format("POLYGON((%g %g, %g %g, %g %g, %g %g, %g %g))",
            x1, y1,   x1, y2,   x2, y2,   x2, y1,   x1, y1);