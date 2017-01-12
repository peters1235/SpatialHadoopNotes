package org.apache.hadoop.mapreduce;

public interface MapContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> 
    extends TaskInputOutputContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> {

    /**
     * Get the input split for this map.
     */
    public InputSplit getInputSplit();