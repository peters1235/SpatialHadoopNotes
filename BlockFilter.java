interface BlockFilter
	void configure(Configuration conf);

	//挑选出要被MapReduce任务处理的块
	void selectCells(GlobalIndex<Partition> gIndex,
      ResultCollector<Partition> output);

	void selectCellPairs(GlobalIndex<Partition> gIndex1,
	      GlobalIndex<Partition> gIndex2,
	      ResultCollector2<Partition, Partition> output);