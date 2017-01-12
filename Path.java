
package org.apache.hadoop.fs;
//指文件系统中的文件或目录，以/分隔，以/打头的Path是绝对路径
public class Path implements Comparable {

  /** Return the FileSystem that owns this Path. */
  public FileSystem getFileSystem(Configuration conf) throws IOException {
    return FileSystem.get(this.toUri(), conf);
  }