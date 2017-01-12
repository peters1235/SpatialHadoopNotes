DataInput
	DataInputStream
		FSDataInputStream extends DataInputStream
          implements Seekable, PositionedReadable, 
            ByteBufferReadable, HasFileDescriptor, CanSetDropBehind, CanSetReadahead,
            HasEnhancedByteBufferAccess

	/*
	Reads some bytes from an input stream and stores them into the buffer array b. 
	The number of bytes read is equal to the length of b.
	*/
	readFully