org.apache.hadoop.io;
class DataInputBuffer extends DataInputStream 
	static class Buffer extends ByteArrayInputStream {
	    Buffer() {
	        super(new byte[] {});
	     
  
	    void reset(byte[] input, int start, int length) {
	       this.buf = input;
	       this.count = start+length;
	       this.mark = start;
	       this.pos = start;
	    


	    byte[] getData() { return buf; }
	    int getPosition() { return pos; }
	    int getLength() { return count; }

	Buffer buffer;

	void reset(byte[] input, int start, int length) {
	    buffer.reset(input, start, length);