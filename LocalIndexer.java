interface LocalIndexer
	void setup(Configuration conf)

	String getExtension();

	void buildLocalIndex(File nonIndexedFile, Path outputIndexedFile, Shape shape) 