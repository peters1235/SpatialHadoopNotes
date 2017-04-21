class DistributedCache
	//只修改了配置，没有实际添加文件
	public static void addCacheFile(URI uri, Configuration conf) {
	  String files = conf.get(MRJobConfig.CACHE_FILES);
	  conf.set(MRJobConfig.CACHE_FILES, files == null ? uri.toString() : files + ","
	           + uri.toString());