package com.c2point.tools.ui;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.configuration.Versioning;
import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.ui.msg.MessagesView;
import com.c2point.tools.ui.reports.ReportsView;
import com.c2point.tools.ui.repositoryview.RepositoryManagementView;
import com.c2point.tools.ui.settings.SettingsView;
import com.c2point.tools.ui.tools.history.ToolsHistoryManagementView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.Alignment;
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
//				otherButtonPressed();
				temporalTests();
                
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
				reportButtonPressed();
                
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
		layout.setWidth( "100%" );
		layout.setMargin( new MarginInfo( false, true, false, false ));
		layout.setSpacing( true );
		
        Label nameText = new Label(
        		"<b>"
        		+ (( InventoryUI )UI.getCurrent()).getSessionOwner().getFirstAndLastNames()
        		+ " (" 
        		+ (( InventoryUI )UI.getCurrent()).getSessionOwner().getOrganisation().getName()
        		+ ")"
        		+ "</b>",
        		ContentMode.HTML
        	);
        
        
        Button accountButton = new Button();
        accountButton.setIcon( VaadinIcons.COG );
        accountButton.addStyleName( "borderless" );
        
//        accountButton.addStyleName( "icon-" + view );
        accountButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
				
            }
        });
        
        Button exitButton = new NativeButton( "Logout" );
        exitButton.addStyleName( BaseTheme.BUTTON_LINK );
        
//        exitButton.addStyleName( "icon-" + view );
        exitButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick( ClickEvent event ) {
                exitPressed();
            }
        });

		Label glue = new Label( " " );
//		glue.setWidth("100%");
        
		nameText.setSizeUndefined();
		
        layout.addComponent( glue );
        layout.addComponent( nameText );
        layout.addComponent( accountButton );
        layout.addComponent( exitButton );

        layout.setExpandRatio( glue, 1.0f );
        
        layout.setComponentAlignment( nameText, Alignment.MIDDLE_RIGHT );

		
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
    
    private void reportButtonPressed() {
    	
		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	
		mainSplit.setSecondComponent( new ReportsView());

    }
    
    private void settingsButtonPressed(){

		try {
			mainSplit.removeComponent( mainSplit.getSecondComponent());
		} catch( Exception e ) {
			
		}
    	
		mainSplit.setSecondComponent( new SettingsView());
		
    }
    
    @SuppressWarnings("unused")
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
		
		Label ttt = new Label( "Version: " + Versioning.getVersion(), ContentMode.HTML );
		
		notImplementedView.addComponent( ttt );
///		t.setWidth( "100%" );
		
		return notImplementedView;
    	
    }

    private void temporalTests() {
    	
    }
    
    private void exitPressed() {
    	// Log out and exit
    	
		logger.debug( "Logout has been selected" );
		InventoryUI app = ( InventoryUI )UI.getCurrent();

		// Logout user
		AuthenticationFacade.getInstance().logout( app.getSessionOwner(), false );
		// Close application
		app.close();    	
		// Reload Web page to get Login screen
		Page.getCurrent().reload();
    	
    }
    
}

