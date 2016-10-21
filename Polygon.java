public class Polygon extends java.awt.Polygon implements Shape
	/*npoints The total number of points. The value of npoints represents the number of valid points in this Polygon
	 and might be less than the number of elements in xpoints or ypoints. This value can be NULL.

	*/
	public Polygon(int[] xpoints, int[] ypoints, int npoints) {
	    super(xpoints, ypoints, npoints);.

	public void set(int[] xpoints, int[] ypoints, int npoints) {

	public void write(DataOutput out) throws IOException {

	public void readFields(DataInput in) throws IOException {

	public Text toText(Text text) {

	public void fromText(Text text) {

	public Rectangle getMBR() {
        Rectangle2D mbr = super.getBounds2D();
        return new Rectangle(mbr.getMinX(), mbr.getMinY(),
            mbr.getMaxX(), mbr.getMaxY());

    double distanceTo(double x, double y) {

    public boolean isIntersected(Shape s) {

    public Polygon clone() {

    //两个draw都没实现 
    	
    public void draw(Graphics g, Rectangle fileMBR, int imageWidth,int imageHeight, double scale) {

    public void draw(Graphics g, double xscale, double yscale) {