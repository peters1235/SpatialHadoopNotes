bufstart
	init
		 bufstart = bufend = bufindex = equator;

	resetSpill
		bufstart = bufend = e;
	Buffer.Write
		//单条记录过大
		setEquator(0);
		bufstart = bufend = bufindex = equator;
		kvstart = kvend = kvindex;
		bufvoid = kvbuffer.length;
	//跟bufend比，只差flush里一样
	
	//多一条
	SpillThread.run
		kvstart = kvend;
		bufstart = bufend;

bufend 
	init
        bufstart = bufend = bufindex = equator;

    Buffer.Write
		//单条记录过大
		setEquator(0);
		bufstart = bufend = bufindex = equator;

	resetSpill
		bufstart = bufend = e;

	
	flush
		//类似resetSpill
		bufend = bufmark;

kvstart
	init
		bufstart = bufend = bufindex = equator;
	SpillThread.run
		//spill完之后
		kvstart = kvend;
		bufstart = bufend;
	Buffer.Write
		//单条记录过大
		setEquator(0);
		bufstart = bufend = bufindex = equator;
		kvstart = kvend = kvindex;
		bufvoid = kvbuffer.length;
	resetSpill
		bufstart = bufend = e;
		kvstart = kvend = (int)
		  (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;

kvend
	init
		bufstart = bufend = bufindex = equator;
	startSpill
		kvend = (kvindex + NMETA) % kvmeta.capacity();
		bufend = bufmark;
		spillInProgress = true;
	resetSpill
		kvstart = kvend = (int)
		  (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;

bufindex
	init
		bufstart = bufend = bufindex = equator;

	Buffer.Write
		//单条记录过大
		setEquator(0);
		bufstart = bufend = bufindex = equator;
		kvstart = kvend = kvindex;
		bufvoid = kvbuffer.length;

		//after write kv 
		bufindex += len;
	shiftBufferedKey()
		
	collect
		// after startSpill
		setEquator(newPos);
		bufmark = bufindex = newPos; 

bufmark
	collect
		setEquator(newPos);
		bufmark = bufindex = newPos;.
	int markRecord() {
		//本方法只在collect里头，序列化完key，并且调用完 shiftBufferedkey之后调用
	  	bufmark = bufindex;

kvindex
	collect
		kvindex = (kvindex - NMETA + kvmeta.capacity()) % kvmeta.capacity();
	setEquator	
		//4 bytes after equator
		kvindex = (int)
		        (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;

collect
	不能写了
		spill完了?
			resetSpill
		还没开始spill
			startSpill
			setEquator  

setEquator
	//代码
		equator = pos;
		kvindex = (int)
          (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;
	init

	collect
		startSpill之后 
		setEquator
	Buffer.write
		startSpill之后 
		setEquator

resetSpill
	startSpill之后，spill在另一个线程里进行，本线程没法主动地决定什么时候该调用resetSpill
	只能在后面通过 条件判断  spill结束了，这才调用resetSpill
	代码
		bufstart = bufend = e;
		kvstart = kvend = (int)
        	(((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;

	Buffer.write
		if(!spillInProgress)
			if ((kvbend + METASIZE) % kvbuffer.length !=  equator - (equator % METASIZE)) {
				resetSpill
	flush
		if(!spillInProgress)
			if ((kvbend + METASIZE) % kvbuffer.length !=  equator - (equator % METASIZE)) {
				resetSpill

	collect
		if(!spillInProgress)
			if ((kvbend + METASIZE) % kvbuffer.length !=
			    equator - (equator % METASIZE)) {
			  // spill finished, reclaim space
			  resetSpill();


