/*
 OGC compliant geometry 
 输入的文本格式为csv， geometry 是其中第一个字段，可以是文本的WKT格式（可由PostGIS的ST_AsText(geom)生成） 
 也可以是二进制格式，也可由PostGIS生成，可包含其他字段
 序列化写入时用二进制，读入的时候可文本可二进制
 */
OGCESRIShape
	public OGCGeometry geom;

	public void write(DataOutput out) throws IOException {
	    byte[] bytes = geom.asBinary().array();
	    out.writeInt(bytes.length);
	    out.write(bytes);

	public void readFields(DataInput in) throws IOException {
	    int size = in.readInt();
	    byte[] bytes = new byte[size];
	    in.readFully(bytes);
	    geom = OGCGeometry.fromBinary(ByteBuffer.wrap(bytes));

	/*字符串转成对应的16进制形式，比如 AABB 转成 byte array {0xAA, 0XBB}
	public static byte[] hexToBytes(String hex) {

	public Text toText(Text text) {
	    TextSerializerHelper.serializeGeometry(text, geom, '\0');
	    return text;

	public void fromText(Text text) {	  
	    geom = TextSerializerHelper.consumeGeometryESRI(text, '\0');

	/*
	Geometry 取Envelope，再通过遍历4个角点，取得实例化Rectangle对象所需的4个坐标值
	最后生成Rectangle并返回*/
	public Rectangle getMBR() {

	double distanceTo(double x, double y) {
		OGCPoint point = new OGCPoint(new com.esri.core.geometry.Point(x, y), this.geom.getEsriSpatialReference());
		return this.geom.distance(point);

	/*先比较外包，然后用s转成 OGCESRIShape对象，再取其geom，调用
	OGCGeometry.intersects 来完成相交判断
	*/
	boolean isIntersected(Shape s)

	public Shape clone() {

	public String toString() {
	    return geom.asText();

	void draw(Graphics g, Rectangle fileMBR, int imageWidth,int imageHeight, double scale)

	void draw(Graphics g, double xscale, double yscale) {
    	drawESRIGeom(g, geom, xscale, yscale);

    void drawESRIGeom(Graphics g, OGCGeometry geom, double xscale, double yscale) 