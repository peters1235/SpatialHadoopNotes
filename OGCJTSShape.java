/*
A shape class that represents an OGC compliant geometry. 
The geometry is enclosed inside the class and all calls are delegated to it. 
The class also holds extra information for each records that could represent other columns for each records. 
The text representation is assumed to be some kind of CSV. 
The shape is always the first column in that CSV. 
The text representation of the shape could be either a WTK (Well Known Text) or a binary representation. 
The WKT can be generated with PostGIS using the function ST_AsText(geom).
An example may look like this:POLYGON ((-89 43,-89 50,-97 50,-97 43,-89 43)) 
The binary representation can be generated from PostGIS by selecting
the geom column using a normal select statement. 
When a shape is parsed, we detect the format and use the appropriate parser. 
When writing to text, we always use the binary representation as it is faster and more compact. 
For binary serialization/deserialization, we use the PostGIS writer and parser
*/
class OGCJTSShape implements Shape {
	private final WKTReader wktReader = new WKTReader();
	private final WKBReader wkbReader = new WKBReader();
	private final WKBWriter wkbWriter = new WKBWriter();

	public com.vividsolutions.jts.geom.Geometry geom; // 与ESRIGeometry不一样	

	void write(DataOutput out)
		//同样是先长度，然后Geometry
		byte[] wkb = wkbWriter.write(geom);
		out.writeInt(wkb.length);
		out.write(wkb);

	public void readFields(DataInput in) throws IOException {	  
	    byte[] wkb = new byte[in.readInt()];
	    in.readFully(wkb);
	    geom = wkbReader.read(wkb);

	Text toText(Text text) {
	    TextSerializerHelper.serializeGeometry(text, geom, '\0');

	public Geometry parseText(String str) throws ParseException {
		//先当WKT来解析，不行就当WKB
    	Geometry geom = null;
    	try {
    	    // Parse string as well known text (WKT)
    	    geom = wktReader.read(str);
    	} catch (ParseException e) {
    	    try {
    	        // Error parsing from WKT, try hex string instead
    	        byte[] binary = WKBReader.hexToBytes(str);
    	        geom = wkbReader.read(binary);

    public void fromText(Text text) {
        this.geom = TextSerializerHelper.consumeGeometryJTS(text, '\0');

    Rectangle getMBR() {
    	Coordinate[] coords = geom.getEnvelope().getCoordinates();

    public double distanceTo(double x, double y) {

    public boolean isIntersected(Shape s) {

    public Shape clone() {

    public String toString() {
        return geom == null? "(empty)" : geom.toString();

    void draw(Graphics g, Rectangle fileMBR, int imageWidth,int imageHeight, double scale) {

    public void draw(Graphics g, double xscale, double yscale) {
        drawJTSGeom(g, geom, xscale, yscale, false);

    static void drawJTSGeom(Graphics g, Geometry geom, double xscale, double yscale, boolean fill) {

    /*过时了*/
    static void drawJTSShape(Graphics graphics, Geometry geom,Rectangle fileMbr, int imageWidth, int imageHeight, double scale,
          Color shape_color)