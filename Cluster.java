//Provides a way to access information about the map/reduce cluster.
public class Cluster {
	private ClientProtocol client;
	
	private static ServiceLoader<ClientProtocolProvider> frameworkLoader =
	    ServiceLoader.load(ClientProtocolProvider.class);

	public Cluster(Configuration conf) throws IOException {
	  this(null, conf);
	  	this.conf = conf;
	  	this.ugi = UserGroupInformation.getCurrentUser();
	  	initialize(jobTrackAddr, conf);
	  		//构造函数只一个参数时jobTrackAddr 为null
	  		synchronized (frameworkLoader) {
	  		    for (ClientProtocolProvider provider : frameworkLoader) {
	  		        LOG.debug("Trying ClientProtocolProvider : "
	  		            + provider.getClass().getName());
	  		        ClientProtocol clientProtocol = null; 
	  		        
	  		         // if (jobTrackAddr == null) {
	  		              clientProtocol = provider.create(conf);
	  		         /*
	  		          } else {
	  		              clientProtocol = provider.create(jobTrackAddr, conf);	  		        
  					*/
	  		          if (clientProtocol != null) {
	  		              clientProtocolProvider = provider;
	  		              client = clientProtocol;
	  		              LOG.debug("Picked " + provider.getClass().getName()+ " as the ClientProtocolProvider");
	  		              break;
	  		          }
	  		          else {
	  		              LOG.debug("Cannot pick " + provider.getClass().getName()+ " as the ClientProtocolProvider - returned null protocol");
	  		          
	 
	public synchronized FileSystem getFileSystem()    
	 	      
	    this.fs = ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
	      public FileSystem run() throws IOException, InterruptedException {
	        final Path sysDir = new Path(client.getSystemDir());
			// LocalJobRunner
				Path sysDir = new Path(
				  conf.get(JTConfig.JT_SYSTEM_DIR, "/tmp/hadoop/mapred/system"));  
				return fs.makeQualified(sysDir).toString();
			//YarnRunner
				return this.resMgrDelegate.getSystemDir();
					Path sysDir = new Path("jobSubmitDir");
					return sysDir.toString();	        

	        return sysDir.getFileSystem(getConf());
	      }
	    });
	      
	  
	    return fs;

	/**
	 * Grab the jobtracker's view of the staging directory path where 
	 * job-specific files will  be placed.
	 * 
	 * @return the staging directory where job-specific files are to be placed.
	 */
	public Path getStagingAreaDir() throws IOException, InterruptedException {
	  if (stagingAreaDir == null) {
	    stagingAreaDir = new Path(client.getStagingAreaDir());
	  }
	  return stagingAreaDir;