/*
各种空间算法 */
SpatialAlgorithms
	/*
	Computes the union of multiple groups of polygons. The algorithm runs in the following steps.

	The polygons are flattened using flattenGeometries(Geometry []) to extract the simplest form of them
	Polygons are grouped into groups of overlapping polygons using groupPolygons(Geometry [], Progressable) 
	so that we can compute the answer of each group separately
	The union of each group is computed using the unionGroup(Geometry [], Progressable, ResultCollector) function
	*/
	int multiUnion(Geometry[] geoms, final Progressable prog, ResultCollector<Geometry> output)
		//把GeometryCollection转成Geometry数组
		final Geometry[] basicShapes = flattenGeometries(geoms);

		prog.progress();

		final Geometry[][] groups = groupPolygons(basicShapes, prog);
		prog.progress();


		int resultSize = 0;
		for (Geometry[] group : groups) {
		  resultSize += unionGroup(group, prog, output);
		  prog.progress();
		}

		return resultSize;

	public static Geometry[][] groupPolygons(final Geometry[] polygons, final Progressable prog)

	//把GeometryCollection转成Geometry数组
	public static Geometry[] flattenGeometries(final Geometry[] geoms) {

	/*
	Union a group of (overlapping) geometries. It runs as follows.

	All polygons are sorted by the x-dimension of their left most point
	We run a plane-sweep algorithm that keeps merging polygons in batches of 500 objects
	As soon as part of the answer is to the left of the sweep-line, it is finalized and reported to the output
	As the sweep line reaches the far right, all remaining polygons are merged and the answer is reported
	*/
	int unionGroup(final Geometry[] geoms,final Progressable prog, ResultCollector<Geometry> output) 
		if (geoms.length == 1) {
		  output.collect(geoms[0]);
		  return 1;

		// Sort objects by x to increase the chance of merging overlapping objects
		for (Geometry geom : geoms) {
		  Coordinate[] coords = geom.getEnvelope().getCoordinates();
		  double minx = Math.min(coords[0].x, coords[2].x);
		  geom.setUserData(minx);
		}
		
		Arrays.sort(geoms, new Comparator<Geometry>() {
		  @Override
		  public int compare(Geometry o1, Geometry o2) {
		    Double d1 = (Double) o1.getUserData();
		    Double d2 = (Double) o2.getUserData();
		    if (d1 < d2) return -1;
		    if (d1 > d2) return +1;
		    return 0;
		  }

		// All polygons that are to the right of the sweep line
		//应该是一次将500个左右多边形放到这里头进行合并 
		List<Geometry> nonFinalPolygons = new ArrayList<Geometry>();

		while (i < geoms.length) {
			for (int j = 0; j < batchSize; j++) {
			  nonFinalPolygons.add(geoms[i++]);

			double sweepLinePosition = (Double)nonFinalPolygons.get(nonFinalPolygons.size() - 1).getUserData();

			Geometry  batchUnion = safeUnion(nonFinalPolygons, new Progressable.NullProgressable()

			nonFinalPolygons.clear();
			if (batchUnion instanceof GeometryCollection) {
				GeometryCollection coll = (GeometryCollection) batchUnion;
				for (int n = 0; n < coll.getNumGeometries(); n++) {
				  Geometry geom = coll.getGeometryN(n);
				  Coordinate[] coords = geom.getEnvelope().getCoordinates();
				  double maxx = Math.max(coords[0].x, coords[2].x);
				  if (maxx < sweepLinePosition) {
				    // This part is finalized
				    resultSize++;
				    if (output != null)
				      output.collect(geom);
				  } else {
				    nonFinalPolygons.add(geom);

			} else {
			  nonFinalPolygons.add(batchUnion);


		for (Geometry finalPolygon : nonFinalPolygons)
		  output.collect(finalPolygon);


	/*	
	 * Directly unions the given list of polygons using a safe method that tries
	 * to avoid geometry exceptions. First, it tries the buffer(0) method. It it
	 * fails, it falls back to the tradition union method.
	*/
	public static Geometry safeUnion(List<Geometry> polys,Progressable progress)
		if (polys.size() == 1)
		  return polys.get(0);

		GeometryFactory geomFactory = new GeometryFactory();
		Stack<Integer> rangeStarts,rangeEnds
		rangeStarts.push(0);
		rangeEnds.push(polys.size());
		List<Geometry> results
		// Minimum range size that is broken into two subranges
		final int MinimumThreshold = 10;

		while (!rangeStarts.isEmpty()) {
			int rangeStart = rangeStarts.pop();
			int rangeEnd = rangeEnds.pop();

			try {
			  // Union using the buffer operation

			  /*
			  Build an appropriate Geometry, MultiGeometry, or GeometryCollection to contain the Geometrys in it. For example:
			  If geomList contains a single Polygon, the Polygon is returned.
			  If geomList contains several Polygons, a MultiPolygon is returned.
			  If geomList contains some Polygons and some LineStrings, a GeometryCollection is returned.
			  If geomList is empty, an empty GeometryCollection is returned
			  Note that this method does not "flatten" Geometries in the input, and hence if any MultiGeometries are contained in the input a GeometryCollection containing them will be returned.
			  */		 
			  GeometryCollection rangeInOne = (GeometryCollection) geomFactory.buildGeometry(polys.subList(rangeStart, rangeEnd));
			  
			  /*
				Computes a buffer area around this geometry having the given width. 
				The buffer of a Geometry is the Minkowski sum or difference of the geometry with a disc of radius abs(distance).

				Mathematically-exact buffer area boundaries can contain circular arcs. 
				To represent these arcs using linear geometry they must be approximated with line segments. 
				The buffer geometry is constructed using 8 segments per quadrant to approximate the circular arcs. 
				The end cap style is CAP_ROUND.

				The buffer operation always returns a polygonal result. 
				The negative or zero-distance buffer of lines and points is always an empty Polygon. 
				This is also the result for the buffers of degenerate (zero-area) polygons.
			  */
			  Geometry rangeUnion = rangeInOne.buffer(0);
			  results.add(rangeUnion);
			  progressNum += rangeEnd - rangeStart;
			catch (Exception e) {
				if (rangeEnd - rangeStart < MinimumThreshold) {
					// Do the union directly using the old method (union)
					Geometry rangeUnion = geomFactory.buildGeometry(new ArrayList<Geometry>());
					for (int i = rangeStart; i < rangeEnd; i++) {			
						/*
						Computes a Geometry representing the point-set which is contained in both this Geometry and the other Geometry.

						The union of two geometries of different dimension produces a result geometry of dimension equal to the maximum dimension of the input geometries. 
						The result geometry may be a heterogenous GeometryCollection. 
						If the result is empty, it is an atomic geometry with the dimension of the highest input dimension.

						Unioning LineStrings has the effect of noding and dissolving the input linework. 
						In this context "noding" means that there will be a node or endpoint in the result for every endpoint or line segment crossing in the input. 
						"Dissolving" means that any duplicate (i.e. coincident) line segments or portions of line segments will be reduced to a single line segment in the result. 
						If merged linework is required, the LineMerger class can be used.

						Non-empty GeometryCollection arguments are not supported.
						*/ 
					    rangeUnion = rangeUnion.union(polys.get(i));

					results.add(rangeUnion);

		Geometry finalResult = results.remove(results.size() - 1);
		while (!results.isEmpty()) {
		  try {
		    finalResult = finalResult.union(results.remove(results.size() - 1));