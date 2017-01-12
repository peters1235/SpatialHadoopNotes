/*
The protocol between clients and the ResourceManager to submit/abort jobs and to get information on applications, cluster metrics, nodes, queues and ACLs.
客户端和RM之间 提交、取消job，获取集群、应用的相关信息的协议
*/
public interface ApplicationClientProtocol {


	/*
	The interface used by clients to submit a new application to the ResourceManager.

	The client is required to provide details such as queue, Resource required to run the ApplicationMaster, the equivalent of ContainerLaunchContext for launching the ApplicationMaster etc. via the SubmitApplicationRequest.

	Currently the ResourceManager sends an immediate (empty) SubmitApplicationResponse on accepting the submission and throws an exception if it rejects the submission. However, this call needs to be followed by getApplicationReport(GetApplicationReportRequest) to make sure that the application gets properly submitted - obtaining a SubmitApplicationResponse from ResourceManager doesn't guarantee that RM 'remembers' this application beyond failover or restart. If RM failover or RM restart happens before ResourceManager saves the application's state successfully, the subsequent getApplicationReport(GetApplicationReportRequest) will throw a ApplicationNotFoundException. The Clients need to re-submit the application with the same ApplicationSubmissionContext when it encounters the ApplicationNotFoundException on the getApplicationReport(GetApplicationReportRequest) call.

	During the submission process, it checks whether the application already exists. If the application exists, it will simply return SubmitApplicationResponse

	In secure mode,the ResourceManager verifies access to queues etc. before accepting the application submission.


	*/
	SubmitApplicationResponse submitApplication(SubmitApplicationRequest request) 