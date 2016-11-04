/*
Service object that takes a geometry and applies a MathTransform to the coordinates it contains, 
creating a new geometry as the transformed output. 

The standard usage pattern is to supply a MathTransform and CoordinateReferenceSystem explicitly.
The transform(Geometry) method can then be used to construct transformed geometries 
using the GeometryFactory and CoordinateSequenceFactory of the input geometry.
用指定的投影将坐标串转为另一个投影下的坐标串并输出
*/
class GeometryCoordinateSequenceTransformer {
	//构造函数为空

	private CoordinateSequenceTransformer csTransformer = null;

	void setMathTransform(MathTransform transform) {
        this.transform = transform;
        this.curveCompatible = isCurveCompatible(transform);

    //针对不同类型（点线面等）进行转换，
    Geometry transform(Geometry g)
    	GeometryFactory factory = g.getFactory();
    	Geometry transformed = null;
    	
    	// lazily init csTransformer using geometry's CSFactory
    	init(factory);

    	//点
    	if (g instanceof Point) {
    	    transformed = transformPoint((Point) g, factory);
    	//多点
	    	MultiPoint mp = (MultiPoint) g;
	    	Point[] points = new Point[mp.getNumGeometries()];

	    	for (int i = 0; i < points.length; i++) {
	    	    points[i] = transformPoint((Point) mp.getGeometryN(i), factory);
	    	}

	    	transformed = factory.createMultiPoint(points);
	    //线
	    	transformed = transformLineString((LineString) g, factory);
	    //多线
	    	MultiLineString mls = (MultiLineString) g;
	    	LineString[] lines = new LineString[mls.getNumGeometries()];

	    	for (int i = 0; i < lines.length; i++) {
	    	    lines[i] = transformLineString((LineString) mls.getGeometryN(i), factory);
	    	}

	    	transformed = factory.createMultiLineString(lines);

	    //多边形
	    	transformed = transformPolygon((Polygon) g, factory);
	    //多多边形
	    	MultiPolygon mp = (MultiPolygon) g;
	    	Polygon[] polygons = new Polygon[mp.getNumGeometries()];

	    	for (int i = 0; i < polygons.length; i++) {
	    	    polygons[i] = transformPolygon((Polygon) mp.getGeometryN(i), factory);
	    	}

	    	transformed = factory.createMultiPolygon(polygons);
	    //多部件
	    	GeometryCollection gc = (GeometryCollection) g;
	    	Geometry[] geoms = new Geometry[gc.getNumGeometries()];

	    	for (int i = 0; i < geoms.length; i++) {
	    	    geoms[i] = transform(gc.getGeometryN(i));
	    	}

	    	transformed = factory.createGeometryCollection(geoms);

	    //copy over user data 用户数据是什么？
	    // do a special check for coordinate reference system 
	    transformed.setUserData(g.getUserData());

	    if ((g.getUserData() == null) || g.getUserData() instanceof CoordinateReferenceSystem) {
	        //set the new one to be the target crs
	        if (crs != null) {
	            transformed.setUserData(crs);
	    
	    return transformed;

	Point transformPoint(Point point, GeometryFactory gf)        
    	
        // if required, init csTransformer using geometry's CSFactory
        init(gf);

        CoordinateSequence cs = projectCoordinateSequence(point.getCoordinateSequence());
        Point transformed = gf.createPoint(cs);
        transformed.setUserData( point.getUserData() );
        return transformed; 

    CoordinateSequence projectCoordinateSequence(CoordinateSequence cs)    
        return csTransformer.transform(cs, transform);

    void init(GeometryFactory gf)            
    	// don't init if csTransformer already exists
    	if (inputCSTransformer != null) 
    		return;
    	// don't reinit if gf is the same (the usual case)
    	if (currGeometryFactory == gf)
    		return;
    	
    	currGeometryFactory = gf;
    	CoordinateSequenceFactory csf = gf.getCoordinateSequenceFactory();
    	csTransformer = new DefaultCoordinateSequenceTransformer(csf);


interface CoordinateSequenceTransformer {
	CoordinateSequence transform(CoordinateSequence sequence, MathTransform transform)