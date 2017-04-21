package org.apache.hadoop.mapred;
/**
 * A helper class for managing the distributed cache for LocalJobRunner.
 */
class LocalDistributedCacheManager {
	/**
	 * Set up the distributed cache by localizing the resources, and updating
	 * the configuration with references to the localized resources.
	 * 将distributed cache 中的资源本地化，并更新相应的配置
	 * @param conf
	 * @throws IOException
	 */
	public void setup(JobConf conf) throws IOException {
	    File workDir = new File(System.getProperty("user.dir"));
	    
	    // Generate YARN local resources objects corresponding to the distributed
	    // cache configuration
	    Map<String, LocalResource> localResources = 
	      new LinkedHashMap<String, LocalResource>();
	    MRApps.setupDistributedCache(conf, localResources);
	    // Generating unique numbers for FSDownload.
	    AtomicLong uniqueNumberGenerator =
	        new AtomicLong(System.currentTimeMillis());
	    
	    //拭出要添加 到 本地Classpath中去的文件 Find which resources are to be put on the local classpath
	    Map<String, Path> classpaths = new HashMap<String, Path>();
	    Path[] archiveClassPaths = DistributedCache.getArchiveClassPaths(conf);
	    if (archiveClassPaths != null) {
	      for (Path p : archiveClassPaths) {
	        FileSystem remoteFS = p.getFileSystem(conf);
	        p = remoteFS.resolvePath(p.makeQualified(remoteFS.getUri(),
	            remoteFS.getWorkingDirectory()));
	        classpaths.put(p.toUri().getPath().toString(), p);
	      }
	    }
	    Path[] fileClassPaths = DistributedCache.getFileClassPaths(conf);
	    if (fileClassPaths != null) {
	      for (Path p : fileClassPaths) {
	        FileSystem remoteFS = p.getFileSystem(conf);
	        p = remoteFS.resolvePath(p.makeQualified(remoteFS.getUri(),
	            remoteFS.getWorkingDirectory()));
	        classpaths.put(p.toUri().getPath().toString(), p);
	      }
	    }
	    
	    // Localize the resources
	    LocalDirAllocator localDirAllocator =
	      new LocalDirAllocator(MRConfig.LOCAL_DIR);
	    FileContext localFSFileContext = FileContext.getLocalFSFileContext();
	    UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
	    
	    ExecutorService exec = null;
	    try {
	      ThreadFactory tf = new ThreadFactoryBuilder()
	      .setNameFormat("LocalDistributedCacheManager Downloader #%d")
	      .build();
	      exec = Executors.newCachedThreadPool(tf);
	      Path destPath = localDirAllocator.getLocalPathForWrite(".", conf);
	      Map<LocalResource, Future<Path>> resourcesToPaths = Maps.newHashMap();
	      for (LocalResource resource : localResources.values()) {
	        Callable<Path> download =
	            new FSDownload(localFSFileContext, ugi, conf, new Path(destPath,
	                Long.toString(uniqueNumberGenerator.incrementAndGet())),
	                resource);
	        Future<Path> future = exec.submit(download);
	        resourcesToPaths.put(resource, future);
	      }
	      for (Entry<String, LocalResource> entry : localResources.entrySet()) {
	        LocalResource resource = entry.getValue();
	        Path path;
	        try {
	          path = resourcesToPaths.get(resource).get();
	        } catch (InterruptedException e) {
	          throw new IOException(e);
	        } catch (ExecutionException e) {
	          throw new IOException(e);
	        }
	        String pathString = path.toUri().toString();
	        String link = entry.getKey();
	        String target = new File(path.toUri()).getPath();
	        //这里生成链接文件
	        symlink(workDir, target, link);
	        
	        if (resource.getType() == LocalResourceType.ARCHIVE) {
	          localArchives.add(pathString);
	        } else if (resource.getType() == LocalResourceType.FILE) {
	          localFiles.add(pathString);
	        } else if (resource.getType() == LocalResourceType.PATTERN) {
	          //PATTERN is not currently used in local mode
	          throw new IllegalArgumentException("Resource type PATTERN is not " +
	          		"implemented yet. " + resource.getResource());
	        }
	        Path resourcePath;
	        try {
	          resourcePath = ConverterUtils.getPathFromYarnURL(resource.getResource());
	        } catch (URISyntaxException e) {
	          throw new IOException(e);
	        }
	        LOG.info(String.format("Localized %s as %s", resourcePath, path));
	        //生成这样的：Localized hdfs://localhost:9000/user/shadoop/render/data/dltb/dltb.sld as file:/tmp/hadoop-shadoop/mapred/local/1482992764841/dltb.sld
	        String cp = resourcePath.toUri().getPath();
	        if (classpaths.keySet().contains(cp)) {
	          localClasspaths.add(path.toUri().getPath().toString());
	        }
	      }
	    } finally {
	      if (exec != null) {
	        exec.shutdown();
	      }
	    }    
	    // Update the configuration object with localized data.
	    if (!localArchives.isEmpty()) {
	      conf.set(MRJobConfig.CACHE_LOCALARCHIVES, StringUtils
	          .arrayToString(localArchives.toArray(new String[localArchives
	              .size()])));
	    }
	    if (!localFiles.isEmpty()) {
	      conf.set(MRJobConfig.CACHE_LOCALFILES, StringUtils
	          .arrayToString(localFiles.toArray(new String[localArchives
	              .size()])));
	    }
	    setupCalled = true;
	}
	

/*
LocalResource represents a local resource required to run a container.

The NodeManager is responsible for localizing the resource prior to launching the container.

Applications can specify LocalResourceType and LocalResourceVisibility.
*/
package org.apache.hadoop.yarn.api.records;
public abstract class LocalResource {
