/*
要把Map里的CellInfo 换成Rectangle 才能运行，不然ShapeLineInputFormat里输出的是Rectangle
Map要的却是Rectangle的子类，会出现类型转换失败异常，
另外 ，如果要对HDFS上的数据计数的话，outputPath的声明语句里不能有getPath() ，因为有这个方法的话会把
HDFS路径转成本地路径，导致抛出无法创建文件夹的异常
*/
class RecordCount {
	class Map extends MapReduceBase implements Mapper<CellInfo, Text, NullWritable, LongWritable> {
		map(
			output.collect(Dummy, ONEL);

	class Reduce extends MapReduceBase implements Reducer<NullWritable, LongWritable, NullWritable, LongWritable> {
		long total_lines = 0;
		while (values.hasNext()) {
		  LongWritable next = values.next();
		  total_lines += next.get();
		}
		output.collect(dummy, new LongWritable(total_lines));

	long recordCountMapReduce(FileSystem fs, Path inFile)
	    JobConf job = new JobConf(RecordCount.class);
	  
	    //Path outputPath = new Path(inFile.toUri().getPath()+".linecount");
	    Path outputPath = new Path(inFile.toUri()+".linecount");

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setCombinerClass(Reduce.class);


		job.setNumReduceTasks(1);
		
		job.setInputFormat(ShapeLineInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);