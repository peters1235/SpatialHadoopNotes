public class YARNRunner implements ClientProtocol {
	ResourceMgrDelegate resMgrDelegate;
	
	String getStagingAreaDir() 
		return this.resMgrDelegate.getStagingAreaDir();

	JobStatus submitJob(JobID jobId, String jobSubmitDir, Credentials ts)
		// Construct necessary information to start the MR AM
		ApplicationSubmissionContext appContext =
		    createApplicationSubmissionContext(conf, jobSubmitDir, ts);

		// 提交给 ResourceManager 
		ApplicationId applicationId = resMgrDelegate.submitApplication(appContext);
			/*
			Submit a new application to YARN. It is a blocking call - it will not return ApplicationId until the submitted application is submitted successfully and accepted by the ResourceManager.
			Users should provide an ApplicationId as part of the parameter ApplicationSubmissionContext when submitting a new application, otherwise it will throw the ApplicationIdNotProvidedException.
			This internally calls (SubmitApplicationRequest), and after that, it internally invokes (GetApplicationReportRequest) and waits till it can make sure that the application gets properly submitted. If RM fails over or RM restart happens before ResourceManager saves the application's state, #getApplicationReport(GetApplicationReportRequest) will throw the ApplicationNotFoundException. 
			This API automatically resubmits the application with the same ApplicationSubmissionContext when it catches the ApplicationNotFoundException
			*/
			return this.client.submitApplication(appContext);


		ApplicationReport appMaster = resMgrDelegate
		      .getApplicationReport(applicationId);