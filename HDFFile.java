HDFFile
	HDFFile(FSDataInputStream inStream)
		this.inStream = inStream;

		//验证是HDF文件头
		byte[] signature = new byte[4];
		inStream.readFully(signature);