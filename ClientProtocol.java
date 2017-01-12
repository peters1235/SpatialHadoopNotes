/** 
 * Protocol that a JobClient and the central JobTracker use to communicate.  The
 * JobClient can use these methods to submit a Job for execution, and learn about
 * the current system status.
 */ 
public interface ClientProtocol extends VersionedProtocol {

两个类实现此接口

LocalJobRunner

YarnRunner