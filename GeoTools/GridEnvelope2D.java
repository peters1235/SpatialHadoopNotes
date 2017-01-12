public class GridEnvelope2D extends java.awt.Rectangle implements GridEnvelope, Cloneable

	public GridEnvelope2D(final java.awt.Rectangle rectangle) {
	    super(rectangle);
	    	this(r.x, r.y, r.width, r.height);



public class java.awt.Rectangle extends Rectangle2D
    implements Shape, java.io.Serializable

    X  Y 表示的是矩形的左上角点的坐标，





/*
Provides the grid coordinate values for the diametrically opposed corners of the grid. 

Remark that both corners are inclusive. Thus the number of elements in the direction of the first axis is getHigh(0) - getLow(0) + 1. 
This is the opposite of Java2D usage where maximal values in Rectangle (as computed by getMaxX() and getMaxY()) are exclusive.
*/
public interface org.opengis.coverage.grid.GridEnvelope
	int getDimension();
	GridCoordinates getLow();
	GridCoordinates getHigh();

	int getLow(int dimension) 
	int getHigh(int dimension)
	int getSpan(int dimension)



interface org.opengis.coverage.grid.GridCoordinates extends Cloneable
	int getDimension();

	/*
	像是整数坐标格网里的坐标值
	Returns one integer value for each dimension of the grid. The ordering of these coordinate values shall be the same as that of the elements of Grid.getAxisNames. 
	The value of a single coordinate shall be the number of offsets from the origin of the grid in the direction of a specific axis.
	*/
	int[] getCoordinateValues();

	/*
	等价于
	getCoordinateValues()[i]
	*/
	int getCoordinateValue(int dimension)

	void setCoordinateValue(int dimension, int value)
