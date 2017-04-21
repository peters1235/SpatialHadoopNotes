//注释与JobContext一样的。
public class JobContextImpl implements JobContext	
	/*
	User and group information for Hadoop.
	 This class wraps around a JAAS Subject and provides methods to determine the user's username and groups.
	 It supports both the Windows, Unix and Kerberos login modules.
	*/
	protected UserGroupInformation ugi;

	//封装了旧的JobConf
	protected org.apache.hadoop.mapred.JobConf conf


	JobContextImpl(Configuration conf,JobID jobId)
		//conf 如果是JobConf对象，直接赋值给自己的成员变量，否则作为参数传给JobConf的构造函数，
		//总之要返回一个JobConf对象	

UserGroupInformation
	/*
	以指定用户的身份执行某个操作
	*/
    public <T> T doAs(PrivilegedAction<T> action) {
    	logPrivilegedAction(subject, action);
    	return Subject.doAs(subject, action);




/*
Subject就像一个人， Princial就像一个人有多个ID，石骞是我， 我的身份证指向的也是我
A Subject represents a grouping of related information for a single entity, 
such as a person. Such information includes the Subject's identities as well as its security-related attributes 
(passwords and cryptographic keys, for example).

Subjects may potentially have multiple identities. 
Each identity is represented as a Principal within the Subject. Principals simply bind names to a Subject. 
For example, a Subject that happens to be a person, Alice, might have two Principals: 
one which binds "Alice Bar", the name on her driver license, to the Subject, and another which binds, 
"999-99-9999", the number on her student identification card, to the Subject. 
Both Principals refer to the same Subject even though each has a different name.

A Subject may also own security-related attributes, which are referred to as credentials. 
Sensitive credentials that require special protection, such as private cryptographic keys, 
are stored within a private credential Set. 
Credentials intended to be shared, such as public key certificates or Kerberos server tickets are stored within a public credential Set. 
Different permissions are required to access and modify the different credential Sets.

To retrieve all the Principals associated with a Subject, invoke the getPrincipals method. 
To retrieve all the public or private credentials belonging to a Subject, 
invoke the getPublicCredentials method or getPrivateCredentials method, respectively. 
To modify the returned Set of Principals and credentials, use the methods defined in the Set class. For example:

      Subject subject;
      Principal principal;
      Object credential;

      // add a Principal and credential to the Subject
      subject.getPrincipals().add(principal);
      subject.getPublicCredentials().add(credential);
 
This Subject class implements Serializable. While the Principals associated with the Subject are serialized, the credentials associated with the Subject are not. Note that the java.security.Principal class does not implement Serializable. Therefore all concrete Principal implementations associated with Subjects must implement Serializable.
*/
class Subject
	Perform work as a particular Subject.

	This method first retrieves the current Thread's AccessControlContext via AccessController.getContext, 
	and then instantiates a new AccessControlContext 
	using the retrieved context along with a new SubjectDomainCombiner (constructed using the provided Subject). 
	Finally, this method invokes AccessController.doPrivileged, passing it the provided PrivilegedAction, 
	as well as the newly constructed AccessControlContext.

	static <T> T doAs(final Subject subject,
	                        final java.security.PrivilegedAction<T> action)


/*
A computation to be performed with privileges enabled. 
The computation is performed by invoking AccessController.doPrivileged on the PrivilegedAction object. 
This interface is used only for computations that do not throw checked exceptions; 
computations that throw checked exceptions must use PrivilegedExceptionAction instead.
*/
public interface PrivilegedAction<T> {

	/*
	Performs the computation. This method will be called by AccessController.doPrivileged after enabling privileges.

	Returns:
	a class-dependent value that may represent the results of the computation. 
	Each class that implements PrivilegedAction should document what (if anything) this value represents.*/
	T run();