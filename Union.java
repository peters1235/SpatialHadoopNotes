/*
Finds the union of all shapes in the input file.
The output is one shape that represents the union of all shapes in input file.
Given shape must be a subclass of OGCJTSShape

*/
Union
	/**
	 * The map function for the BasicUnion algorithm which works on a set of
	 * shapes. It computes the union of all these shapes and writes the result
	 * to the output.
	 */
	class UnionMap<S extends OGCJTSShape> extends Mapper<Rectangle, Iterable<S>, IntWritable, OGCJTSShape>
		private double[] columnBoundaries;

		map(Rectangle mbr, Iterable<S> shapes, final Context context)
