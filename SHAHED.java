public class ShahedServer extends AbstractHandler {
	main
		Path[] paths = params.getPaths();
		//只有一个数据参数的话，把它当成索引文件的位置
		Path datasetPath = paths.length == 1? new Path("http://e4ftl01.cr.usgs.gov/") : paths[0];
		Path indexPath = paths.length == 1? paths[0] : paths[1];
		startServer(datasetPath, indexPath, params);
			int port = params.getInt("port", 8889);
			if (params.get("shape") == null)
			  params.setClass("shape", NASARectangle.class, Shape.class);

			Server server = new Server(port);
			server.setHandler(new ShahedServer(dataPath, indexPath, params));
			server.start();
			server.join();

	public ShahedServer(Path dataPath, Path indexPath, OperationsParams params) {
		this.username =this.from= params.get("username");
		this.dataPath = dataPath;
		this.indexPath = indexPath;

	public void handle(String target, HttpServletRequest request,HttpServletResponse response, int dispatch)
		LOG.info("Received request: '"+request.getRequestURL()+"'");
		//导出图片
		if (target.endsWith("/generate_image.cgi")) {
		  LOG.info("Generating image");
		  // Start a background thread that handles the request
		  new ImageRequestHandler(request).start();
		  response.setStatus(HttpServletResponse.SC_OK);
		  response.setContentType("text/plain;charset=utf-8");
		  response.getWriter().println("Image request received successfully");
		//统计
		} else if (target.endsWith("/aggregate_query.cgi")) {
		  handleAggregateQuery(request, response);
		  LOG.info("Aggregate query results returned");
		//论文中提到的一种查询
		} else if (target.endsWith("/selection_query.cgi")) {
		  handleSelectionQuery(request, response);
		  LOG.info("Selection query results returned");
		} else {
		  if (target.equals("/"))
		    target = "/index.html";
		  tryToLoadStaticResource(target, response);
		}