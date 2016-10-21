final class TextSerializerHelper {
	//从text中读一个 double出来，并把对应double的字节都去除
	//如果设了分隔符的话，分隔符也去掉
	static double consumeDouble(Text text, char separator) {

	//把一个double型的16进制字符串添加到t中
	void serializeDouble(double d, Text t, char toAppend)
		//代码中没看出来16进制是在哪里转换的