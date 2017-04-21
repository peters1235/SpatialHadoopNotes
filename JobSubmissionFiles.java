
package org.apache.hadoop.mapreduce;

/**
 * A utility to manage job submission files.
 */
public class JobSubmissionFiles {
    static Path getJobSplitFile(Path jobSubmissionDir) {
        return new Path(jobSubmissionDir, "job.split");

    static Path getJobSplitMetaFile(Path jobSubmissionDir) {
        return new Path(jobSubmissionDir, "job.splitmetainfo");


    /**
     * 根据使用的是LocalJobRunner还是YarnRunner ,在所使用的文件系统上
     * 创建一个 staging directory并返回其路径.同时记录相关的权限信息  
     */
    public static Path getStagingDir(Cluster cluster, Configuration conf) 
    	//Cluster里有ClientProtocol，由它返回一个路径，返回路径如果存在的话，检查其权限信息，不存在的话，创建之。
        Path stagingArea = cluster.getStagingAreaDir();
            return new Path(client.getStagingAreaDir());
                /*
                LocalJobRunner

                YarnRunner 返回不同的相对路径
                */

        FileSystem fs = stagingArea.getFileSystem(conf);
        String realUser;
        String currentUser;
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        realUser = ugi.getShortUserName();
        currentUser = UserGroupInformation.getCurrentUser().getShortUserName();
        if (fs.exists(stagingArea)) {
            FileStatus fsStatus = fs.getFileStatus(stagingArea);
            String owner = fsStatus.getOwner();
            if (!(owner.equals(currentUser) || owner.equals(realUser))) {
               throw new IOException("The ownership on the staging directory " +
                            stagingArea + " is not as expected. " +
                            "It is owned by " + owner + ". The directory must " +
                            "be owned by the submitter " + currentUser + " or " +
                            "by " + realUser);
            }
            if (!fsStatus.getPermission().equals(JOB_DIR_PERMISSION)) {
                LOG.info("Permissions on staging directory " + stagingArea + " are " +
                    "incorrect: " + fsStatus.getPermission() + ". Fixing permissions " +
                    "to correct value " + JOB_DIR_PERMISSION);
                fs.setPermission(stagingArea, JOB_DIR_PERMISSION);
            }
        } else {
            fs.mkdirs(stagingArea, 
                new FsPermission(JOB_DIR_PERMISSION));
        }
        return stagingArea;
     