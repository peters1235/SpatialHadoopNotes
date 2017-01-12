InputStream
	/*
	Reads some number of bytes from the input stream and stores them into the buffer
	array b. The number of bytes actually read is returned as an integer. 
	This method blocks until input data is available, end of file is detected, 
	or an exception is thrown.
	If the length of b is zero, then no bytes are read and 0 is returned; otherwise, 
	there is an attempt to read at least one byte.
	If no byte is available because the stream is at the end of the file, 
	the value -1 is returned; otherwise, at least one byte is read and stored into b.
	The first byte read is stored into element b[0], the next one into b[1], and so on.
	The number of bytes read is, at most, equal to the length of b. 
	Let k be the number of bytes actually read; these bytes will be stored in elements b[0] through b[k-1], 
	leaving elements b[k] through b[b.length-1] unaffected.
	The read(b) method for class InputStream has the same effect as:
	read(b, 0, b.length) 
	*/
	int read(byte b[]) 
		return read(b, 0, b.length);

	//循环调用read方法
	int read(byte b[], int off, int len)
		if (b == null) {
		    throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
		    throw new IndexOutOfBoundsException();
		} else if (len == 0) {
		    return 0;}

		int c = read();
		if (c == -1) {
		    return -1;}
		b[off] = (byte)c;

		int i = 1;
		try {
		    for (; i < len ; i++) {
		        c = read();
		        if (c == -1) {
		            break;}
		        b[off + i] = (byte)c;}
		} catch (IOException ee) {
		}
		return i;

	//也是不断调用read方法，只是不存储读的结果
	long skip(long n)