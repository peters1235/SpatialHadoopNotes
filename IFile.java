package org.apache.hadoop.mapred;

/*
用于处理 Map 的中间结果。
有Writer 和Reader来实现 读写
*/
public class IFile {

	//用于输出 map的中间结果

	public static class Writer<K extends Object, V extends Object> {