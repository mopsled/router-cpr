package com.ageatches.routerCPR;

import com.ageatches.routerCPR.BruteForceTask.Credential;
import com.ageatches.routerCPR.BruteForceTask.Error;

public interface BruteForceTaskListener {
	void processBruteForceTaskSucceeded(Credential credentials);
	void processBruteForceTaskFailed(Error error);
}
