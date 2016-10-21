//允许指定读取位置的流
interface Seekable {
	//将流跳到指定位置，下次read（）方法从pos指定的位置开始读，不能跳到流的末尾的后面
	void seek(long pos) 

	//返回流当前相对流的开头处的偏移
	long getPos() 

	/* Seeks a different copy of the data.  Returns true if 
	* found a new source, false otherwise.  */
	boolean seekToNewSource(long targetPos) 