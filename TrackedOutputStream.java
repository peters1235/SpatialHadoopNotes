//记录定稿了多少个字节的输出流
class TrackedOutputStream extends OutputStream
	OutputStream rawOut;
	long offset;

	public void write(int b) throws IOException {
		rawOut.write(b);
		this.offset++;

	public long getPos() {
	    return offset;