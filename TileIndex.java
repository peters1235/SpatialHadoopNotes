TileIndex

/**
 * An class that represents a position of a tile in the pyramid.
 * Level is the level of the tile starting with 0 at the top.
 * x and y are the index of the column and row of the tile in the grid
 * at this level.
 */
public class TileIndex implements WritableComparable<TileIndex> {
	public int level, x, y;