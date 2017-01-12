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

	com.vividsolutions.jts.geom.Geometry geom; // 与ESRIGeometry不一样	

	void write(DataOutput out)
		//同样是先长度，然后Geometry
		byte[] wkb = wkbWriter.write(geom);
		out.writeInt(wkb.length);
		out.write(wkb);

	void readFields(DataInput in) throws IOException {	  
	    byte[] wkb = new byte[in.readInt()];
	    in.readFully(wkb);
	    geom = wkbReader.read(wkb);

	Text toText(Text text) {
	    TextSerializerHelper.serializeGeometry(text, geom, '\0');

	Geometry parseText(String str) throws ParseException {
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

    void fromText(Text text) {
        this.geom = TextSerializerHelper.consumeGeometryJTS(text, '\0');

    Rectangle getMBR() {
    	Coordinate[] coords = geom.getEnvelope().getCoordinates();

    double distanceTo(double x, double y) {

    boolean isIntersected(Shape s) {

    Shape clone() {

    String toString() {
        return geom == null? "(empty)" : geom.toString();


    //应该是使用这个了
    void draw(Graphics g, double xscale, double yscale) {
    	//全部用false会不会有问题？
        drawJTSGeom(g, geom, xscale, yscale, false);

    /*
    /xscale： 一空间单位合多少个像素 。xscale * 空间坐标x = x方向上要多少个像素，得到的结果可直接用于在Graphics上绘图
    fill：是填充整个形状还是只绘制轮廓
    */
    static void drawJTSGeom(Graphics g, Geometry geom, double xscale, double yscale, boolean fill) {
    	//多部件的话，分部件绘制
    	if (geom instanceof GeometryCollection) {
    		GeometryCollection geom_coll = (GeometryCollection) geom;
    		for (int i = 0; i < geom_coll.getNumGeometries(); i++) {
    		    Geometry sub_geom = geom_coll.getGeometryN(i);
    		    // Recursive call to draw each geometry
    		    drawJTSGeom(g, sub_geom, xscale, yscale, fill);

    	//多边形的话，先画所有内环，再画外环
    	else if (geom instanceof com.vividsolutions.jts.geom.Polygon) {
    		com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) geom;

    		for (int i = 0; i < poly.getNumInteriorRing(); i++) {
    		    LineString ring = poly.getInteriorRingN(i);
    		    drawJTSGeom(g, ring, xscale, yscale, fill);
    	 
    		drawJTSGeom(g, poly.getExteriorRing(), xscale, yscale, fill);

    	else if (geom instanceof com.vividsolutions.jts.geom.LineString)
    		LineString line = (LineString) geom;
    		double geom_alpha = line.getLength() * (xscale + yscale) / 2.0;
    		int color_alpha = geom_alpha > 1.0 ? 255 : (int) Math.round(geom_alpha * 255);
    		if (color_alpha == 0)
    		    return;
    		
    		int[] xpoints = new int[line.getNumPoints()];
    		int[] ypoints = new int[line.getNumPoints()];
    		int n = 0;

    		for (int i = 0; i < xpoints.length; i++) {
    		    double px = line.getPointN(i).getX();
    		    double py = line.getPointN(i).getY();
    		    
    		    //空间坐标转图像坐标
    		    xpoints[n] = (int) Math.round(px * xscale);
    		    ypoints[n] = (int) Math.round(py * yscale);
    		    // Include this point only if first point or different than previous point
    		    if (n == 0 || xpoints[n] != xpoints[n-1] || ypoints[n] != ypoints[n-1])
    		   		n++;    		 
    		
    		// Draw the polygon
    		//graphics.setColor(new Color((shape_color.getRGB() & 0x00FFFFFF) | (color_alpha << 24), true));
    		if (n == 1)
    			//画点
    		    g.fillRect(xpoints[0], ypoints[0], 1, 1);
    		else if (!fill)
    			//不填充的话，当线来画
    			//首末端点要相同线才闭合
    		    g.drawPolyline(xpoints, ypoints, n);
    		else
    			//填充的话，当多边形来画
    		    g.fillPolygon(xpoints, ypoints, n);

    //不调用这个了
    void draw(Graphics g, Rectangle fileMBR, int imageWidth,int imageHeight, double scale) {
    	Geometry geom = this.geom;
    	Color shape_color = g.getColor();
    	
    	drawJTSShape(g, geom, fileMBR, imageWidth, imageHeight, scale, shape_color);

    /*过时了*/
    static void drawJTSShape(Graphics graphics, Geometry geom,Rectangle fileMbr, int imageWidth, int imageHeight, double scale,
          Color shape_color)