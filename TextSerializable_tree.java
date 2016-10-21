interface TextSerializable
	//Store current object as string in the given text appending text already there.
	//返回的是传入的对象，生成的字符串添加到传入的字符串的末尾
	public Text toText(Text text);


    // Retrieve information from the given text.   
    public void fromText(Text text);

