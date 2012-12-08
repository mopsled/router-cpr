package com.ageatches.routerCPR;

import com.ageatches.routerCPR.BruteForceTask.Credential;
import com.ageatches.routerCPR.BruteForceTask.Error;

public interface BruteForceTaskListener {
	public void processBruteForceTaskSucceeded(Credential credentials);
	public void processBruteForceTaskUpdate(String progress, String user, String password);
	public void processBruteForceTaskFailed(Error error);
}
