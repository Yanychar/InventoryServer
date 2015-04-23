package com.c2point.tools.ui;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.Configuration;
import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.ui.msg.MessagesView;
import com.c2point.tools.ui.orgmgmt.OrgManagementView;
import com.c2point.tools.ui.personnelmgmt.StuffManagementView;
import com.c2point.tools.ui.repositoryview.RepositoryManagementView;
import com.c2point.tools.ui.settings.SettingsView;
import com.c2point.tools.ui.tools.history.ToolsHistoryManagementView;
import com.c2point.tools.ui.toolsmgmt.ToolsManagementView;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout { //implements Organisation.PropertyChangedListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( MainView.class.getName());

	private HorizontalSplitPanel	mainSplit;

	private Button homeButton;
	private Button inventoryButton;
	private Button msgButton;
	private Button transButton;
	private Button reportButton;
	private Button setButton;
	
	public MainView() {

	}
	
	public void initWindow() {
		setSizeFull();
		setSpacing( true );
/*		
		HorizontalSplitPanel mainSplit  = new HorizontalSplitPanel();
		mainSplit.setSplitPosition( 50, Unit.EX );
		mainSplit.setSizeFull();
		mainSplit.setLocked( false );

		VerticalSplitPanel rightSplit  = new VerticalSplitPanel();
		rightSplit.setSplitPosition( 5, Unit.EM );
		rightSplit.setSizeFull();
		rightSplit.setLocked( false );
		
		rightSplit.setFirstComponent( getInfoBar());
		mainSplit.setSecondComponent( rightSplit );
		
		
		mainSplit.setFirstComponent( getMenuBar());
		mainSplit.setSecondComponent( rightSplit );

		
		
		addComponent( mainSplit );
		setExpandRatio( mainSplit, 1.0F );
*/
		
		mainSplit  = new HorizontalSplitPanel();
		mainSplit.setSizeFull();
		mainSplit.setLocked( false );
		mainSplit.setSplitPosition( 10, Unit.PERCENTAGE );
		
		mainSplit.setFirstComponent( getMenuBar());
		mainSplit.setSecondComponent( getNotImplementedView());
		
		
		addComponent( getInfoBar() );
		addComponent( mainSplit );
		setExpandRatio( mainSplit, 1.0F );
		
		inventoryButton.click();
	}

	private Component getMenuBar() {
		
		final VerticalLayout layout = new VerticalLayout();
		
//		layout.setSpacing( true );

		homeButton 		= createMenuButton( "Home", 		"icons/64/home.png", layout );
		inventoryButton	= createMenuButton( "Inventory", 	"icons/64/inventory.png", layout );
		msgButton		= createMenuButton( "Messages", 	"icons/64/mailbox.png", layout );
		transButton 	= createMenuButton( "Transactions", "icons/64/transactions.png", layout );
		reportButton	= createMenuButton( "Reporting", 	"icons/64/reporting.png", layout );
		setButton 		= createMenuButton( "Settings", 	"icons/64/settings.png", layout );
		/* Inventory",
      "transactions", "reporting", "settings" }		
*/		
		SecurityContext context = (( InventoryUI )UI.getCurrent()).getSessionData().getContext();
		
		
		layout.addComponent( homeButton );
		layout.addComponent( inventoryButton );
		layout.addComponent( msgButton );
		if ( context.hasViewPermissionMgmt( FunctionalityType.TRN_MGMT )) {
			layout.addComponent( transButton );
		}
		layout.addComponent( reportButton );

		if ( context.hasViewPermissionMgmt( FunctionalityType.ORGS_MGMT )
			||
			 context.hasViewPermissionMgmt( FunctionalityType.USERS_MGMT )
			||
			 context.hasViewPermissionMgmt( FunctionalityType.TOOLS_MGMT )
			|| 
			 context.hasViewPermissionMgmt( FunctionalityType.TRN_MGMT )) {
			
			layout.addComponent( setButton );
			
		}
		

		homeButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Home button pressed!" );
				otherButtonPressed();
                
            }
        });

		inventoryButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Inventory button pressed!" );
				inventoryButtonPressed();
                
            }
        });
		
		msgButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Message button pressed!" );
				msgButtonPressed();
                
            }
        });
		
		transButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Transaction button pressed!" );
				transButtonPressed();
                
            }
        });

		reportButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Reports button pressed!" );
				otherButtonPressed();
                
            }
        });

		setButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
				logger.debug( "Settings button pressed!" );
				settingsButtonPressed();
                
            }
        });

		
		
		
		Label glue = new Label( "" );
//		glue.setHeight("100%");
		
		layout.addComponent( glue );
		layout.setExpandRatio( glue, 1.0f );
		
        layout.addStyleName("menu");
        layout.setHeight("100%");
        layout.setWidth("100%");
//        layout.setComponentAlignment( component, Alignment.MIDDLE_LEFT); 
		
		return layout;
	}
	
	private Component getInfoBar() {

		HorizontalLayout layout = new HorizontalLayout();
		
        Button accountButton = new NativeButton( "Account" );
        
//        accountButton.addStyleName( "icon-" + view );
        accountButton.addClickListener( new ClickListener() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
                
            }
        });
        
        Button exitButton = new NativeButton( "Exit" );
        
//        exitButton.addStyleName( "icon-" + view );
        exitButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
                
            }
        });

        layout.addComponent( accountButton );
        layout.addComponent( exitButton );

		
		return layout;
	}

    private void clearMenuSelection( AbstractComponentContainer container ) {

    	for ( Iterator<Component> it = container.iterator(); it.hasNext(); ) {
            
    		Component next = it.next();
            if ( next instanceof NativeButton ) {
                next.removeStyleName("selected");
            } 
            /*            	
            else
            	if (next instanceof DragAndDropWrapper) {
                // Wow, this is ugly (even uglier than the rest of the code)
                ((DragAndDropWrapper) next).iterator().next()
                        .removeStyleName("selected");
            }
                        */
        }
    }
	

    
    private Button createMenuButton( final String name, String iconName, final AbstractComponentContainer menu ) {

    	Button menuButton = new Button( name );

    	menuButton.addStyleName( "icon-on-top" );
		menuButton.setStyleName( "icon-on-top" );
		menuButton.setIcon( new ThemeResource( iconName ));
		
		menuButton.setWidth( "100%" );		
		menuButton.setHeight( "74px" );
		
		menuButton.setDescription( name );

		menuButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
                
            	clearMenuSelection( menu );
                event.getButton().addStyleName( "selected" );
/*
                    if (!nav.getState().equals("/" + view))
                        nav.navigateTo("/" + view);
*/                        
                
                
                
            }
        });

		return menuButton;
    }

    
    private void inventoryButtonPressed(){

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	

		mainSplit.setSecondComponent( new RepositoryManagementView());
		
		
    }
    
    private void msgButtonPressed(){

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	
		mainSplit.setSecondComponent( new MessagesView());
		
    }

    private void transButtonPressed() {

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	
		mainSplit.setSecondComponent( new ToolsHistoryManagementView());
    	
    }
    private void settingsButtonPressed(){

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	
		mainSplit.setSecondComponent( new SettingsView());
		
    }
    
    private void otherButtonPressed(){

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
   
		mainSplit.setSecondComponent( getNotImplementedView());
    	
    }

    private Component getNotImplementedView() {

		VerticalLayout notImplementedView = new VerticalLayout();
		
		notImplementedView.setSizeFull();
		
		Label ttt = new Label( "Version: " + Configuration.getVersion(), ContentMode.HTML );
		
		notImplementedView.addComponent( ttt );
///		t.setWidth( "100%" );
		
		return notImplementedView;
    	
    }
}

