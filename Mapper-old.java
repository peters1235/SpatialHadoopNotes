package org.apache.hadoop.mapred;
interface Mapper<K1, V1, K2, V2> extends JobConfigurable, Closeable {
	void map(K1 key, V1 value, OutputCollector<K2, V2> output, Reporter reporter)