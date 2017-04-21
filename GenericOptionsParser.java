class GenericOptionsParser
	//命令行中设置的文件 jar包等都会对应地添加到  conf中的 tmp参数中
	void processGeneralOptions(Configuration conf, CommandLine line){ 
		if (line.hasOption("libjars")) {
		  conf.set("tmpjars", 
		           validateFiles(line.getOptionValue("libjars"), conf),
		           "from -libjars command line option");
		if (line.hasOption("files")) {
		  conf.set("tmpfiles", 
		           validateFiles(line.getOptionValue("files"), conf),
		           "from -files command line option");
		}
		if (line.hasOption("archives")) {
		  conf.set("tmparchives", 
		            validateFiles(line.getOptionValue("archives"), conf),
		            "from -archives command line option");
		}  