/**
 * Stores grid information that can be used with spatial files.
 * The grid is uniform which means all cells have the same width and the same
 * height.
 */


//应该是规定了格网的总范围，以及其内的单元格的数目，没有规定单元格的大小=》可以算
public class GridInfo extends Rectangle {
  	public int columns, rows;

  	public GridInfo(double x1, double y1, double x2, double y2) {
  	  super(x1, y1, x2, y2);
  	  this.columns = 0;
  	  this.rows = 0;

  	//计算按当前设置的范围和分区数，应该把格网分成几行，几列。
  	//要求最终每个单元格的宽、高尽量接近，也就是说单元格的形状尽量接近正方形
  	//numCells 本格网一共有多少个cell，也可理解成待分区的数据一共要被分成多少个分区
  	calculateCellDimensions(int numCells) {


	@Override
	public void write(DataOutput out) throws IOException {
	  super.write(out);
	  out.writeInt(columns);
	  out.writeInt(rows);
	}

	//Computes the range of all cells that overlap a given rectangle
	public java.awt.Rectangle getOverlappingCells(Rectangle rect) {
		col1 = (int)Math.floor((rect.x1 - this.x1) / this.getWidth() * columns);
		col2 = (int)Math.ceil((rect.x2 - this.x1) / this.getWidth() * columns);
		row1 = (int)Math.floor((rect.y1 - this.y1) / this.getHeight() * rows);
		row2 = (int)Math.ceil((rect.y2 - this.y1) / this.getHeight() * rows);
		return new java.awt.Rectangle(col1, row1, col2 - col1, row2 - row1);
			//从左下往右上，
			/*
			int x,
	        int y,
	        int width,
	        int height)
			*/

	public int getOverlappingCell(double x, double y) {
		//返回点所在的单元格的ID，ID是从左下角第一个格子为1 往右上数过去的


	public CellInfo getCell(int cellId) {
		return new CellInfo(cellId, xstart, ystart, xend, yend);