public interface MapOutputCollector<K, V> {
    public void init(Context context
                   
    public void collect(K key, V value, int partition
                   
    public void close()
      
    public void flush()
      
    public static class Context {
        private final MapTask mapTask;
        private final JobConf jobConf;
        private final TaskReporter reporter;
    
        public Context(MapTask mapTask, JobConf jobConf, TaskReporter reporter) {
            this.mapTask = mapTask;
            this.jobConf = jobConf;
            this.reporter = reporter;  