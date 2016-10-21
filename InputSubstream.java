//只能读入固定字节数的流
class InputSubstream extends InputStream {

	private InputStream in;
	
	private long remainingBytes;

	public InputSubstream(InputStream in, long length) {
	  this.in = in;
	  this.remainingBytes = length;


	//只读一个字节
	public int read() throws IOException {
	    if (remainingBytes > 0) {
	      remainingBytes--;
	      return in.read();
	    }
	    return -1;

	//最多可读1M
	public int available() throws IOException {
  	    return (int) Math.min(remainingBytes, 1024 * 1024);


	public void close() throws IOException {
	    in.close();