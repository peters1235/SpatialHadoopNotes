package org.apache.hadoop.mapred;
//代表一个分区的索引信息

public   IndexRecord {
	long startOffset;
	long rawLength;
	long partLength;

	IndexRecord() { }

	IndexRecord(long startOffset, long rawLength, long partLength) {
	    this.startOffset = startOffset;
	    this.rawLength = rawLength;
	    this.partLength = partLength;