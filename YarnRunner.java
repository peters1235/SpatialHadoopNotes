public class YARNRunner implements ClientProtocol {
	ResourceMgrDelegate resMgrDelegate;
	
	JobStatus submitJob(JobID jobId, String jobSubmitDir, Credentials ts)
		// Construct necessary information to start the MR AM
		ApplicationSubmissionContext appContext =
		    createApplicationSubmissionContext(conf, jobSubmitDir, ts);

		// 提交给 ResourceManager 
		ApplicationId applicationId =
		      resMgrDelegate.submitApplication(appContext);

		ApplicationReport appMaster = resMgrDelegate
		      .getApplicationReport(applicationId);