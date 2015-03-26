package com.c2point.tools.ui.listeners;

import java.util.EventListener;

public interface  EditInitiationListener extends EventListener {

	public void initiateAdd();
	public void initiateCopy();
	public void initiateEdit();
	public void initiateDelete();
}
