package com.c2point.tools.ui.listeners;

import java.util.EventListener;

import com.c2point.tools.entity.organisation.Organisation;

public interface PropertiesListener extends EventListener {

	public void wasChanged( Organisation org );

}
