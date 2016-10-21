/**
 * Computes the union of a set of shapes using a distributed MapReduce program.
 * First, a self join is carried out to relate each shape with all overlapping
 * shapes. Then,  
 *
 */
UltimateUnion

	/**
	 * The map function for the UltimateUnion algorithm which works on a cell
	 * level. It takes all shapes in a rectangular cell, and returns the portion
	 * of the union that is contained in this cell. The output is of type
	 * MultiLineString and contains the lines that is part of the final result
	 * and contained in the given cell.
	 */
	class UltimateUnionMap<S extends OGCJTSShape> extends Mapper<Rectangle, Iterable<Shape>, NullWritable, OGCJTSShape>
		List<Geometry> vgeoms = new ArrayList<Geometry>();
		for (Shape s : shapes)
		  vgeoms.add(((OGCJTSShape) s).geom);

		Coordinate[] coords = new Coordinate[5];
		//把key从左下角，逆时针回到左下角，填入coords

		Geometry partitionMBR = FACTORY.createPolygon(FACTORY.createLinearRing(coords), null);

		ResultCollector<Geometry> resultCollector = new ResultCollector<Geometry>() 
			collect(Geometry r) 
				Geometry croppedUnion = r.getBoundary().intersection(partitionMBR);
				if (croppedUnion != null) {
				  value.geom = croppedUnion;
				  context.write(nullKey, value);


		SpatialAlgorithms.multiUnion(geoms,
		          new Progressable.TaskProgressable(context), resultCollector);




	Job ultimateUnionMapReduce(Path input, Path output,OperationsParams params)
		job.setMapperClass(UltimateUnionMap.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(OGCJTSShape.class);
		job.setNumReduceTasks(0);

		// Set input and output
		job.setInputFormatClass(SpatialInputFormat3.class);
		SpatialInputFormat3.addInputPath(job, input);

		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(job, output);