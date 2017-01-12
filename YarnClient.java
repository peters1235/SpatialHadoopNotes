public abstract class YarnClient extends AbstractService {
	/*
	Submit a new application to YARN. It is a blocking call - it will not return ApplicationId until 
	the submitted application is submitted successfully and accepted by the ResourceManager.

	Users should provide an ApplicationId as part of the parameter ApplicationSubmissionContext 
	when submitting a new application, otherwise it will throw the ApplicationIdNotProvidedException.

	This internally calls ApplicationClientProtocol.submitApplication(SubmitApplicationRequest), 
	and after that, it internally invokes ApplicationClientProtocol.getApplicationReport(GetApplicationReportRequest) 
	and waits till it can make sure that the application gets properly submitted. 
	If RM fails over or RM restart happens before ResourceManager saves the application's state, 
	ApplicationClientProtocol.getApplicationReport(GetApplicationReportRequest) will throw the ApplicationNotFoundException. 
	This API automatically resubmits the application with the same ApplicationSubmissionContext 
	when it catches the ApplicationNotFoundException
	*/