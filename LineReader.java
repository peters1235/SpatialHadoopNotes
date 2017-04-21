/*A class that provides a line reader from an input stream. Depending on the constructor used, lines will either be terminated by:

one of the following: '\n' (LF) , '\r' (CR), or '\r\n' (CR+LF).
or, a custom byte sequence delimiter
In both cases, EOF also terminates an otherwise unterminated line
*/
package org.apache.hadoop.util;
/*
A Closeable is a source or destination of data that can be closed. 
The close method is invoked to release resources that the object is holding (such as open files).
*/
class LineReader implements Closeable {
	//已读入str中的字节数
	int txtLength = 0;
	//输入的流
	InputStream in;
	static final int DEFAULT_BUFFER_SIZE = 64 * 1024;
	//从流中读数据的buffer的大小 
	int bufferSize = DEFAULT_BUFFER_SIZE;
	byte[] buffer;

	// the number of bytes of real data in the buffer
	int bufferLength = 0;
	// the current position in the buffer
	//buffer中下一个待处理的字节的索引，从0 开始
	int bufferPosn = 0;
	//分隔符数组
	final byte[] recordDelimiterBytes;

	/*Read from the InputStream into the given Text*/
	int readLine(Text str)
		return readLine(str, Integer.MAX_VALUE, Integer.MAX_VALUE);

	int readLine(Text str, int maxLineLength) 
		return readLine(str, maxLineLength, Integer.MAX_VALUE);

	//前两个方法都调用这个方法
	/*
	str用于存储读到的新行
	str中最多存储maxLineLength个字节，该行中超出的部分不会存入str中。the object to store the given line (without newline)
	maxBytesToConsume the maximum number of bytes to consume in this call. 
		This is only a hint, because if the line cross this threshold, we allow it to happen.
	 	It can overshoot potentially by as much as one buffer length.
	*/
	int readLine(Text str, int maxLineLength,int maxBytesToConsume)
		if (this.recordDelimiterBytes != null) {
            //recordDelimiterBytes ： The line delimiter
            //Read a line terminated by a custom delimiter
            //自定义了行分隔符的话，按自定义分隔符断行，读取一行的内容，否则按默认分隔符断行，读取一行的内容
			return readCustomLine(str, maxLineLength, maxBytesToConsume);
	    else 
	    	//以CR、LF、或者CRLF为换行符读取一行
		    //Read a line terminated by one of CR, LF, or CRLF.
		    return readDefaultLine(str, maxLineLength, maxBytesToConsume);				    		
	    		/* We're reading data from in, but the head of the stream may be
			     * already buffered in buffer, so we have several cases:
			     * 1.  Buffer里没有换行符，则需要把Buffer中的全部数据都读出来，并且还要从in中读一部分数据到Buffer中
			          No newline characters are in the buffer, so we need to copy
			     *    everything and read another buffer from the stream.
			     * 2. Buffer里有完整的换行符，则把对应的内容 读到str中就好了An unambiguously terminated line is in buffer, so we just
			     *    copy to str.
			     * 3. Buffer的末尾是换行符一部分，eg只有CR，则str中存储 Buffer里直到CR字符前的内容，
			          同时还得查看 CR后面是不是LF，如果是的话，这个LF要去掉，这样下次读一行的话就不会把LF给读进去
			          Ambiguously terminated line is in buffer, i.e. buffer ends
			     *    in CR.  In this case we copy everything up to CR to str, but
			     *    we also need to see what follows CR: if it's LF, then we
			     *    need consume LF as well, so next call to readLine will read
			     *    from after that.
			     *
			       prevCharCR 表示上一个字符是不是CR，如果Buffer的最后一个字符是CR的话，把这个字符和接下来的字符放在一块处理
			       We use a flag prevCharCR to signal if previous character was CR
			     * and, if it happens to be at the end of the buffer, delay
			     * consuming it until we have a chance to look at the char that
			     * follows.
			     CRLF是“carriage return/line feed”，意思就是回车。这是两个ASCII字符，
			     分别排在第十三和第十位。CR和LF是在计算机终端还是电传打印机的时候遗留下来的东西。
			     电传打字机就像普通打字机一样工作。在每一行的末端，CR命令让打印头回到左边。
			     LF命令让纸前进一行。虽然使用卷纸的终端时代已经过去了，但是，CR和LF命令依然存在，
			     许多应用程序和网络协议仍使用这些命令作为分隔符。
			     */
			    str.clear();
			    do {
			    	//bufferPosn从0 开始
			    	//startPosn表示 为了读取一行，是从Buffer中的哪个位置开始的，
			    	int startPosn = bufferPosn; //starting from where we left off the last time
			     		//bufferPosn： the current position in the buffer
			    		//bufferPosn为Buffer中下一个待处理的字节的索引

					//Buffer中的数据都处理完了，从in中再读一些数据进来
				    if (bufferPosn >= bufferLength) {
				  	    // bufferLength：the number of bytes of real data in the buffer		
				  	    // 从in中读取一次数据到buffer中后，实际读到的字节数

				  	    startPosn = bufferPosn = 0;
				  	    if (prevCharCR) {
				  	    	//还之前的账，把上一次缓存中剩下的换行符算进来
				  		    ++bytesConsumed;
				  	    //重新从in里读到Buffer里
				  	    bufferLength = fillBuffer(in, buffer, prevCharCR);	                       
							return in.read(buffer);

			    		if (bufferLength <= 0) {
			    			break //EOF

		            //把缓存中的字符过一遍，遇到断行符的话提前结束，并且用newlineLength记录断行符的长度
			    			//换行符为 CR或LF则newlineLength为 1，为CRLF则为2
			    	//Buffer中从bufferPosn开始没有换行符的话，for循环中的Break执行不到，newLineLength的值为0
			    	
			    	//bufferLength 为Buffer中一共可处理的字节数，
			    	//bufferPosn为Buffer中下一个待处理的字节的索引
					for (; bufferPosn < bufferLength; ++bufferPosn) {
		    			//找换行符， 换行符可能是CR, LF, or CRLF
		    			//newlineLength length of terminating newline 
		                //为换行符的长度，

		                //当前字符为LF，则到了行尾，前一个字符是不是CR决定了newlineLength的长度为2或者1
					    if (buffer[bufferPosn] == LF) {
							newlineLength = (prevCharCR) ? 2 : 1;
							++bufferPosn; // at next invocation proceed from following byte
							break

						//前一个字符为CR，当前字符不是LF，也视为到了行尾
						if (prevCharCR) { //CR + notLF, we are at notLF
							newlineLength = 1;
							break
						prevCharCR = (buffer[bufferPosn] == CR);
			      		
		      		//连换行符在内，读到的这行一共多少个字符    
		      		  /*如果是因为LF退出的，则在那个条件后面有个++bufferPosn，bufferPosn指向的是LF后一个字符
		      		  	如果是因为只有CR退出的，则bufferPosn指向的是CR后面的一个字符
		      		  	如果是这次在Buffer中没有读到换行符，则buff相若erPosn指向Buffer结尾后一个字符，
		      		  	总之，直接拿bufferPosn - startPosn 就是这个Buffer中 这次已经被处理的字节数
						*/
					int readLength = bufferPosn - startPosn;
					if (prevCharCR && newlineLength == 0) {
                        //缓存中最后一个字符是CR才能进到这里来，
                        //这个字符在这一遍读取的过程中不读，留给下一遍，下次读完缓存后，到97行去一起处理
  						--readLength; //CR at the end of the buffer
		                      
					bytesConsumed += readLength;
		            //计算读到的一行中，减去换行符之后的字符数
		            //有换行符的话，去掉，没有的话，全部读到str中去
					int appendLength = readLength - newlineLength;
											   
					if (appendLength > maxLineLength - txtLength) {
		                //超过maxLineLength的部分被直接截断
						appendLength = maxLineLength - txtLength;
					if (appendLength > 0) {
						str.append(buffer, startPosn, appendLength);		                
						txtLength += appendLength;

				while (newlineLength == 0 && bytesConsumed < maxBytesToConsume);

				//怎么不是和maxBytesToConsume比呢？
				if (bytesConsumed > Integer.MAX_VALUE) {
				    throw new IOException("Too many bytes before newline: " + bytesConsumed);
				
				return (int)bytesConsumed;

	//类似readDefaultLine 也要处理换行符的问题
	int readCustomLine(Text str, int maxLineLength, int maxBytesToConsume)
		str.clear();
		int txtLength = 0; // tracks str.getLength(), as an optimization
		long bytesConsumed = 0;
		int delPosn = 0;
		int ambiguousByteCount=0; // 上一次Buffer的末尾 与 分隔符部分匹配的 字节数
		 //To capture the ambiguous characters count  //读到的可能是行尾字符的字节数
		do {
		    int startPosn = bufferPosn; // 从上次读取行结束的位置开始Start from previous end position
		    if (bufferPosn >= bufferLength) {
		        startPosn = bufferPosn = 0;
		        bufferLength = fillBuffer(in, buffer, ambiguousByteCount > 0);
		        if (bufferLength <= 0) {
		            str.append(recordDelimiterBytes, 0, ambiguousByteCount);
		            break; /* EOF*/}}
		    for (; bufferPosn < bufferLength; ++bufferPosn) {
		        if (buffer[bufferPosn] == recordDelimiterBytes[delPosn]) {
		            delPosn++;
		            if (delPosn >= recordDelimiterBytes.length) {
		                bufferPosn++;
		                break;}
		        } else if (delPosn != 0) {
		            bufferPosn--;
		            delPosn = 0;}}
		    int readLength = bufferPosn - startPosn;
		    bytesConsumed += readLength;
		    int appendLength = readLength - delPosn;
		    //截断过长的字符
		    if (appendLength > maxLineLength - txtLength) {
		        appendLength = maxLineLength - txtLength;
		    }
		    if (appendLength > 0) { //本次讲到的字符不是分隔符的后面的部分，也就是说
		    	//如果上次Buffer里的末尾部分与分隔符部分匹配的话，这次的Buffer的开头与分隔符不匹配
		    	//上个Buffer里部分匹配的内容要作为正文添加到str中
		        if (ambiguousByteCount > 0) {
		            str.append(recordDelimiterBytes, 0, ambiguousByteCount);
		            //appending the ambiguous characters (refer case 2.2)
		            bytesConsumed += ambiguousByteCount;
		            ambiguousByteCount=0;
		        }
		        str.append(buffer, startPosn, appendLength);
		        txtLength += appendLength;
		    }
		    if (bufferPosn >= bufferLength) {
		        if (delPosn > 0 && delPosn < recordDelimiterBytes.length) {
		            ambiguousByteCount = delPosn;
		           bytesConsumed -= ambiguousByteCount; //to be consumed in next
		        }
		    }
	    } while (delPosn < recordDelimiterBytes.length 
	          && bytesConsumed < maxBytesToConsume);
	    if (bytesConsumed > Integer.MAX_VALUE) {
	        throw new IOException("Too many bytes before delimiter: " + bytesConsumed);
	    }
	    return (int) bytesConsumed; 

