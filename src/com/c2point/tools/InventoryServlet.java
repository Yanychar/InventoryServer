package com.c2point.tools;

import javax.servlet.annotation.WebServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration( productionMode = true, ui = InventoryUI.class, widgetset = "com.c2point.tools.widgetset.InventoryserverWidgetset" )
public class InventoryServlet extends VaadinServlet {

	private static final long serialVersionUID = -1217432343436715198L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( InventoryServlet.class.getName());

	
}
