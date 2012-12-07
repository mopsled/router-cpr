package com.ageatches.routerCPR;

import com.ageatches.routerCPR.BruteForceTask.Credential;
import com.ageatches.routerCPR.BruteForceTask.Error;

public interface BruteForceTaskListener {
	public void processBruteForceTaskSucceeded(Credential credentials);
	public void processBruteForceTaskUpdate(String update);
	public void processBruteForceTaskFailed(Error error);
}
