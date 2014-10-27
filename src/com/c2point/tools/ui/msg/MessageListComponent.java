package com.c2point.tools.ui.msg;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.msg.Message;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class MessageListComponent extends VerticalLayout implements MessageModelListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( MessageListComponent.class.getName());
	
	private MessagingModel	model; 
	private Table 			table;
	private Panel			infoPanel;
	
	
	public MessageListComponent( MessagingModel model ) {
		super();
		
		initUI();
		
		initModel( model );
		
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		
		table = new Table();
		initTable();

		infoPanel = new Panel();
		initInfoPanel();
		
//		this.addComponent( getSearchBar());
		this.addComponent( table );
		this.addComponent( infoPanel );
		
		this.setExpandRatio( table, 0.8f );
		this.setExpandRatio( infoPanel, 0.20f );
		
	}
	
	private void initTable() {

		// Configure table
		table.setSelectable( true );
		table.setMultiSelect( false );
//		table.setNullSelectionAllowed( false );
		table.setColumnCollapsingAllowed( false );
		table.setColumnReorderingAllowed( false );
//		table.setColumnHeaderMode( Table.ColumnHeaderMode.HIDDEN );
//		table.setSortEnabled( false );
		table.setImmediate( true );
		table.setSizeFull();
		
//		categoriesTree.addContainerProperty( "code",		String.class, 	null );
		table.addContainerProperty( "type",		Embedded.class, null );
		table.addContainerProperty( "text", 	String.class, 	null );
		table.addContainerProperty( "status", 	String.class, 	null );
		table.addContainerProperty( "date", 	LocalDate.class,null );
		table.addContainerProperty( "controls", ApproveRejectButtonsComponent.class, null );

		table.setSortAscending( true );
		table.setSortContainerPropertyId( "date" );

		table.addGeneratedColumn("date", new Table.ColumnGenerator() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell( Table source, Object itemId,
					Object columnId ) {
				
				// Get the object stored in the cell as a property
				@SuppressWarnings("rawtypes")
				Property prop = source.getItem( itemId ).getItemProperty( columnId );
				if ( prop.getType().equals( LocalDate.class )) {
					
					LocalDate date = ( LocalDate ) prop.getValue(); 
					if ( date != null ) {

						return new Label( date.toString( DateTimeFormat.forPattern("dd.MM.yyyy")));
					}
				}
				
				return null;
			}
			
		});		
		
		
		// New User has been selected. Send event to model
		table.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "CategoriesList selection were changed" );

//				model.categorySelected(( Category ) categoriesTree.getValue());
				
			}
		});
		
	}
	
	private void initInfoPanel() {
		
		infoPanel.addStyleName( "light" );

		MessageInfoComponent infoComp = new MessageInfoComponent();
		table.addValueChangeListener( infoComp );

		infoPanel.setContent( infoComp );

	}
	
	private void updateUI() {
		
	}

	private void initModel( MessagingModel model ) {
		
		this.model = model; 
		model.addChangedListener( this );		
		model.init();

	}

	
	private void dataFromModel() {

		Collection<Message> msgList = 
							model.getMessages();
		
		
		table.removeAllItems();
		
		if ( msgList != null && msgList.size() > 0 ) {
			for ( Message msg : msgList ) {
				if ( msg  != null ) {
					addMessage( msg );
				}
			}
		}
		
		table.setSortContainerPropertyId( "tool" );

		table.sort();
		
	}
	
	private void addMessage( Message msg ) {

		Item item = table.addItem( msg );

		if ( logger.isDebugEnabled()) logger.debug( "Item will be added. Repository Item id: " + msg.getId());

		updateMessage( item, msg );
			
	}

	int num = 1;

	private void updateMessage( Item item, Message msg ) {
		
		// Msg Type icon column 
		item.getItemProperty( "type" ).setValue( getTypeIcon( msg ));
		
		// Item Text or description column 
		item.getItemProperty( "text" ).setValue( getText( msg ));
		
		// Status column 
		item.getItemProperty( "status" ).setValue( getStatus( msg ));
		
		// Date column 
		item.getItemProperty( "date" ).setValue( msg.getDate());
		
		// Update control buttons
		item.getItemProperty( "controls" ).setValue( updateControls( item, msg ));
		
		
	}
	
	
	
	@Override
	public void wasAdded(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasChanged(Message msg) {
		
		if ( logger.isDebugEnabled())
			logger.debug( "MessageList  received WasChanged event!" );
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasDeleted(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listWasChanged() {

		Object selectedItemId = table.getValue();

		dataFromModel();

		table.setValue( selectedItemId );
		
	}

	@Override
	public void selected(Message msg) {
		// TODO Auto-generated method stub
		
	}

	private Embedded getTypeIcon( Message msg ) {

		Embedded icon = null;
		String iconName = null;
		String tooltipStr = "";
		
		switch ( msg.getType()) {
			case AGREEMENT:
				iconName = "icons/16/approved1.png";
				tooltipStr = "";
				break;
			case CONFIRMATION:
				iconName = "icons/16/info.png";
				tooltipStr = "";
				break;
			case INFO:
				iconName = "icons/16/info.png";
				tooltipStr = "";
				break;
			case REJECTION:
				iconName = "icons/16/attention.png";
				tooltipStr = "";
				break;
			case REQUEST:
				iconName = "icons/16/attention.png";
				tooltipStr = "";
				break;
			case TEXT:
				iconName = "icons/16/info.png";
				tooltipStr = "";
				break;
			default:
				break;
		}
/*
		case AGREEMENT:
				break;
			case InfoMessageBorrowed:
				break;
			case InfoMessageWantToBorrow:
				iconName = "icons/16/info.png";
				tooltipStr = "";
				break;
			case TEXT:
				break;
			case REQUEST:
				iconName = "icons/16/attention.png";
				tooltipStr = "";
				break;
			case Reject:
				iconName = "icons/16/attention.png";
				tooltipStr = "";
				break;
			default:
				break;
*/		
		
		if ( iconName != null ) {	
		
			icon = new Embedded( "", new ThemeResource( iconName ));
			icon.setDescription( tooltipStr );
		}
		
		return icon;
		
	}

	private String getText( Message msg ) {

		String text = "Not defined yet";

		switch ( msg.getType()) {
			case AGREEMENT:
				text = "Approved by " +msg.getFrom().getFirstAndLastNames();
				break;
			case CONFIRMATION:
				text = "Confirmed by " +msg.getFrom().getFirstAndLastNames();
				break;
			case INFO:
				text = msg.getText();
				break;
			case REJECTION:
				text = "Rejected by " +msg.getFrom().getFirstAndLastNames();
				break;
			case REQUEST:
				text = "Tool requested by " +msg.getFrom().getFirstAndLastNames();
				break;
			case TEXT:
				text = msg.getText();
				break;
			default:
				break;
		}
		return text;
		
	}

	private String getStatus( Message msg ) {

		String text;
		
		switch ( msg.getStatus()) {
		case CREATED:
			text = "Unknown";
			break;
		case READ:
			text = "Read";
			break;
		case SENT:
			text = "Sent";
			break;
		case UNREAD:
			text = "Unread";
			break;
		case RESPONDED:
			text = "Answered";
			break;
		default:
			text = "Unknown";
			break;
		
		}
		
		return text;
		
	}

	private ApproveRejectButtonsComponent updateControls( final Item item, final Message msg ) {

		ApproveRejectButtonsComponent controls = new ApproveRejectButtonsComponent( model.getApp(), msg );

		switch ( msg.getType()) {
			case REQUEST:
				controls.addApproveListener( new Button.ClickListener() {
					private static final long serialVersionUID = 1L;
	
					@Override
					public void buttonClick(ClickEvent event) {
						logger.debug( "Approve button pressed" );
						model.approveToBorrow( msg );
						
					}
					
				});
				controls.addRejectListener( new Button.ClickListener() {
					private static final long serialVersionUID = 1L;
	
					@Override
					public void buttonClick(ClickEvent event) {
	
						logger.debug( "Reject button pressed" );
						model.rejectToBorrow( msg );
						
					}
					
				});
				break;
			case AGREEMENT:
				break;
			case REJECTION:
				break;
			case CONFIRMATION:
				break;
			case INFO:
				break;
			case TEXT:
				break;
			default:
				break;
		
		}
		
		return controls;
		
	}
}

