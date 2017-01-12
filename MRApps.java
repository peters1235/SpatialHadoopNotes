package org.apache.hadoop.mapreduce.v2.util;
public class MRApps extends Apps {
	//看着只是生成一个 文件名-文件对应的集群中的URL 的 字典 ，没有本地化
	static void setupDistributedCache(Configuration conf, Map<String, LocalResource> localResources) 
		// Cache archives
		parseDistributedCacheArtifacts(conf, localResources,  
		    LocalResourceType.ARCHIVE, 
		    DistributedCache.getCacheArchives(conf), 
		    DistributedCache.getArchiveTimestamps(conf),
		    getFileSizes(conf, MRJobConfig.CACHE_ARCHIVES_SIZES), 
		    DistributedCache.getArchiveVisibilities(conf));
		
		// Cache files
		parseDistributedCacheArtifacts(conf, 
		    localResources,  
		    LocalResourceType.FILE, 
		    DistributedCache.getCacheFiles(conf),
		    DistributedCache.getFileTimestamps(conf),
		    getFileSizes(conf, MRJobConfig.CACHE_FILES_SIZES),
		    DistributedCache.getFileVisibilities(conf));


		private static void parseDistributedCacheArtifacts(
		      Configuration conf,
		      Map<String, LocalResource> localResources,
		      LocalResourceType type,
		      URI[] uris, long[] timestamps, long[] sizes, boolean visibilities[])

			for (int i = 0; i < uris.length; ++i) {
			    URI u = uris[i];
			    Path p = new Path(u);
			    FileSystem remoteFS = p.getFileSystem(conf);
			    //resolvePath: 找到文件实际对应的物理文件Return the fully-qualified path of path f resolving the path through any symlinks or mount point
			    /*
			    uri的Authority指的是IP地址或者主机地址+ 端口号的那部分， Schema则是http  hdfs之类的， fragment 则是类似html中的链接中的
			    锚，代表网页的位置
			    */
			    p = remoteFS.resolvePath(p.makeQualified(remoteFS.getUri(),
			        remoteFS.getWorkingDirectory()));
			    // Add URI fragment or just the filename
			    Path name = new Path((null == u.getFragment())
			      ? p.getName()
			      : u.getFragment());
			    if (name.isAbsolute()) {
			      throw new IllegalArgumentException("Resource name must be relative");
			    }
			    String linkName = name.toUri().getPath();
			    LocalResource orig = localResources.get(linkName);
			    org.apache.hadoop.yarn.api.records.URL url = 
			      ConverterUtils.getYarnUrlFromURI(p.toUri());
			    if(orig != null && !orig.getResource().equals(url)) {
			      LOG.warn(
			          getResourceDescription(orig.getType()) + 
			          toString(orig.getResource()) + " conflicts with " + 
			          getResourceDescription(type) + toString(url) + 
			          " This will be an error in Hadoop 2.0");
			      continue;
			    }
			    localResources.put(linkName, LocalResource.newInstance(ConverterUtils
			      .getYarnUrlFromURI(p.toUri()), type, visibilities[i]
			        ? LocalResourceVisibility.PUBLIC : LocalResourceVisibility.PRIVATE,
			      sizes[i], timestamps[i]));