interface Transaction extends Closeable
	void commit()	
	void rollback() 	
	void close(	