package com.c2point.tools.ui.upload;

import java.util.EventListener;
import java.util.List;

public interface ProcessListener extends EventListener {

	public void processingStarted( String filename );
	public void lineProcessed( String [] precessedObject, ProcessedStatus status, int lineNumber );
	public void processingFailed( int processed, List<Integer> errRecNumbers );
	public void processingSucceeded( int processed );
	
}
