/*
表示Geometry中的坐标串，
The internal representation of a list of coordinates inside a Geometry. 

This allows Geometries to store their points using something other than the JTS Coordinate class.
 For example, a storage-efficient implementation might store coordinate sequences as an array of x's and an array of y's. 
 Or a custom coordinate class might support extra attributes like M-values. 

Implementing a custom coordinate storage structure requires
 implementing the CoordinateSequence and CoordinateSequenceFactory interfaces. 
 To use the custom CoordinateSequence,
  create a new GeometryFactory parameterized by the CoordinateSequenceFactory 
  The GeometryFactory can then be used to create new Geometrys. 
  The new Geometries will use the custom CoordinateSequence implementation. 

*/
interface CoordinateSequence extends Cloneable
	//number of ordinates in each coordinate)，一个坐标，是（x，y）这种形式的话Dimension是2，就是几个维度的意思
	int getDimension();

	Coordinate getCoordinate(int i);

	Coordinate getCoordinateCopy(int i);

	void getCoordinate(int index, Coordinate coord);

	double getX(int index);

	double getY(int index);

	double getOrdinate(int index, int ordinateIndex);

	//串中的坐标数
	int size();

	void setOrdinate(int index, int ordinateIndex, double value);

	Coordinate[] toCoordinateArray();

	Envelope expandEnvelope(Envelope env);

	Object clone();