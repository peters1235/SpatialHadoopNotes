class ShapeLineRecordReader
    extends SpatialRecordReader<Rectangle, Text> 

    boolean next(Rectangle key, Text shapeLine) throws IOException {
        boolean read_line = nextLine(shapeLine);
        key.set(cellMbr);
        return read_line;