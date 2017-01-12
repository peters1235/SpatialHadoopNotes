package org.apache.hadoop.mapreduce.task;
public class MapContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
    extends TaskInputOutputContextImpl<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
    implements MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {