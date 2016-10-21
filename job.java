job
	setJarByClass(Class<?> cls) {
		ensureState(JobState.DEFINE);
		JobConf:conf.setJarByClass(cls);
			