package com.c2point.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InventoryContextListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger( InventoryContextListener.class.getName());
	
	private static EntityManagerFactory entityManagerFactory = null;

	public static EntityManagerFactory getEntityManagerFactory() {
		if ( entityManagerFactory == null ) {
			entityManagerFactory = Persistence.createEntityManagerFactory( "Inventory", null );
		}
		if ( logger.isDebugEnabled()) logger.debug( "getEntityManagerFactory()" );
		return entityManagerFactory;
	}

	public static void setEntityManagerFactory( EntityManagerFactory emf ) {
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
		entityManagerFactory = emf;
		if ( logger.isDebugEnabled()) logger.debug( "setEntityManagerFactory(...)" );
	}

	@Override
	public void contextDestroyed( ServletContextEvent arg0 ) {
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
		logger.info( "Server closed" );
	}

	@Override
	public void contextInitialized( ServletContextEvent arg0 ) {
		logger.info( "Tools Inventorry Server has been started!" );
	}

	
}
