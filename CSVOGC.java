class CSVOGC extends OGCJTSShape
	/* 包含shape的那一列的索引
	*/
	private byte column = 0;

	/*应当是字体分割符
	Use tab as the default to match with PigStorage*/
	*/
	private char separator = '\t';

	/* 一行文本中shape字段前面的部分*/	
	private byte[] prefix;

	/*一行文本中shape字段后面的部分*/
	private byte[] suffix;