interface PositionedReadable {
	//从流的指定位置开始，读length个字节 到buffer 从 offset开始的位置中去，并返回 实际 读到的字节数
	int read(long position, byte[] buffer, int offset, int length)