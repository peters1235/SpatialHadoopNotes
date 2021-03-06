package org.apache.hadoop.mapreduce.split;
public class JobSplitWriter {
    public static void createSplitFiles(Path jobSubmitDir, 
        Configuration conf, FileSystem fs, 
        org.apache.hadoop.mapred.InputSplit[] splits) 
    
        FSDataOutputStream out = createFile(fs, JobSubmissionFiles.getJobSplitFile(jobSubmitDir), conf);
            return new Path(jobSubmissionDir, "job.split");
            FSDataOutputStream out = FileSystem.create(fs, splitFile, 
                new FsPermission(JobSubmissionFiles.JOB_FILE_PERMISSION));
            int replication = job.getInt(Job.SUBMIT_REPLICATION, 10);
            fs.setReplication(splitFile, (short)replication);
            writeSplitHeader(out);
                out.write(SPLIT_FILE_HEADER);
                out.writeInt(splitVersion);
            return out;
                                                  
        SplitMetaInfo[] info = writeNewSplits(splits, out, conf);
        out.close();
        //写入元数据文件头以及各个SplitMetaInfo
        writeJobSplitMetaInfo(fs,JobSubmissionFiles.getJobSplitMetaFile(jobSubmitDir), 
          new FsPermission(JobSubmissionFiles.JOB_FILE_PERMISSION), splitVersion,
          info);
            // write the splits meta-info to a file for the job tracker
            FSDataOutputStream out = 
              FileSystem.create(fs, filename, p);
            out.write(JobSplit.META_SPLIT_FILE_HEADER);
            WritableUtils.writeVInt(out, splitMetaInfoVersion);
            WritableUtils.writeVInt(out, allSplitMetaInfo.length);
            for (JobSplit.SplitMetaInfo splitMetaInfo : allSplitMetaInfo) {
              splitMetaInfo.write(out);
            }
            out.close();
   
    //Split由两部分信息组成， 两部分供不同的类使用，参见JobSplit类的说明   
    //把各个分片的信息序列化到job.split 文件中
    //同时用SplitMetaInfo【】 记录各个分片的元数据 ：所在机器，在job.split文件中的开始位置，分片中数据的字节数
    static SplitMetaInfo[] writeNewSplits( org.apache.hadoop.mapred.InputSplit[] splits,
          FSDataOutputStream out, Configuration conf)  
      
        SplitMetaInfo[] info = new SplitMetaInfo[splits.length];
        //splits 数目大于0 才往下
        int i = 0;
        long offset = out.getPos();
        int maxBlockLocations = conf.getInt(MRConfig.MAX_BLOCK_LOCATIONS_KEY,
            MRConfig.MAX_BLOCK_LOCATIONS_DEFAULT);
        for(org.apache.hadoop.mapred.InputSplit split: splits) {
            long prevLen = out.getPos();
            Text.writeString(out, split.getClass().getName());
            split.write(out);
            long currLen = out.getPos();
            String[] locations = split.getLocations();
            if (locations.length > maxBlockLocations) {
              LOG.warn("Max block location exceeded for split: "
                  + split + " splitsize: " + locations.length +
                  " maxsize: " + maxBlockLocations);
              locations = Arrays.copyOf(locations,maxBlockLocations);
            }
            info[i++] = new JobSplit.SplitMetaInfo( 
                locations, offset,
                split.getLength());
            offset += currLen - prevLen;
        }
         
        return info;


    private static FSDataOutputStream createFile(FileSystem fs, Path splitFile, Configuration job)   
        FSDataOutputStream out = FileSystem.create(fs, splitFile, 
            new FsPermission(JobSubmissionFiles.JOB_FILE_PERMISSION));
        int replication = job.getInt(Job.SUBMIT_REPLICATION, 10);
        fs.setReplication(splitFile, (short)replication);
        writeSplitHeader(out);
        return out;
    
    private static void writeSplitHeader(FSDataOutputStream out) 
        //标记是
        out.write(SPLIT_FILE_HEADER); //"SPL".getBytes("UTF-8");
        out.writeInt(splitVersion);
     
  



package org.apache.hadoop.mapreduce.split;
/**
*元数据供JobTracker使用，纯Split信息供map使用，两部分信息写入两个不同的文件 
 * This class groups the fundamental classes associated with
 * reading/writing splits. The split information is divided into
 * two parts based on the consumer of the information. The two
 * parts are the split meta information, and the raw split 
 * information. The first part is consumed by the JobTracker to
 * create the tasks' locality data structures. The second part is
 * used by the maps at runtime to know what to do!
 * These pieces of information are written to two separate files.
 * The metainformation file is slurped by the JobTracker during 
 * job initialization. A map task gets the meta information during
 * the launch and it reads the raw split bytes directly from the 
 * file.
 */
public class JobSplit {
    public static class SplitMetaInfo implements Writable {
        private long startOffset;
        private long inputDataLength;
        private String[] locations;    