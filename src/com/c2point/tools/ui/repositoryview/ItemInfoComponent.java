package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class ItemInfoComponent extends GridLayout implements ToolsModelListener {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ItemInfoComponent.class.getName());

//	private RepositoryItem	repItem;
	
	private Label	nameLabel;
	private Label	categoriesLabel;
	private Label	descriptionLabel;

	private Label	userNameLabel;
	private Label	userValueLabel;
	
	private Label	responsibleNameLabel;
	private Label	responsibleValueLabel;

	private Label	locationNameLabel;
	private Label	locationValueLabel;

	public ItemInfoComponent() {
		
		super( 4, 6 );
		
//		this.repItem = repItem;
		
		init();
	}
	
	private void init() {
	
		nameLabel = new Label();
		nameLabel.addStyleName( "h1" );
		
		categoriesLabel = new Label();
		descriptionLabel = new Label( "", ContentMode.HTML );

		userNameLabel = new Label();
		userNameLabel.addStyleName( "h3" );
		
		responsibleNameLabel = new Label();
		responsibleNameLabel.addStyleName( "h3" );

		locationNameLabel = new Label();
		locationNameLabel.addStyleName( "h3" );

		userValueLabel = new Label();
		responsibleValueLabel = new Label();
		locationValueLabel = new Label();
		
		this.addComponent( categoriesLabel, 	0, 0, 1, 0 );
		this.addComponent( nameLabel, 			0, 1, 1, 1 );
		this.addComponent( descriptionLabel, 	0, 2, 1, 2 );

		this.addComponent( userNameLabel, 		2, 2 );
		this.addComponent( responsibleNameLabel,2, 3 );
		this.addComponent( locationNameLabel, 	2, 4 );
		
		this.addComponent( userValueLabel, 			3, 2 );
		this.addComponent( responsibleValueLabel,	3, 3 );
		this.addComponent( locationValueLabel,		3, 4 );
		
		
	}
	
	public void showItem( ToolItem repItem ) {
		
		// Name label value
		try {
			nameLabel.setValue( repItem.getTool().getName());
		} catch ( Exception e ) {
			logger.debug( "  Tool name are missing for Repository Item" );
			nameLabel.setValue( "???" );
		}
			
		// Categories label value
		try {

			categoriesLabel.setValue( createCategoriesChain( repItem.getTool().getCategory()));
			
		} catch ( Exception e ) {
			logger.debug( "  categories chain cannot be fetched for Repository Item" );
			nameLabel.setValue( "???" );
		}
			
		// Description label
		try {

			descriptionLabel.setValue( repItem.getTool().getDescription());
			
		} catch ( Exception e ) {
			logger.debug( "  Description is missing for Repository Item" );
			nameLabel.setValue( "???" );
		}
		
		
		userNameLabel.setValue( "User:" );
		responsibleNameLabel.setValue( "Responsible:" );
		locationNameLabel.setValue( "Last seen at:" );

		// User label
		try {

			userValueLabel.setValue( repItem.getCurrentUser().getFirstAndLastNames());
			
		} catch ( Exception e ) {
			logger.debug( "  Current User is missing for Repository Item" );
			userValueLabel.setValue( "" );
		}
		
		// Responsible label
		try {

			responsibleValueLabel.setValue( repItem.getResponsible().getFirstAndLastNames());
			
		} catch ( Exception e ) {
			logger.debug( "  Responsible User is missing for Repository Item" );
			responsibleValueLabel.setValue( "" );
		}
		
	}

	public void clearItem() {
		
		nameLabel.setValue( "" );
		categoriesLabel.setValue( "" );
		descriptionLabel.setValue( "" );

		userNameLabel.setValue( "" );
		userValueLabel.setValue( "" );
		
		responsibleNameLabel.setValue( "" );
		responsibleValueLabel.setValue( "" );

		locationNameLabel.setValue( "" );
		locationValueLabel.setValue( "" );
	}
	
	
	private String createCategoriesChain( Category category ) {
		
		String chain;
		Category tmpCategory = category;
		
		if ( tmpCategory != null ) {
			
			chain = tmpCategory.getName();
			while ( tmpCategory.getParent() != null ) {
				
				tmpCategory = tmpCategory.getParent();
				
				if ( tmpCategory.getName() != null ) {
					
					chain = tmpCategory.getName() + " > " + chain; 
							
				} else {
					chain = " ??? " + ">" + chain; 
				}
				
			}
			
		} else {
			
			chain = "???";
			logger.error( "Could not create the String with chain of categories");
		}
		
		return chain;
	}

	@Override
	public void wasChanged(ToolItem repItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listWasChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selected( ToolItem repItem ) {

		logger.debug( "infoComponent received Tools Selection Changed event from ToolsModel" );
		
		if ( repItem != null ) {
			logger.debug( "  There are selection in the Repository Items List" );

			showItem( repItem );
			
		} else {
			logger.debug( "  There is NO selection in the Repository Items List" );

			clearItem();
				
		}
		
/*
		try {
			if ( event.getProperty().getValue() instanceof RepositoryItem ) {
			
				showItem(( RepositoryItem )event.getProperty().getValue());
			}
		}
		catch( Exception e ) {
			
			clearItem();
			
		}
*/		
		
	}
	

}
