/**
 * Information about a specific cell in a grid.
 * Note: Whenever you change the instance variables that need to
 * be stored in disk, you have to manually fix the implementation of class
 * BlockListAsLongs
 */
//记录了单元格对应的实际范围
CellInfo  extends Rectangle 
	/**
    * A unique ID for this cell in a file. This must be set initially when
    * cells for a file are created. It cannot be guessed from cell dimensions.
    */
  	public int cellId;


/*
This class provides an interface for accessing list of blocks
that has been implemented as long[]. This class is useful for block report.
Rather than send block reports as a Block[] we can send it as a long[]. 
The structure of the array is as follows: 
0: the length of the finalized replica list; 
1: the length of the under-construction replica list; 
- followed by finalized replica list where each replica is represented by 3 longs:
 one for the blockId, one for the block length, and one for the generation stamp; 
 - followed by the invalid replica represented with three -1s;
  - followed by the under-construction replica list where
   each replica is represented by 4 longs: 
three for the block id, length, generation stamp, and the fourth for the replica state.
*/
BlockListAsLongs


