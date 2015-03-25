package com.c2point.tools.ui.repositoryview;

import java.util.Collection;
import java.util.EventListener;

import com.c2point.tools.entity.repository.ItemStatus;

public interface FilterListener extends EventListener {

	public boolean filterWasChanged( Collection<String> strArray, ItemStatus status );
	
}
