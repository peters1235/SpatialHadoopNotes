Mapper
	public void run(Context context)
		setup(context);
		try {
	      while (context.nextKeyValue()) {
	        map(context.getCurrentKey(), context.getCurrentValue(), context);
	      }
	    } finally {
	      cleanup(context);
    