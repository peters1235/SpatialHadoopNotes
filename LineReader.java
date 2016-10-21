/*A class that provides a line reader from an input stream. Depending on the constructor used, lines will either be terminated by:

one of the following: '\n' (LF) , '\r' (CR), or '\r\n' (CR+LF).
or, a custom byte sequence delimiter
In both cases, EOF also terminates an otherwise unterminated line
*/
LineReader
	/*Read from the InputStream into the given Text*/
	int readLine(Text str)
		return readLine(str, maxLineLength, Integer.MAX_VALUE)
			    				return readLine(str, maxLineLength：Integer.MAX_VALUE, maxBytesToConsume：Integer.MAX_VALUE);
			    					//maxLineLength行的最大长度，文件该行中超出的部分不会读入
			    					/*maxBytesToConsume：Integer: the maximum number of bytes to consume in this call.  
			                This is only a hint, because if the line cross
							        *  this threshold, we allow it to happen.  It can overshoot
								       *  potentially by as much as one buffer length.
								    */
							    	if (this.recordDelimiterBytes != null) {
			                //recordDelimiterBytes ： The line delimiter
			                //Read a line terminated by a custom delimiter
			                //自定义了行分隔符的话，按自定义分隔符断行，读取一行的内容，否则按默认分隔符断行，读取一行的内容
							        return readCustomLine(str, maxLineLength, maxBytesToConsume);
							      else 
							        //Read a line terminated by one of CR, LF, or CRLF.
			                return readDefaultLine(str, maxLineLength, maxBytesToConsume);				    		

							    		/* We're reading data from in, but the head of the stream may be
									     * already buffered in buffer, so we have several cases:
									     * 1. No newline characters are in the buffer, so we need to copy
									     *    everything and read another buffer from the stream.
									     * 2. An unambiguously terminated line is in buffer, so we just
									     *    copy to str.
									     * 3. Ambiguously terminated line is in buffer, i.e. buffer ends
									     *    in CR.  In this case we copy everything up to CR to str, but
									     *    we also need to see what follows CR: if it's LF, then we
									     *    need consume LF as well, so next call to readLine will read
									     *    from after that.
									     * We use a flag prevCharCR to signal if previous character was CR
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
									      	int startPosn = bufferPosn; //starting from where we left off the last time
									       		//bufferPosn： the current position in the buffer
													  private int bufferPosn = 0;
												  if (bufferPosn >= bufferLength) {
													  // bufferLength：the number of bytes of real data in the buffer
													  private int bufferLength = 0;

													  startPosn = bufferPosn = 0;
													  if (prevCharCR) {
														  ++bytesConsumed;
													  //重新从in里读到Buffer里
													  bufferLength = fillBuffer(in, buffer, prevCharCR);
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
														  return in.read(buffer);

			    									if (bufferLength <= 0) {
			    										break
			                    //把缓存中的字符过一遍，遇到断行符的话提前结束，并且用newlineLength记录断行符的长度
											    for (; bufferPosn < bufferLength; ++bufferPosn) {
			    									//找换行符， 换行符可能是CR, LF, or CRLF
			    									//newlineLength length of terminating newline 
			                      //为换行符的长度，CR, LF对应 1，CRLF对应2
												    if (buffer[bufferPosn] == LF) {
			    										newlineLength = (prevCharCR) ? 2 : 1;
			    										++bufferPosn; // at next invocation proceed from following byte
			    										break
			    									if (prevCharCR) { //CR + notLF, we are at notLF
			    										newlineLength = 1;
			    										break
			    									prevCharCR = (buffer[bufferPosn] == CR);
			      							//连换行符在内，读到的这行一共多少个字符          									
			      							int readLength = bufferPosn - startPosn;
			      							if (prevCharCR && newlineLength == 0) {
			                        //缓存中最后一个字符是CR才能进到这里来，
			                        //这个字符在一遍读取的过程中不读，留给下一遍
			  										  --readLength; //CR at the end of the buffer
			                      
											    bytesConsumed += readLength;
			                    //计算读到的一行中，减去换行符之后的字符数
											    int appendLength = readLength - newlineLength;
												   
											    if (appendLength > maxLineLength - txtLength) {
			                      //超过maxLineLength的部分被直接截断
												    appendLength = maxLineLength - txtLength;
											    if (appendLength > 0) {
									          str.append(buffer, startPosn, appendLength);
			                      //换行符最终也被读进str
									          txtLength += appendLength;

										    while (newlineLength == 0 && bytesConsumed < maxBytesToConsume);

