interface TaskAttemptContext
//The context for task attempts.
	TaskAttemptID getTaskAttemptID();
 
    //Set the current status of the task to the given string.    
	public void setStatus(String msg);

    //return the current status message
    public String getStatus();

    //0到1 之间
    public abstract float getProgress();

    public Counter getCounter(Enum<?> counterName);

    public Counter getCounter(String groupName, String counterName);
