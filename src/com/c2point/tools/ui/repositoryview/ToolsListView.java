package com.c2point.tools.ui.repositoryview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.InventoryUI;
import com.c2point.tools.ui.listeners.FilterListener;
import com.c2point.tools.ui.listeners.PrintNowListener;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.c2point.tools.ui.printpdf.AbstractPdfForm;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToolsListView extends VerticalLayout implements
		ToolItemChangedListener, FilterListener, PrintNowListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(ToolsListView.class
			.getName());

	private static int BUTTON_WIDTH = 25;

	protected FilterToolbar filterBar;

	private ToolsListModel model;
	private TreeTable itemsTable;

	private Category topCategory;

	public ToolsListView(ToolsListModel model) {
		super();

		this.model = model;

		initUI();

		// model.addChangedListener(( CategoryModelListener ) this );
		model.addChangedListener((ToolItemChangedListener) this);

	}

	private void initUI() {

		setSizeFull();
		// setHeight( "100%" );

		setMargin(true);
		setSpacing(true);

		initTable();
		initFilterToolbar();

		this.addComponent(filterBar);
		this.addComponent(itemsTable);

		this.setExpandRatio(itemsTable, 1.0f);

	}

	private void initTable() {

		itemsTable = new TreeTable();

		itemsTable.setSizeFull();
		// itemsTable.setHeight( "100%");

		// Configure table
		itemsTable.setSelectable(true);
		itemsTable.setNullSelectionAllowed(false);
		itemsTable.setMultiSelect(false);
		itemsTable.setColumnCollapsingAllowed(false);
		itemsTable.setColumnReorderingAllowed(false);
		// table.setColumnHeaderMode( Table.ColumnHeaderMode.HIDDEN );
		// table.setSortEnabled( false );
		itemsTable.setImmediate(true);

		itemsTable.addContainerProperty("name", String.class, null);
		itemsTable.addContainerProperty("user", String.class, null);
		itemsTable.addContainerProperty("status", Label.class, null);
		itemsTable
				.addContainerProperty("buttons", HorizontalLayout.class, null);
		// itemsTable.addContainerProperty( "data", ToolItem.class, null );

		itemsTable.setVisibleColumns(new Object[] { "name", "user", "status",
				"buttons" }); // , "action" } );

		itemsTable.setColumnHeaders(new String[] {
				((InventoryUI) UI.getCurrent())
						.getResourceStr("repositorymgmt.list.header.tool"),
				((InventoryUI) UI.getCurrent())
						.getResourceStr("repositorymgmt.list.header.user"),
				((InventoryUI) UI.getCurrent())
						.getResourceStr("repositorymgmt.list.header.status"),
				"" });

		itemsTable.setColumnWidth("buttons", BUTTON_WIDTH * 2);

		// New User has been selected. Send event to model
		itemsTable.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				if (logger.isDebugEnabled())
					logger.debug("Selection were changed");

				try {
					Object itemId = itemsTable.getValue();
					// model.setSelectedItem(( ToolItem ) item.getItemProperty(
					// "data" ).getValue());
					model.setSelectedItem((ToolItem) itemId);
				} catch (Exception e) {
					logger.debug("No selection. Tool Item cannot be fetched from itemsList ");
					model.setSelectedItem(null);
				}

			}
		});

		itemsTable.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {

				if (toProcess(event) && event.getItemId() instanceof ToolItem) {
					if (logger.isDebugEnabled())
						logger.debug("Click shall be processed! Table item clicked: "
								+ event.getItem().getItemProperty("name")
										.getValue());

					handleActions((ToolItem) event.getItemId());

				} else {
					if (logger.isDebugEnabled())
						logger.debug("Click done. No processing");
				}

			}

		});

	}

	private void handleActions(ToolItem item) {

		ChangeItemAttributesDlg editDlg = new ChangeItemAttributesDlg(model,
				item) {
			private static final long serialVersionUID = 1L;

			@Override
			public void dlgClosed() {

				logger.debug("ChangeItemAttributes Dialog has been closed!");
				endClickHandlingProcess();

			}
		};

		UI.getCurrent().addWindow(editDlg);

	}

	private Object clickedId = null;
	private int clickedCounter = 0;

	private void endClickHandlingProcess() {
		clickedCounter = 0;
	}

	private boolean toProcess(ItemClickEvent event) {

		boolean bRes = false;

		if (event.isDoubleClick()) {
			// We pass doubleclick completely!
			return bRes;
		}
		if (clickedId != event.getItemId()) {

			// New item clicked. Select it but do nothing
			clickedCounter = 1;
			clickedId = event.getItemId();

		} else {
			// Item has been selected already

			if (clickedCounter < 1) {
				clickedCounter = 1;
			}

			if (clickedCounter == 1) {
				// Second click opens dialog or starts other actions
				bRes = true;

			} else {
				// This is more than 2nd click. Do nothing
			}
			clickedCounter++;

		}

		return bRes;
	}

	private void initFilterToolbar() {

		filterBar = new FilterToolbar(this, model, this);

	}

	private void dataFromModel() {

		itemsTable.removeAllItems();

		// Add the top element of Category Tree
		topCategory = new Category("000000", "All Categories", true);
		topCategory.setOrg(model.getOrg());

		addCategoryIfNecessary(topCategory);

		// Now add all Tool Items and related categories if necessary

		Collection<ToolItem> itemList = model.getItems();

		if (itemList != null && itemList.size() > 0) {

			for (ToolItem repItem : itemList) {
				if (repItem != null) {
					addCategoryIfNecessary(repItem.getTool().getCategory());

					addToolItem(repItem);
				}
			}

		}

		itemsTable.setSortContainerPropertyId("tool");

		itemsTable.sort();

	}

	@SuppressWarnings("unchecked")
	private void updateItem(ToolItem toolItem) {

		Item item = itemsTable.getItem(toolItem);

		if (item == null) {
			// Something wrong. Here we can edit only. Item must exist
			if (logger.isDebugEnabled())
				logger.error("Did not find updated ToolItem in the list");

			return;

		} else {
			if (logger.isDebugEnabled())
				logger.debug("Tool Item exists already. Will be modified: "
						+ toolItem);

			try {
				item.getItemProperty("user").setValue(
						StringUtils.defaultString(toolItem.getCurrentUser()
								.getLastAndFirstNames()));
			} catch (Exception e) {
			}
			setStatusProperty(item, toolItem);

		}

	}

	@SuppressWarnings("unchecked")
	private void setStatusProperty(Item item, ToolItem toolItem) {

		try {
			Label statusLabel = (Label) item.getItemProperty("status")
					.getValue();
			String nameStr;
			nameStr = "<b "
					+ getColorAttribute(toolItem.getStatus())
					+ ">"
					+ toolItem.getStatus().toString(
							model.getApp().getSessionData().getBundle())
					+ "</b>";

			statusLabel.setValue(nameStr);

		} catch (Exception e) {
			logger.error("Could not create Tool property because unknown reason. Tool Item: "
					+ toolItem);
			item.getItemProperty("status").setValue(new Label("?"));
		}

	}

	private String getColorAttribute(ItemStatus status) {

		switch (status) {
		case FREE:
			return "style='color:green'";

		case RESERVED:
		case BROKEN:
		case REPAIRING:
		case STOLEN:
		case UNKNOWN:
			return "style='color:red'";
		case INUSE:
			return "style='color:#FDD835'";

		}

		return "";

	}

	class ToolsViewFilter implements Container.Filter {
		private static final long serialVersionUID = 1L;

		private Collection<String> strArray;
		private ItemStatus status;

		public ToolsViewFilter(Collection<String> strArray, ItemStatus status) {

			this.strArray = strArray;
			this.status = status;
		}

		@Override
		public boolean passesFilter(Object itemId, Item item)
				throws UnsupportedOperationException {

			// If no filter defined that any record is OK. Return TRUE
			if ((strArray == null || strArray.size() == 0) && status == null)
				return true;

			ToolItem toolItem = null;
			Category category = null;
			Tool tool = null;

			if (itemId instanceof Category) {

				category = (Category) itemId;

			} else if (itemId instanceof ToolItem) {

				toolItem = (ToolItem) itemId;
				tool = toolItem.getTool();

			}

			if (category == null && toolItem == null)
				return false;

			if (toolItem != null && this.status != null
					&& toolItem.getStatus() != this.status)
				return false;

			if ((strArray == null || strArray.size() == 0))
				return true;

			for (String searchStr : strArray) {

				try {
					searchStr = searchStr.toLowerCase().trim();
					/*
					 * This variant tries to find any one from substring in
					 * toolitems and or categories if ( toolItem != null ) { //
					 * this is line with ToolItem
					 * 
					 * if ( tool.getName() != null &&
					 * tool.getName().toLowerCase().indexOf( searchStr ) != -1 )
					 * return true;
					 * 
					 * if ( tool.getDescription() != null &&
					 * tool.getDescription().toLowerCase().indexOf( searchStr )
					 * != -1 ) return true;
					 * 
					 * try { if (
					 * tool.getManufacturer().getName().toLowerCase().indexOf(
					 * searchStr ) != -1 ) return true; } catch ( Exception e )
					 * {}
					 * 
					 * 
					 * if ( tool.getModel() != null &&
					 * tool.getModel().toLowerCase().indexOf( searchStr ) != -1
					 * ) return true;
					 * 
					 * if ( toolItem.getBarcode() != null &&
					 * toolItem.getBarcode().toLowerCase().indexOf( searchStr )
					 * != -1 ) return true;
					 * 
					 * if ( toolItem.getSerialNumber() != null &&
					 * toolItem.getSerialNumber().toLowerCase().indexOf(
					 * searchStr ) != -1 ) return true;
					 * 
					 * if ( toolItem.getCurrentUser() != null &&
					 * toolItem.getCurrentUser
					 * ().getFirstAndLastNames().toLowerCase().indexOf(
					 * searchStr ) != -1 ) return true; } else if ( category !=
					 * null ) {
					 * 
					 * if ( category.getName() != null &&
					 * category.getName().toLowerCase().indexOf( searchStr ) !=
					 * -1 ) return true;
					 * 
					 * }
					 */

					/*
					 * This variant tries to find all substrings in toolitems
					 * and/or categories
					 */

					if (toolItem != null) {
						// this is line with ToolItem

						if (tool.getName() != null
								&& tool.getName().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

						if (tool.getToolInfo() != null
								&& tool.getToolInfo().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

						try {
							if (tool.getManufacturer().getName().toLowerCase()
									.indexOf(searchStr) != -1)
								continue;
						} catch (Exception e) {
						}

						if (tool.getModel() != null
								&& tool.getModel().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

						if (toolItem.getBarcode() != null
								&& toolItem.getBarcode().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

						if (toolItem.getSerialNumber() != null
								&& toolItem.getSerialNumber().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

						if (toolItem.getCurrentUser() != null
								&& toolItem.getCurrentUser()
										.getFirstAndLastNames().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

					} else if (category != null) {

						if (category.getName() != null
								&& category.getName().toLowerCase()
										.indexOf(searchStr) != -1)
							continue;

					}

					return false;

				} catch (Exception e) {
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean appliesToProperty(Object propertyId) {

			return (propertyId != null && propertyId.equals("data"));

		}

	}

	@Override
	public boolean filterWasChanged(Collection<String> strArray,
			ItemStatus status) {

		boolean found = false;

		((Filterable) itemsTable.getContainerDataSource())
				.removeAllContainerFilters();

		if (strArray != null && strArray.size() > 0
				&& itemsTable.getContainerDataSource() != null
				|| status != null) {

			Filter filter = new ToolsViewFilter(strArray, status);

			((Filterable) itemsTable.getContainerDataSource())
					.addContainerFilter(filter);

			found = itemsTable.getContainerDataSource().size() > 0;

			if (itemsTable != null
					&& itemsTable.getContainerDataSource() instanceof Container.Ordered) {

				itemsTable.setValue(found ? itemsTable.firstItemId() : null);
			}

			if (logger.isDebugEnabled())
				logger.debug("Search: str = '" + strArray + "'. Found? "
						+ found);

		}

		return found;

	}

	@Override
	public void wasAdded(ToolItem item) {
	}

	@Override
	public void wasDeleted(ToolItem item) {
	}

	@Override
	public void wholeListChanged() {

		if (logger.isDebugEnabled())
			logger.debug("Tool Items List received WhleListChanged event!");

		dataFromModel();

		// for itemsTable.getItemIds()

		itemsTable.setCollapsed(topCategory, false);
		itemsTable.setValue(topCategory);

	}

	@Override
	public void currentWasSet(ToolItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wasChanged(ToolItem item) {

		logger.debug("Tool Items List receives notification: Tool Item was Changed!");

		// Find correct Item. Start from selected one
		// update row with data
		updateItem(item);

		// After changes one click shall be handled again
		endClickHandlingProcess();

		// set correct selection
		// itemsTable.setValue( item.getId());
		itemsTable.setValue(item);

	}

	@SuppressWarnings("unchecked")
	private boolean addCategoryIfNecessary(Category addCategory) {

		boolean ret = false;

		if (addCategory != null) {
			// Firstly add parent Category if necessary
			if (addCategory.getParent() != null) {

				addCategoryIfNecessary(addCategory.getParent());

			}

			// Search if category was added earlier
			Item categoryItem = itemsTable.getItem(addCategory);

			if (categoryItem == null) {
				// Category was not added to table yet
				// Will be added

				try {
					// Add this category
					categoryItem = itemsTable.addItem(addCategory);

					// What shall be on the screen
					categoryItem.getItemProperty("name").setValue(
							addCategory.getName());
					// categoryItem.getItemProperty( "data" ).setValue(
					// addCategory );

					// Set parent and allow childs (categories and ToolItems
					itemsTable.setParent(
							addCategory,
							addCategory.getParent() != null ? addCategory
									.getParent() : topCategory);
					itemsTable.setChildrenAllowed(addCategory, true);

					itemsTable.setCollapsed(addCategory, false);

					ret = true;
				} catch (Exception e) {
					logger.error("Failed to add Category: " + addCategory
							+ "\n" + e);
				}

			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private boolean addToolItem(ToolItem toolItem) {

		boolean ret = false;

		if (toolItem != null) {

			// If category is not presented than add to Top Category
			Category category = toolItem.getTool().getCategory();

			if (itemsTable.getItem(category) == null) {
				category = this.topCategory;
			}

			try {
				// Add this category
				Item item = itemsTable.addItem(toolItem);

				// What shall be on the screen
				try {
					item.getItemProperty("name").setValue(
							StringUtils.defaultString(toolItem.getTool()
									.getFullName()));
				} catch (Exception e) {
				}
				try {
					item.getItemProperty("user").setValue(
							StringUtils.defaultString(toolItem.getCurrentUser()
									.getLastAndFirstNames()));
				} catch (Exception e) {
				}
				item.getItemProperty("status").setValue(
						new Label("", ContentMode.HTML)); // actual value will
															// be set below

				item.getItemProperty("buttons")
						.setValue(getButtonSet(toolItem));

				setStatusProperty(item, toolItem);
				// setActionsContent( item, toolItem );

				// Set category as parent and disallow childs for ToolItem
				itemsTable.setParent(toolItem, category);
				itemsTable.setChildrenAllowed(toolItem, false);

				ret = true;

			} catch (Exception e) {
				logger.error("Failed to add ToolItem: "
						+ toolItem.getFullName() + "\n" + e);
			}

		}

		return ret;
	}

	private Component getButtonSet(ToolItem item) {

		HorizontalLayout buttonsSet = new HorizontalLayout();

		buttonsSet.setSpacing(true);

		// if ( model.allowsToEdit()) {

		final NativeButton editButton = createButton("icons/16/edit.png",
				"toolsmgmt.edit.tooltip", item);

		editButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				ToolItem item = (ToolItem) editButton.getData();

				itemsTable.select(item);
				handleActions(item);

			}
		});

		buttonsSet.addComponent(editButton);
		// }

		return buttonsSet;
	}

	private NativeButton createButton(String iconPath, String tooltipKey,
			ToolItem item) {

		NativeButton button = new NativeButton();

		button.setIcon(new ThemeResource(iconPath));
		button.setDescription(model.getApp().getResourceStr(tooltipKey));

		button.setHeight(Integer.toString(BUTTON_WIDTH) + "px");
		// button.setStyleName( "v-nativebutton-deleteButton" );
		// button.addStyleName( "v-nativebutton-link" );
		button.setStyleName(BaseTheme.BUTTON_LINK);

		button.setData(item);
		button.setImmediate(true);

		return button;

	}

	@SuppressWarnings("unchecked")
	public void printNow() {

		Window subwindow = new Window(""
		// model.getApp().getResourceStr( "menu.item.report.time" )
		);

		subwindow.setModal(true);
		subwindow.setWidth("80%");
		subwindow.setHeight("90%");
		subwindow.setResizable(true);
		subwindow.center();

		ArrayList<?> itemsList = new ArrayList<Object>( itemsTable.getItemIds());

		normalizeToolItems( itemsList );
		
		BrowserFrame browser = new BrowserFrame( "Browser" );
		final AbstractPdfForm form = model.getDocToPrint(( ArrayList<ToolItem> ) itemsList );
		StreamResource resource = null;
		
		if ( form != null ) {
			resource = form.getStream();
			
			resource.setCacheTime( 0 );
			// Set the right mime type
			resource.setMIMEType("application/pdf");
			browser.setSource( resource );
			browser.setSizeFull();
	
			subwindow.setContent( browser );
			subwindow.addCloseListener( new CloseListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {

					form.deleteTmp();
					
				}
				
			});

			
			UI.getCurrent().addWindow( subwindow );
			
		}
	}

	private void normalizeToolItems( ArrayList<?> itemsList ) {

		// Remove object if it is NOT ToolItem
		Iterator<?> iter = itemsList.iterator();
		Object obj;
		while (iter.hasNext()) {
			obj = iter.next();
			if ( !( obj instanceof ToolItem )) {
//				itemsList.remove( obj );
				iter.remove();
			}

		}
		
		// Sort according to name
		
		
	}
	
}
