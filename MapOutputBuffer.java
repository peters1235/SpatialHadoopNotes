package org.apache.hadoop.mapred;

static class MapOutputBuffer<K extends Object, V extends Object>
    implements MapOutputCollector<K, V>, IndexedSortable

    int bufmark;            // marks end of record 指向最后一条记录
    IntBuffer kvmeta;
    static final int NMETA = 4;            // num meta ints
    static final int METASIZE = NMETA * 4; // size in bytes
    int softLimit; //已用空间达到这个值之后，开始Spill
    int partitions; //分区数目，也即Reduce任务数
    int numSpills = 0;  //输出的spill文件数目

    void init(MapOutputCollector.Context context)
        float spillper = job.getFloat(JobContext.MAP_SORT_SPILL_PERCENT, (float)0.8);
       
        
        partitions = job.getNumReduceTasks();

        int maxMemUsage = sortmb << 20; // 100 <<20 = 104857600
        maxMemUsage -= maxMemUsage % METASIZE; //没变
        kvbuffer = new byte[maxMemUsage];  //kvbuffer是一个字节数组

        bufvoid = kvbuffer.length; //指向数组最末的位置

        kvmeta = ByteBuffer.wrap(kvbuffer)
           .order(ByteOrder.nativeOrder())
           //Creates a view of this byte buffer as an int buffer.把这个Byte数组视为一个IntBuffer
           .asIntBuffer();

        setEquator(0);
        bufstart = bufend = bufindex = equator;
        kvstart = kvend = kvindex;

        //kvmeta.capacity() 表示的是缓冲区总共能放多少个整型 =》 缓冲区大小（单位：字节） /4 即整型数，一个键值对的元数据
        //用4个整型记录，故再除以4 
        //就得到这个缓冲区总共能放多少条 元数据
        maxRec = kvmeta.capacity() / NMETA; //kvmeta.capacity()== kvbuffer的大小/4  所以kvbuffer 是4个4个的往上走的

        softLimit = (int)(kvbuffer.length * spillper);
        //最开始的剩余空间=SoftLimit
        bufferRemaining = softLimit;

        /*
        100MB  合 104857600 字节
        初始化之后日志：

        17/01/05 04:20:47 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
        17/01/05 04:28:34 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
        17/01/05 04:28:38 INFO mapred.MapTask: soft limit at 83886080
        17/01/05 04:28:53 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
        17/01/05 04:29:55 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
        */

        //刚开始没有Spill
        spillInProgress = false;

        minSpillsForCombine = job.getInt(JobContext.MAP_COMBINE_MIN_SPILLS, 3); //默认3

        spillThread.setDaemon(true); //后台线程
        spillThread.setName("SpillThread");
        spillLock.lock();
        try {
            spillThread.start();
            /* 
            spillThreadRunning 只表示spill线程是否已启动，spillInProgress 才表示 spill 是否已启动。这两者不一样。 线程启动了，spill不一定开始了，spill结束了，线程可能还是启动着的，准备下一次spill

            spillDone 条件表示 spill线程启动与否
            spillReady 则表演 是否要开始spill
            因此从这里进去 启动spill线程之后， spill线程会在  spillReady 这个条件之里停下
            直到后面 缓冲区中的数据足够多了之后，触发spill的条件才 spillReady 才会放行，才开始 sortAndSpill开始 。前面知道 spillReady.signal只在startSpill中调用，也印证了此处的说法
            */
            while (!spillThreadRunning) 
                //最开始spill线程没启动，故进入循环
                spillDone.await();
                //await之后，交出锁，开始等待，等spill线程完事
             
        } catch (InterruptedException e) {
            throw new IOException("Spill thread failed to initialize", e);
        } finally {
            spillLock.unlock();
        
        if (sortSpillException != null) {
          throw new IOException("Spill thread failed to initialize",
              sortSpillException);
    /*
      设置序列化好的键值对 与 键值对的元数据 在 环形缓冲区中的 分隔点
       Set the point from which meta and serialization data expand. 
       没看懂  The meta
       indices are aligned with the buffer, so metadata never spans the ends of
      the circular buffer.
    */
    private void setEquator(int pos) {
        /*最开始pos=0， kvbuffer.length = 104857600 ，算出来 kvindex = (104857600-16)/4 = 26214396  */
        equator = pos;
        // set index prior to first entry, aligned at meta boundary
        //aligned 是去掉不能被 METASIZE整除的部分，因为称之为对齐
        final int aligned = pos - (pos % METASIZE);
        // Cast one of the operands to long to avoid integer overflow
        //这里除了4，所以，后面kvindex每+1 ，应该就相当于在缓冲区中要前进4
        kvindex = (int)
          (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;
        LOG.info("(EQUATOR) " + pos + " kvi " + kvindex +
            "(" + (kvindex * 4) + ")");


    /*
      一条元数组 4个整型， 1个整型4个字节
     * For the given meta position, return the offset into the int-sized
     * kvmeta buffer.
     metapos代表第几条元数据，此函数从元数据条数 返回的是这条元数据 在缓存中从第几个字节开始
     返回值 再 +0 +1 +2 +3  就表示 什么 key 的索引 value的索引 value长度之类的了
     */
    int offsetFor(int metapos) {
      return metapos * NMETA;
    }


    //对第mi 和mj条元数据，首先根据其 分区 然后 是键的值 来 进行
    //这个比较的结果 就是 移动元数据的依据， 先按分区排，分区内按 键 有序

    public int compare(final int mi, final int mj) {
        final int kvi = offsetFor(mi % maxRec);
        final int kvj = offsetFor(mj % maxRec);
        final int kvip = kvmeta.get(kvi + PARTITION);
        final int kvjp = kvmeta.get(kvj + PARTITION);
        // sort by partition
        if (kvip != kvjp) {
            return kvip - kvjp;
        }
        // sort by key
        return comparator.compare(kvbuffer,
            kvmeta.get(kvi + KEYSTART),
            kvmeta.get(kvi + VALSTART) - kvmeta.get(kvi + KEYSTART),
            kvbuffer,
            kvmeta.get(kvj + KEYSTART),
            kvmeta.get(kvj + VALSTART) - kvmeta.get(kvj + KEYSTART));
      
    final byte META_BUFFER_TMP[] = new byte[METASIZE];
    /* 交换元数据的存储位置
     * Swap metadata for items i, j
          i到tmp  j到i， tmp再到j
        这个方法加上上面的compare方法，可以将元数据按 分区、键 重排 
     */
    public void swap(final int mi, final int mj) {
        int iOff = (mi % maxRec) * METASIZE;
        int jOff = (mj % maxRec) * METASIZE;
        System.arraycopy(kvbuffer, iOff, META_BUFFER_TMP, 0, METASIZE);
        System.arraycopy(kvbuffer, jOff, kvbuffer, iOff, METASIZE);
        System.arraycopy(META_BUFFER_TMP, 0, kvbuffer, jOff, METASIZE);  

    /**
     * Serialize the key, value to intermediate storage.
     * When this method returns, kvindex must refer to sufficient unused
     * storage to store one METADATA.
     *将键值对写入环形缓冲区，这个方法返回时kvindex应当指向至少有 METADATA 个存储空间的
     *位置
     */
    public synchronized void collect(K key, V value, final int partition
        reporter.progress();
        //key  value的类型不对的话，抛出异常
        //写数据之前，先留足写入元数据的空间，等到真正写入数据的时候在Buffer的Write中再从
        //bufferRemaining中减去数据的长度，这样写完一个键值对之后 ，bufferRemaining中减去了数据和元数据的总长
        bufferRemaining -= METASIZE;
        //如果环形缓冲区的已用空间达到soft Limit—— 整个缓冲区大小 乘以 一个百分比，一般0.8
        //并且spill 线程又没开妈工作的话， 开始Spill
        if (bufferRemaining <= 0) {
            // start spill if the thread is not running and the soft limit has been
            // reached
  
  
            /* 
            取锁，取到的话立即返回，否则进行睡眠状态，直到取到锁
            Acquires the lock if it is not held by another thread and returns immediately, setting the lock hold count to one.

            If the current thread already holds the lock then the hold count is incremented by one and the method returns immediately.

            If the lock is held by another thread then the current thread becomes disabled for thread scheduling purposes and lies dormant 
            until the lock has been acquired, at which time the lock hold count is set to one.
                      */
            spillLock.lock();
            try
              //只循环一次while里为false，真奇怪
                do                    
                    if (!spillInProgress) 
                        //spill要么未开始 ，要么已完成

                        final int kvbidx = 4 * kvindex;
                        final int kvbend = 4 * kvend;
      
                        // serialized, unspilled bytes always lie between kvindex and
                        // bufindex, crossing the equator. 待Spill的字节处于 kvindex 和 bufindex之间，
                        //这部分空间跨equator.后面这部分没看懂了Note that any void space
                        // created by a reset must be included in "used" bytes
                        
                        //bUsed应该是指待Spill的字节数， 这样KvIndex就是按 整型递增的，而bufindex则是按字节递增的，所以
                        //kvindex才要*4
                        final int bUsed = distanceTo(kvbidx, bufindex);
                        final boolean bufsoftlimit = bUsed >= softLimit; // L1097

                        if ((kvbend + METASIZE) % kvbuffer.length !=  equator - (equator % METASIZE)) {
                            // Spill没开始的话，上面的条件表达式两边应该相等，所以spill已完成
                            resetSpill()
                            //这两个值，最小值应该一直是 第一个吧
                            bufferRemaining = Math.min(
                                distanceTo(bufindex, kvbidx) - 2 * METASIZE,
                                softLimit - bUsed) 

                                - METASIZE;
                            continue; //没意义                        
                        //上面是spill已经完成，到这里就只有spill未开始吧
                        //spill未开始，还得已经有数据了 才能开始spill
                        //在 Init里kvindex  == kvend，只有后面写入数据了 kvindex 才会不等于kvend
                        else if (bufsoftlimit && kvindex != kvend) {
                            // spill records, if any collected; check latter, as it may
                            // be possible for metadata alignment to hit spill pcnt
                            //开始Spill
                            startSpill();
                            //估算记录的大小 ，只有键值对，不包含元数据
                            final int avgRec = (int)
                              (mapOutputByteCounter.getCounter() /
                              mapOutputRecordCounter.getCounter());

                            // leave at least half the split buffer for serialization data
                            // ensure that kvindex >= bufindex
                            //这部分大概是重置Equator，那个max里头套min没看明白，
                              //能看到一部分是取 剩余空间的中点做新的Equator

                              /*
                              METASIZE + avgRec 应该是一条记录的 键值对+ 元数据 所需的 平均字节数
                              distkvi / (METASIZE + avgRec) 是剩余的空间 能存放的 记录条数 
                              distkvi / (METASIZE + avgRec) * METASIZE 则应该是 这些条数的记录 的元数据所需占用的空间， 设为m

                              就是这样，因为 newPos就是 新的Equator的位置 ，它= bufindex + m，
                              就是说,如果缓存区是顺时针方向 存放 键值对的，则bufindex 位置再 顺时针 前进m 是新的equator, 按照缓冲区的使用方式，equator 顺时针往前的空间存放 键值 ，逆时针往后的空间存放 元数据，而这部分 元数据 空间刚好还有m 。

                              */
                            final int distkvi = distanceTo(bufindex, kvbidx);
                            final int newPos = (bufindex +
                                Math.max(2 * METASIZE - 1,
                                      Math.min(distkvi / 2,
                                               distkvi / (METASIZE + avgRec) * METASIZE)))
                              % kvbuffer.length;
                            setEquator(newPos);
                            //重置bufmark 重置 bufindex
                            bufmark = bufindex = newPos;
    
                while（false) 
            finally {
                spillLock.unlock()

            try //L1143

                // 将key序列化到缓存中， 从bufindex开始写起
                int keystart = bufindex;
                keySerializer.serialize(key);
                //没看懂
                if (bufindex < keystart) {
                    // wrapped the key; must make contiguous
                    bb.shiftBufferedKey();
                    keystart = 0;

                //将Value序列化
                final int valstart = bufindex;
                valSerializer.serialize(value);

                //干啥？处理value为空的情况？
                // It's possible for records to have zero length, i.e. the serializer
                // will perform no writes. To ensure that the boundary conditions are
                // checked and that the kvindex invariant is maintained, perform a
                // zero-length write into the buffer. The logic monitoring this could be
                // moved into collect, but this is cleaner and inexpensive. For now, it
                // is acceptable.
                bb.write(b0, 0, 0);

                // the record must be marked after the preceding write, as the metadata
                // for this record are not yet written
                int valend = bb.markRecord();

                //更新Counter
                mapOutputRecordCounter.increment(1);
                mapOutputByteCounter.increment(
                    distanceTo(keystart, valend, bufvoid));



                // write accounting info 写入元数据信息，代码中叫accounting info
                kvmeta.put(kvindex + PARTITION, partition);
                kvmeta.put(kvindex + KEYSTART, keystart);
                kvmeta.put(kvindex + VALSTART, valstart);
                kvmeta.put(kvindex + VALLEN, distanceTo(valstart, valend));
                // advance kvindex  kvindex 从屁股后面往前挪，逆时针往头部转
                kvindex = (kvindex - NMETA + kvmeta.capacity()) % kvmeta.capacity();


            } catch (MapBufferTooSmallException e) {
                LOG.info("Record too large for in-memory buffer: " + e.getMessage());
                spillSingleRecord(key, value, partition);
                mapOutputRecordCounter.increment(1);
                return;

    void flush()
        LOG.info("Starting flush of map output");
        spillLock.lock();
        try {
          while (spillInProgress) {
            reporter.progress();
            spillDone.await();
          }
          checkSpillException();

          final int kvbend = 4 * kvend;
          if ((kvbend + METASIZE) % kvbuffer.length !=
              equator - (equator % METASIZE)) {
            // spill finished
            resetSpill();
          }
          if (kvindex != kvend) {
            kvend = (kvindex + NMETA) % kvmeta.capacity();
            bufend = bufmark;
            LOG.info("Spilling map output");
            LOG.info("bufstart = " + bufstart + "; bufend = " + bufmark +
                     "; bufvoid = " + bufvoid);
            LOG.info("kvstart = " + kvstart + "(" + (kvstart * 4) +
                     "); kvend = " + kvend + "(" + (kvend * 4) +
                     "); length = " + (distanceTo(kvend, kvstart,
                           kvmeta.capacity()) + 1) + "/" + maxRec);
            sortAndSpill();
          }
        } catch (InterruptedException e) {
          throw new IOException("Interrupted while waiting for the writer", e);
        } finally {
          spillLock.unlock();
        }
        assert !spillLock.isHeldByCurrentThread();
        // shut down spill thread and wait for it to exit. Since the preceding
        // ensures that it is finished with its work (and sortAndSpill did not
        // throw), we elect to use an interrupt instead of setting a flag.
        // Spilling simultaneously from this thread while the spill thread
        // finishes its work might be both a useful way to extend this and also
        // sufficient motivation for the latter approach.
        try {
          spillThread.interrupt();
          spillThread.join();
        } catch (InterruptedException e) {
          throw new IOException("Spill failed", e);
        }
        // release sort buffer before the merge
        kvbuffer = null;
        mergeParts();
        Path outputPath = mapOutputFile.getOutputFile();
        fileOutputByteCounter.increment(rfs.getFileStatus(outputPath).getLen());

    protected class SpillThread extends Thread {
        public void run() {
            //拿到spillDone.await 交出的锁，开始干活
            spillLock.lock();
            //只在开始的时候设为true，完事之后设为false
            spillThreadRunning = true;
            try {
                //spill线程应该是一直起着的，任务结束时才结束
                while (true) {
                    //只有这一处释放，init 、Buffer.write、flush 里都有await
                    //这里告诉 MapOutputBuffer的init里的 spillDone.await ,可以继续了
                    //但是只是下面这句 还没有把锁 交出去

                    spillDone.signal();
                    /* 刚启动spill线程时 还没有 开始 spill工作，因此会进入下面的循环
                    并且由 spillReady 释放掉锁，spillReady spillDone 关联的锁都是 spillLock
                    因此 spillReady释放锁之后 ， init里 的spillDone.await 这会有了 上面的  signal 又有了空闲的锁，于是可以继续干活了。

                    */
                    while (!spillInProgress) {
                        //只有startSpill会调用spillReady.signal
                        spillReady.await();
                    }
                    //也就是说，初始化工作完成之后 ，spill线程会停在上面的 spillReady.await()那里
                    //等到 缓冲区写满之后 ，才开始 下面的 sortAndSpill
                    try {
                        spillLock.unlock();
                        sortAndSpill();
                    } catch (Throwable t) {
                        sortSpillException = t;
                    } finally {
                        spillLock.lock();
                        if (bufend < bufstart) {
                            bufvoid = kvbuffer.length;
                        }
                        //kvstart往前移，这次从kvstart spill到了kvend ,
                        //下次从这次的 kvend 开始，到下次的kvindex 后面4个字节
                        /*bufend在刚开始的时候 == bufstart
                        startSpill 的时候， 把kvend 指向最后一条元数据，
                        把bufend指向最后一条键值对， spill的时候  kvstart-kvend bufstart-bufend之间的数据
                        要用来输出。
                        spill完了之后 ，从上次的kvend  bufend处重新开始准备spill，就是在这里
                        把bufstart指向现在的bufend， 把kvstart指向kvend
                        */
                        kvstart = kvend;
                     
                        bufstart = bufend;
                        spillInProgress = false;
                    }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                spillLock.unlock();
                spillThreadRunning = false;
                
    class Buffer extends OutputStream {
        final byte[] scratch = new byte[1];
  
        //实际写入一个下整型时，这个方法会被调用4次，那个整型会被拆成4个字节，每个字节调用一次这个方法       
        void write(int v)
             
            scratch[0] = (byte)v;
            write(scratch, 0, 1);
        }
  
        /*
          向缓冲区中写入一串字节。如果the spill thread is running and it
          cannot write. 的话，本方法会 停下来
         * Attempt to write a sequence of bytes to the collection buffer.
         * This method will block if the spill thread is running and it
         * cannot write.
         *  好像只写了键值数据，没有写元数据。写元数据的操作在MapOutputBuffer的collect方法里
         */
        void write(byte b[], int off, int len)
            // must always verify the invariant that at least METASIZE bytes are
            // available beyond kvindex, even when len == 0
            //是说要保障写完数据还有空间写元数据吧
            bufferRemaining -= len;
            if (bufferRemaining <= 0) {
                // writing these bytes could exhaust available buffer space or fill
                // the buffer to soft limit. check if spill or blocking are necessary
                //看是要开始spill还是 暂停写入
                boolean blockwrite = false;
                spillLock.lock();
               
                try
                    do
                        checkSpillException();

                        final int kvbidx = 4 * kvindex;
                        final int kvbend = 4 * kvend;
                        // ser distance to key index
                        final int distkvi = distanceTo(bufindex, kvbidx);
                        // ser distance to spill end index
                        final int distkve = distanceTo(bufindex, kvbend);

                        //最难懂了
                        blockwrite = distkvi <= distkve
                        
                        /*
                        如果kvindex 比 kvend 离bufindex近，则 spill 肯定没有开始，
                        也肯定 不是已经完成并且已经调用了resetSpill的状态
                        如果已经开始，则在startSpill里 kvend = kvindex + NMETA ，kvend会离得远些
                        如果已经结束并且调用了resetSpill则在setEquator 和 resetSpill里  kvindex和kvend
                        都已经指向 Equator后4个字节的位置了

                        */   
                        // if kvindex is closer than kvend, then a spill is neither in
                        // progress nor complete and reset since the lock was held.

                        // The write should block only if there is insufficient space to
                        // complete the current write, write the metadata for this record,
                        // and write the metadata for the next record.  

                        ? distkvi <= len + 2 * METASIZE  
                        //这种情况什么时候能出现？
                        //If kvend is closer,
                        // then the write should block if there is too little space for either 
                        //the metadata or the current write. 
                        //Note that collect
                        // ensures its metadata requirement with a zero-length write
                        : distkve <= len || distanceTo(bufend, kvbidx) < 2 * METASIZE;
                        //还没开始spill的话，得开始spill，但是不一定得暂停写入，
                        //接下来应该是开始spill ,并且判断 要不要 暂停写入  
                        if (!spillInProgress 
                            && blockwrite) {
                           
                            if ((kvbend + METASIZE) % kvbuffer.length !=
                                equator - (equator % METASIZE)) {
                                // spill finished, reclaim space
                                // need to use meta exclusively; zero-len rec & 100% spill
                                // pcnt would fail
                                resetSpill(); // resetSpill doesn't move bufindex, kvindex
                                bufferRemaining = Math.min(
                                    distkvi - 2 * METASIZE,
                                    softLimit - distanceTo(kvbidx, bufindex)) - len;
                                continue;
                            }
                            // we have records we can spill; only spill if blocked
                            //init完之后  kvindex == kvend 
                            if (kvindex != kvend) {
                                //能进来的话，就是已经写入了记录的
                                startSpill();
                                // Blocked on this write, waiting for the spill just
                                // initiated to finish. 后面这句什么意思 ？Instead of repositioning the marker
                                // and copying the partial record, we set the record start
                                // to be the new equator
                                setEquator(bufmark);
                            } else {
                                // We have no buffered records, and this record is too large
                                // to write into kvbuffer. We must spill it directly from
                                // collect
                                //大概是指单条记录缓冲区放不下，得单个spill吧
                                final int size = distanceTo(bufstart, bufindex) + len;
                                setEquator(0);
                                bufstart = bufend = bufindex = equator;
                                kvstart = kvend = kvindex;
                                bufvoid = kvbuffer.length;
                                throw new MapBufferTooSmallException(size + " bytes");
                            }


                        //如果要阻塞的话， 会在这里循环，等到spill完了才算。
                        //这样看来， 外面的do while循环看起来 应该没机会重复执行了
                        //可能是到这下面来话，确实会在里面的while里转，但是还有可能  上面continue 之后 直接就到 外面的while了    
                        if (blockwrite)                      
                            while (spillInProgress) {
                                reporter.progress();
                                spillDone.await();

                          

                    //要暂停写入的话，就一直在这里循环
                    while(blockwrite)

                finally
                    spillLock.unlock();

            // here, we know that we have sufficient space to write
            //写到头了话，先把尾部空间填满
            if (bufindex + len > bufvoid) {
                final int gaplen = bufvoid - bufindex;
                System.arraycopy(b, off, kvbuffer, bufindex, gaplen);
                len -= gaplen;
                off += gaplen;
                //因为是环形缓冲区，再把写入位置移到缓冲区开头
                bufindex = 0;
            //继续写，环形缓冲区的头尾是接在一块的，可以连着写，只是要注意底下实际是一个 顺序的数组，因此要修改写入位置的索引
            System.arraycopy(b, off, kvbuffer, bufindex, len);
            bufindex += len;   

    /**
     * The spill is complete, so set the buffer and meta indices to be equal to
     * the new equator to free space for continuing collection.  
     * 当kvindex == kvend == kvstart, 时缓冲区为空     
     */
    private void resetSpill() {
        final int e = equator;
        bufstart = bufend = e;
        final int aligned = e - (e % METASIZE);
        // set start/end to point to first meta record
        // Cast one of the operands to long to avoid integer overflow
        kvstart = kvend = (int)
            (((long)aligned - METASIZE + kvbuffer.length) % kvbuffer.length) / 4;
        LOG.info("(RESET) equator " + e + " kv " + kvstart + "(" +
            (kvstart * 4) + ")" + " kvi " + kvindex + "(" + (kvindex * 4) + ")");

    void startSpill() {
        assert !spillInProgress;
        //开始Spill的时候，kvend指向最后一条元数据所在的位置
        kvend = (kvindex + NMETA) % kvmeta.capacity();
        //bufmark 是最后一条记录
        bufend = bufmark;
        //只在这里设为真，在SpillThread的run中，sortAndSpill之后 将spillInProgress置为False
        spillInProgress = true;
        
        LOG.info("Spilling map output");
        LOG.info("bufstart = " + bufstart + "; bufend = " + bufmark +
                 "; bufvoid = " + bufvoid);
        LOG.info("kvstart = " + kvstart + "(" + (kvstart * 4) +
                 "); kvend = " + kvend + "(" + (kvend * 4) +
                 "); length = " + (distanceTo(kvend, kvstart,
                       kvmeta.capacity()) + 1) + "/" + maxRec);
        spillReady.signal();

    void sortAndSpill()
        //approximate the length of the output file to be the length of the
        //buffer + header lengths for the partitions
        //估计输出文件的大小
        final long size = distanceTo(bufstart, bufend, bufvoid) +
                    partitions * APPROX_HEADER_LENGTH;


        final SpillRecord spillRec = new SpillRecord(partitions);
        final Path filename =
            mapOutputFile.getSpillFileForWrite(numSpills, size);
        //生成类似 spill0.out 这样的文件
        out = rfs.create(filename);

        /*kvend为最后一条元数据的起始位置（单位不是字节，而是整型（4个字节）
          这个值如果是100的话，并不是说kvend 当前指向第100条 元数据，而应该 是
          100 / 4 = 25 。kvend的值从kvindex中来， kvindex每次+1 ，只代表一个整型（4个字节）
          即一条 元数据的 4个值 中的一个

          假设环形缓冲区中 键值对 是 顺时针往前 记的， 则 元数据 是逆时针记的，
          startSpill中把kvend 指向了最后一条 元数据 结束的位置，这个值 除以 4的话，相当于顺时针过来 能放的 元数据的条数，从这个地方开始，直到最末尾，
          就是 需要排序的元数据。没说明白，大概是这样
        */

        final int mstart = kvend / NMETA;

        //一般情况应该就是kvstart,有kvstart <kvend的情况么？
        /*
        我去，还真有，如果是equator = 0 的情况，自然是 kvstart >= kvend
        如果 equator  只是稍大于0，在表盘上0到3点之间，则kvstart equator后面也在0到3点之间
        而kvend 则随着 元数据的增加 ，会向左越过0点，这样就会出现  kvend > kvstart的情况
        如果是这种情况，则相当于传给 sort 的索引  是要加一圈的，而在 MapOutputBuffer的 cmopare里
        头又对这种 加一圈的情况用  final int kvi = offsetFor(mi % maxRec); 进行了还原。。。
        真费劲啊
        */
        final int mend = 1 + // kvend is a valid record
                  (kvstart >= kvend
                  ? kvstart
                  : kvmeta.capacity() + kvstart) / NMETA;

        //这一步应该就是对 元数据排序了                  
        sorter.sort(MapOutputBuffer.this, mstart, mend, reporter);

        //用spindex来遍历所有元数据
        int spindex = mstart;

        final IndexRecord rec = new IndexRecord();
        //InMemValBytes 继承自DataInputBuffer，类似地用reset方法记下 value的起始位置、长度，但是
        //多了一步 处理什么 最后一条记录的内容
        final InMemValBytes value = new InMemValBytes();
        //把缓冲区中的内容按分区输出
        for (int i = 0; i < partitions; ++i) {
            //分区的起始位置
            long segmentStart = out.getPos();

            //可能是要加密之类的
            FSDataOutputStream partitionOut = CryptoUtils.wrapIfNecessary(job, out);
            //用于输出 spilln.out文件的类
            writer = new Writer<K, V>(job, partitionOut, keyClass, valClass, codec,
                                      spilledRecordsCounter);
            //如果不需要Combine则直接输出
            if (combinerRunner == null) {
                //这个buffer用来从字节流中读取数据，不用再创建 DataInputStream and ByteArrayInputStream
                DataInputBuffer key = new DataInputBuffer();
                while (spindex < mend &&
                    //当前元数据对应的分区号== 要输出的分区号
                    kvmeta.get(offsetFor(spindex % maxRec) + PARTITION) == i) {
                    
                    final int kvoff = offsetFor(spindex % maxRec);
                    int keystart = kvmeta.get(kvoff + KEYSTART);
                    int valstart = kvmeta.get(kvoff + VALSTART);
                    //把key的起始位置 长度 记下来了，传入writer中便可取到正确的值
                    key.reset(kvbuffer, keystart, valstart - keystart);
                    //value类似
                    getVBytesForOffset(kvoff, value);
                    writer.append(key, value);
                    ++spindex; //下一条元数据
            else
                int spstart = spindex;

            writer.close();

            //记录分区的元数据 segmentStart是本分区在spill.out文件中的 起始位置
            rec.startOffset = segmentStart;
            rec.rawLength = writer.getRawLength() + CryptoUtils.cryptoPadding(job);
            rec.partLength = writer.getCompressedLength() + CryptoUtils.cryptoPadding(job);
            //分区元数据信息放入spillRec中，第i条代表第i个分区
            spillRec.putIndex(rec, i);

            //分区索引如果太大的话，输出之
            if (totalIndexCacheMemory >= indexCacheMemoryLimit) {
                // create spill index file
                Path indexFilename =
                    mapOutputFile.getSpillIndexFileForWrite(numSpills, partitions
                        * MAP_OUTPUT_INDEX_RECORD_LENGTH);
                spillRec.writeToFile(indexFilename, job);
            } else {
                indexCacheList.add(spillRec);
                totalIndexCacheMemory +=
                    spillRec.size() * MAP_OUTPUT_INDEX_RECORD_LENGTH;
  
            LOG.info("Finished spill " + numSpills);
            ++numSpills;

    /**
     * Inner class managing the spill of serialized records to disk.
     */
    protected class BlockingBuffer extends DataOutputStream {

        //DataOutputStream 可以直接输出 基本类型Primitive，也就是说可以直接拿一个int，写入流。
        //OutputStream则是字节流，要写入int得把int转成字节流，再写入
        //OutputStream 外面要再包一层DataOutputStream才能 直接写入基本类型的数据
        public BlockingBuffer() {
          super(new Buffer());
        
  
        /**
         * Mark end of record. Note that this is required if the buffer is to
         * cut the spill in the proper place.
         */
        public int markRecord() {
            bufmark = bufindex;
            return bufindex;
       
  
        /*
         * Set position from last mark to end of writable buffer, then rewrite
         * the data between last mark and kvindex.
         * This handles a special case where the key wraps around the buffer.
         * If the key is to be passed to a RawComparator, then it must be
         * contiguous in the buffer. This recopies the data in the buffer back
         * into itself, but starting at the beginning of the buffer. Note that
         * this method should <b>only</b> be called immediately after detecting
         * this condition. To call it at any other time is undefined and would
         * likely result in data loss or corruption.
        这个方法应该是要把 横跨0位置的 key值  挪到0 位置的右边，这样才能排序
         */
        protected void shiftBufferedKey() throws IOException {
            // spillLock unnecessary; both kvend and kvindex are current
            /*调用这个方法的时机是 一条记录开始的位置和结束的位置横跨 缓冲区的0 值 的左右两边
            刚进来的时候，还没有调用markRecord bufMark指向的还是 上一条记录，也就是说bufmark还是
            指向的0位置 左边。

            这时key已经序列化,bufindex 经过Buffer.write之后，已经指向0右边了
            */
            int headbytelen = bufvoid - bufmark;
            bufvoid = bufmark;
            final int kvbidx = 4 * kvindex;
            final int kvbend = 4 * kvend;
            final int avail =
                Math.min(distanceTo(0, kvbidx), distanceTo(0, kvbend));
            if (bufindex + headbytelen < avail) {
                //把0位置右边的部分往前移
                //直接这样写，重叠的部分内容不会被覆盖，重叠部分取值的时候会取旧值，不会取复制之后的值
                System.arraycopy(kvbuffer, 0, kvbuffer, headbytelen, bufindex);
                //0位置左边的部分 移到右边
                //bufvoid已经在前面更新为指向上一条记录的末尾了  

                System.arraycopy(kvbuffer, bufvoid , kvbuffer, 0, headbytelen);
                //bufindex也相应地往前移
                bufindex += headbytelen;
                //key往前移之后 ，左边空下来的空间，不能用了，应该从bufferRemaining中减掉
                //减掉的空间大小 应该是 headbytelen
                bufferRemaining -= kvbuffer.length - bufvoid ;
            } else {
                //这里是什么情况？看着跟上面的操作是一样的，？哪里没看明白吧
                byte[] keytmp = new byte[bufindex];
                System.arraycopy(kvbuffer, 0, keytmp, 0, bufindex);
                bufindex = 0;
                out.write(kvbuffer, bufmark, headbytelen);
                out.write(keytmp);
               