Parallel
	public static <T> List<T> forEach(int size, RunnableRange<T> r, int parallelism)
		return forEach(0, size, r, parallelism);
			List<T> forEach(int start, int end, RunnableRange<T> r, int parallelism)
				// Put an upper bound on parallelism to avoid empty ranges
			    if (parallelism > (end - start))
			        parallelism = end - start;
			    if (parallelism == 1) {
			      // Avoid creating threads
			        results.add(r.run(start, end));
			    else {
			    	for (int i_thread = 0; i_thread < parallelism; i_thread++) {
				        RunnableRangeThread<T> thread = new RunnableRangeThread<T>(r,
				            partitions[i_thread], partitions[i_thread+1]);
				        thread.setUncaughtExceptionHandler(h);
				        threads.add(thread);
				        threads.lastElement().start();

				    for (int i_thread = 0; i_thread < parallelism; i_thread++) {
				        threads.get(i_thread).join();
				        results.add(threads.get(i_thread).getResult());				    
				return results;