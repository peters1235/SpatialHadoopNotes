package org.apache.hadoop.mapreduce;
 
public abstract class StatusReporter {
  abstract Counter getCounter(Enum<?> name);
  abstract Counter getCounter(String group, String name);
  abstract void progress();
  /**
   * Get the current progress.
   * @return a number between 0.0 and 1.0 (inclusive) indicating the attempt's 
   * progress.
   */
  abstract float getProgress();
  abstract void setStatus(String status);