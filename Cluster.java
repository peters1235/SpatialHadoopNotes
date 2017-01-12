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
	  		synchronized (frameworkLoader) {
	  		    for (ClientProtocolProvider provider : frameworkLoader) {
	  		        LOG.debug("Trying ClientProtocolProvider : "
	  		            + provider.getClass().getName());
	  		        ClientProtocol clientProtocol = null; 
	  		        
	  		          if (jobTrackAddr == null) {
	  		              clientProtocol = provider.create(conf);
	  		          } else {
	  		              clientProtocol = provider.create(jobTrackAddr, conf);	  		        
  
	  		          if (clientProtocol != null) {
	  		              clientProtocolProvider = provider;
	  		              client = clientProtocol;
	  		              LOG.debug("Picked " + provider.getClass().getName()+ " as the ClientProtocolProvider");
	  		              break;
	  		          }
	  		          else {
	  		              LOG.debug("Cannot pick " + provider.getClass().getName()+ " as the ClientProtocolProvider - returned null protocol");
	  		          
	 

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