/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.handler;

import java.io.PrintStream;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * @author fernandoglatz
 */
public class AbstractRequestHandler<I, O> implements RequestHandler<I, O> {

	private Context context;

	@Override
	public O handleRequest(I input, Context context) {
		setContext(context);
		return null;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void logInfo(String message) {
		log(System.out, message);
	}

	public void logError(Exception e) {
		String stacktrace = ExceptionUtils.getStackTrace(e);
		log(System.err, stacktrace);
	}

	public void logError(String message) {
		log(System.err, message);
	}

	private void log(PrintStream printStream, String message) {
		if (getContext() != null) {
			getContext().getLogger().log(message);
		} else {
			printStream.println(message);
		}
	}
}
