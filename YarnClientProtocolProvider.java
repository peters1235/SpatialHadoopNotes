class YarnClientProtocolProvider 
	
	public ClientProtocol create(Configuration conf) throws IOException {
	    return "yarn".equals(conf.get("mapreduce.framework.name"))?
	    	new YARNRunner(conf):null;
	    		this(conf, new ResourceMgrDelegate(new YarnConfiguration(conf)));
	    			public ResourceMgrDelegate(YarnConfiguration conf) {
	    			    super(ResourceMgrDelegate.class.getName());
	    			    this.conf = conf;
	    			    this.client = YarnClient.createYarnClient();
	    			    	YarnClient client = new YarnClientImpl();
	    			    	return client;
	    			    this.init(conf);
	    			    this.start();