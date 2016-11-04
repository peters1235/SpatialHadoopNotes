class DefaultCoordinateSequenceTransformer implements CoordinateSequenceTransformer
	final CoordinateSequenceFactory csFactory;

	DefaultCoordinateSequenceTransformer() {
	    csFactory = CoordinateArraySequenceFactory.instance();


	DefaultCoordinateSequenceTransformer(CoordinateSequenceFactory csFactory) {
	    this.csFactory = csFactory;

	CoordinateSequence transform(final CoordinateSequence sequence, final MathTransform transform)
		CoordinateSequence result =  csFactory.create(sequence.size(), targetCSDim);
		int sourceDim = transform.getSourceDimensions();
		int targetDim = transform.getTargetDimensions();

		int size = sequence.size();
			for (int i = 0; i < size; i++) {
				switch (sourceDim) {				   
				    case 3:
				        buffer[ib + 2] = sequence.getOrdinate(i, 2); // Fall through
   
				    case 2:
				        buffer[ib + 1] = sequence.getY(i); // Fall through
   
				    case 1:
				        buffer[ib] = sequence.getX(i); // Fall through

				ib += sourceDim;
