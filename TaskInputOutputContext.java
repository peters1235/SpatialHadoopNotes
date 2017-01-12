package org.apache.hadoop.mapreduce;

public interface TaskInputOutputContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
       extends TaskAttemptContext {


    public boolean nextKeyValue()
  
    public KEYIN getCurrentKey()
  
  
    public VALUEIN getCurrentValue() 
  
    public void write(KEYOUT key, VALUEOUT value) 
      
  
    public OutputCommitter getOutputCommitter();
 
