Operations
	class Main
		ProgramDriver pgd = new ProgramDriver();
		pgd.addClass("rangequery", RangeQuery.class,
          "Finds all objects in the query range given by a rectangle");
        pgd.addClass("index", Indexer.class,
          "Spatially index a file using a specific indexer");
        pgd.addClass("gplot", GeometricPlot.class,
          "Plots a file to an image");

        pgd.addClass("vizserver", ShahedServer.class,
            "Starts a server that handles visualization requests");
        pgd.addClass("shahedindexer", AggregateQuadTree.class,
          "Creates a multilevel spatio-temporal indexer for NASA data");
        pgd.addClass("hadoopviz", HadoopvizServer.class,
            "Run Hadoopviz Server");

        pgd.addClass("staggquery", SpatioAggregateQueries.class,
            "Runs a spatio temporal aggregate query on HDF files");
        
        pgd.driver(args);
        	/*
			This is a driver for the example programs. It looks at the first 
			command line argument and tries to find an example program with
			that name. If it is found, it calls the main method in that
			class with the rest of the command line arguments.
        	*/
        	run(args)
        		ProgramDescription pgm = programs.get(args[0]);
        		//第一个参数是要跑的程序的名字，
        		//剩下的参数是这个程序的参数 
        		pgm.invoke(new_args);//new_args 是原来的参数去掉第一个参数




/**The shape used to parse input lines*/
ProgramDriver
	static private class ProgramDescription 
		private Method main;
		
		public ProgramDescription(Class<?> mainClass, 
                              String description)
			this.main = mainClass.getMethod("main", paramTypes);

		public void invoke(String[] args)
			main.invoke(null, new Object[]{args});