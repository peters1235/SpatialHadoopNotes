public class ResourceMgrDelegate extends YarnClient {
	protected YarnClient client;

	public ApplicationId submitApplication(ApplicationSubmissionContext appContext)	        
        return client.submitApplication(appContext);



public class YarnClientImpl extends YarnClient {
	protected ApplicationClientProtocol rmClient;

	ApplicationId submitApplication(ApplicationSubmissionContext appContext)
		ApplicationId applicationId = appContext.getApplicationId();
		SubmitApplicationRequest request =
		    Records.newRecord(SubmitApplicationRequest.class);
		request.setApplicationSubmissionContext(appContext);
		rmClient.submitApplication(request);
		while (true) {
			try {
				YarnApplicationState state =
				    getApplicationReport(applicationId).getYarnApplicationState();
				if (!state.equals(YarnApplicationState.NEW) &&
				    !state.equals(YarnApplicationState.NEW_SAVING)) {
				  	//成功提交
				  	LOG.info("Submitted application " + applicationId);
				  	break;

				//超时退出
				long elapsedMillis = System.currentTimeMillis() - startTime;
				if (enforceAsyncAPITimeout() &&
				      elapsedMillis >= asyncApiPollTimeoutMillis) {
					throw new YarnException("Timed out while waiting for application " +applicationId + 
						" to be submitted successfully");

				//告知用户继续等待
				// Notify the client through the log every 10 poll, in case the client
				// is blocked here too long.
				if (++pollCount % 10 == 0) {
				    LOG.info("Application submission is not finished, " +
				      "submitted application " + applicationId +
				      " is still in " + state);
				
				//继续等待
				Thread.sleep(submitPollIntervalMillis);
			catch (ApplicationNotFoundException ex) {
				//失败的话重新提交
				rmClient.submitApplication(request);

