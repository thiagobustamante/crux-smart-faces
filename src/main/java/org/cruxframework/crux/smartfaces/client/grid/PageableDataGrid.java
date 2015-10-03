/*
 * Copyright 2015 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.smartfaces.client.grid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProvider.DataReader;
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord;
import org.cruxframework.crux.core.client.dataprovider.DataSelectionEvent;
import org.cruxframework.crux.core.client.dataprovider.DataSelectionHandler;
import org.cruxframework.crux.core.client.dataprovider.LazyProvider;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedHandler;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.divtable.DivRow;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.grid.Type.RowSelectStrategy;
import org.cruxframework.crux.smartfaces.client.panel.SelectableFlowPanel;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a div grid based widget.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class PageableDataGrid<T> extends AbstractPageable<T, DivTable>
{
	/**
	 * Define the column data type.
	 * @author samuel.cardoso
	 *
	 * @param <V>
	 */
	public class Column<V extends IsWidget>
	{
		private ColumnComparator<T> columnComparator;
		private ColumnGroup columnGroup;
		private GridDataFactory<V, T> dataFactory;
		private CellEditor<T, ?> editableCell;
		private IsWidget headerWidget;
		private int index = 0;
		private String key;
		private ArrayList<String> keys = new ArrayList<String>();
		private Row<T> row;
		private boolean sortable = false;

		public Column(GridDataFactory<V, T> dataFactory, String key)
		{
			assert(!keys.contains(key)): "key must be unique.";
			this.key = key;
			assert(dataFactory != null): "dataFactory must not be null";
			this.dataFactory = dataFactory;
			this.index = columns != null ? columns.size() : 0;
		}

		public Column<V> add()
		{
			PageableDataGrid.this.addColumn(this);
			return this;
		}
		
		/**
		 * Clear the column content.
		 */
		public void clear()
		{
			row = null;
		}

		/**
		 * @return the data factory.
		 */
		public GridDataFactory<V, T> getDataFactory()
		{
			return dataFactory;
		}

		/**
		 * @return the widget 
		 */
		public IsWidget getHeaderWidget() 
		{
			return headerWidget;
		}

		public Row<T> getRow() 
		{
			return row;
		}

		private void render() 
		{
			if(row.isEditing() && editableCell != null)
			{
				renderToEdit();
			}
			else
			{
				renderToView();						
			}
		}

		private void renderToEdit() 
		{
			editableCell.render(PageableDataGrid.this, row.index, index, row.dataProviderRowIndex, row.dataObject);
		}

		private void renderToView() 
		{
			V widget = dataFactory.createData(row.dataObject, row);

			if(widget == null)
			{
				return;
			}

			drawCell(PageableDataGrid.this, row.index, index, row.dataProviderRowIndex, widget);
		}

		public Column<V> setCellEditor(CellEditor<T, ?> editableCell)
		{
			this.editableCell = editableCell;
			return this;
		}
		
		public Column<V> setComparator(Comparator<T> comparator)
		{
			this.columnComparator = new ColumnComparator<T>();
			columnComparator.comparator = comparator;
			columnComparator.multiplier = 1;
			return this;
		}

		public Column<V> setDataFactory(GridDataFactory<V, T> dataFactory)
		{
			this.dataFactory = dataFactory;
			return this;
		}

		public Column<V> setHeaderWidget(IsWidget headerWidget)
		{
			this.headerWidget = headerWidget;
			return this;
		}

		public Column<V> setSortable(boolean sortable)
		{
			this.sortable = sortable;
			return this;
		}

		public void sort()
		{
			assert(getDataProvider() != null) :"No dataProvider set for this component.";
			refreshRowCache();
			getDataProvider().sort(new Comparator<T>()
			{
				@Override
				public int compare(T o1, T o2)
				{
					int compareResult = 0;
					int index = 0;
					//uses all the comparators that were added when uses clicks in the column header.
					while((compareResult == 0) && (index != linkedComparators.size()))
					{
						ColumnComparator<T> columnComparator = linkedComparators.get(index);
						compareResult = columnComparator.comparator.compare(o1, o2)*columnComparator.multiplier;
						index++;
					}
					return compareResult;
				}
			});
		}
	}

	/**
	 * Encapsulate the comparator adding a variable to indicate if the ordering should be
	 * ascending or descending.
	 * @author samuel.cardoso
	 *
	 * @param <T>
	 */
	private static class ColumnComparator<T>
	{
		private Comparator<T> comparator;
		private short multiplier = -1; 
	}

	public class ColumnGroup
	{
		@SuppressWarnings("unused")
		private ColumnGroup columnGroupParent;
		Widget header;
		int index = Integer.MAX_VALUE;
		
		protected ColumnGroup(Widget header)
		{
			this.header = header;
		}
		
		public void addColumn(Column<?> column)
		{
			//find out the smaller index
			if(column.index < index)
			{
				index = column.index;
			}
			column.columnGroup = ColumnGroup.this;
		}

		//TODO: implement it!
		public ColumnGroup newColumGroup(Widget header)
		{
			ColumnGroup columnGroup = new ColumnGroup(header);
			columnGroup.columnGroupParent = this;
			return columnGroup;
		}
	}

	private static Array<String> columnClasses = CollectionFactory.createArray();

	private static String getStyleProperties(String type, int index, int classIndex)
	{
		String typeClassName = type+"_" + classIndex;
		if(columnClasses.indexOf(typeClassName) < 0)
		{
			StyleInjector.inject("."+typeClassName+"{"+("order: " + String.valueOf(index))+"}");
			columnClasses.add(typeClassName);
		}
		return type + " " + typeClassName;
	}
	
	Array<Column<?>> columns = CollectionFactory.createArray();

	private FlowPanel contentPanel = new FlowPanel();

	private DivTable headerSection = new DivTable();
	
	LinkedList<ColumnComparator<T>> linkedComparators = new LinkedList<ColumnComparator<T>>();

	Array<Row<T>> rows = CollectionFactory.createArray();
	
	private RowSelectStrategy rowSelectStrategy = RowSelectStrategy.single;

	/**
	 * @param dataProvider the dataprovider.
	 * @param autoLoadData if true, the data must be loaded after the constructor has been invoked.
	 */
	public PageableDataGrid(final PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		initWidget(contentPanel);

		getContentPanel().add(headerSection);

		setDataProvider(dataProvider, autoLoadData);

		configSelectionStrategy(dataProvider);
		
		handleRowRefreshCache(dataProvider);
	}
	
	/**
	 * @param column the column to be added.
	 */
	public void addColumn(Column<?> column)
	{
		columns.add(column);
	}

	@Override
	protected void clear()
	{
		for (int index = 0; index < getColumns().size(); index++)
		{
			PageableDataGrid<T>.Column<?> dataGridColumn = getColumns().get(index);
			dataGridColumn.clear();
		}
		getPagePanel().clear();
	}

	@Override
	protected void clearRange(int pageStart)
	{
		while (getPagePanel().getRowCount() > pageStart)
		{
			getPagePanel().removeRow(pageStart);
		}
	}

	@Override
	public void commit() 
	{
		for(int i=0; i<rows.size();i++)
		{
			rows.get(i).makeChanges();
		}

		super.commit();
		refreshRowCache();
	}

	private void configSelectionStrategy(final PagedDataProvider<T> dataProvider)
	{
		if(!rowSelectStrategy.equals(RowSelectStrategy.unselectable))
		{
			dataProvider.addDataSelectionHandler(new DataSelectionHandler<T>()
			{
				@Override
				public void onDataSelection(DataSelectionEvent<T> event)
				{
					Array<DataProviderRecord<T>> changedRecords = event.getChangedRecords();
					
					if(changedRecords != null)
					{
						//if single strategy clear all the previous selections
						if(rowSelectStrategy.isSingle())
						{
							DataProviderRecord<T>[] selectedRecords = getDataProvider().getSelectedRecords();
							
							if(selectedRecords != null)
							{
								for(int i=0;i<selectedRecords.length;i++)
								{
									if(changedRecords.indexOf(selectedRecords[i]) < 0)
									{
										selectedRecords[i].setSelected(false, false);
										//if there's a row in this page, refresh it.
										//TODO change strategy to remove only the last record
										Row<T> row = getCurrentPageRow(selectedRecords[i].getRecordObject());
										if(row != null)
										{
											row.selected = false;
											row.refresh();
										}
									}
								}
							}
						}
						
						for(int i=0;i<changedRecords.size();i++)
						{
							DataProviderRecord<T> dataProviderRecord = changedRecords.get(i);
							
							Row<T> row = getCurrentPageRow(dataProviderRecord.getRecordObject());
							row.selected = dataProviderRecord.isSelected();
							row.refresh();
						}
					}
				}
			});
		}
	}

	private void createHeader(final PageableDataGrid<T>.Column<?> column)
	{
		SelectableFlowPanel headerWrapper = new SelectableFlowPanel();

		//Adding the header widget
		headerWrapper.add(column.headerWidget);

		//Handle sort events
		handleSortEvents(column, headerWrapper);

		//Insert the element and set up the row
		handleHeaderInsertion(column, headerWrapper);
	}

	void drawCell(PageableDataGrid<T> grid, final int rowIndex, int columnIndex, final int dataProviderRowIndex, IsWidget widget)
	{
		final DivRow divRow = grid.getPagePanel().setWidget(rowIndex, columnIndex, widget);
		Row<T> row = rows.get(rowIndex);
		
		//for each row...
		if(columnIndex == 0)
		{
			//Adding a pointer to row.
			if(row.divRow == null)
			{
				row.divRow = divRow;
			}
			
			if(!row.editing)
			{
				handleSelectionStrategy(dataProviderRowIndex, divRow, row);
			}
		}
	}

	void drawColumns(Row<T> row)
	{
		//iterate over the columns to render the body (and the header)
		for(int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			PageableDataGrid<T>.Column<?> column = columns.get(columnIndex);
			column.row = row;
			
			//header
			if(row.index == 0 && column.getHeaderWidget() != null)
			{
				createHeader(column);
			}
			
			//body
			column.render();
		}
	}

	/**
	 * @return the column given its key and 'null' case none were found.
	 */
	public Column<?> getColumn(String key)
	{
		if(getColumns() == null)
		{
			return null;
		}
		
		for (int index = 0; index < getColumns().size(); index++)
		{
			PageableDataGrid<T>.Column<?> column = getColumns().get(index);
			if(column.key.equals(key))
			{
				return column; 
			}
		}
		return null;
	}

	/**
	 * @return all the table columns.
	 */
	public Array<Column<?>> getColumns()
	{
		return columns;
	}
	
	@Override
	protected FlowPanel getContentPanel()
	{
		return contentPanel;
	}

	//This should not be exposed as it only returns rows for the current page
	//and is used for internal purposes.
	protected Row<T> getCurrentPageRow(T boundObject)
	{
		int rowIndex = getDataProvider().indexOf(boundObject);
		if(rowIndex < 0)
		{
			return null;
		}
		return rows.get(getCurrentRowIndex(rowIndex));
	}

	/**
	 * @return all the table rows. 
	 */
	public Array<Row<T>> getCurrentPageRows()
	{
		return rows;
	}

	private int getCurrentRowIndex(int dataProviderRowIndex)
	{
		int index;
		if(PageableDataGrid.this.pager != null && PageableDataGrid.this.pager.supportsInfiniteScroll())
		{
			index = dataProviderRowIndex;					
		}
		else
		{
			index = dataProviderRowIndex % getDataProvider().getPageSize();
		}
		return index;
	}

	@Override
	protected DataReader<T> getDataReader() 
	{
		return new DataReader<T>() 
		{
			@Override
			public void read(T dataObject, int dataProviderRowIndex) 
			{
				int currentRowIndex = 0;
				currentRowIndex = getCurrentRowIndex(dataProviderRowIndex);

				Row<T> row = null;
				if(rows.size() <= currentRowIndex)
				{
					row = new Row<T>(PageableDataGrid.this, dataObject, currentRowIndex, dataProviderRowIndex);
					row.selected = getDataProvider().getRecord().isSelected();
					rows.insert(currentRowIndex, row);
				}
				else
				{
					row = rows.get(currentRowIndex);
				}

				drawColumns(row);
			}
		};
	}
	
	public RowSelectStrategy getRowSelectStrategy()
	{
		return rowSelectStrategy;
	}
	
	private void handleHeaderInsertion(final PageableDataGrid<T>.Column<?> column, SelectableFlowPanel headerWrapper)
	{
		if(column.index == 0 && column.row.index == 0)
		{
			headerSection.clear();
		}
		
		if(column.columnGroup != null)
		{
			DivTable columnGroupTable = null;
			try
			{
				columnGroupTable = (DivTable)headerSection.getWidget(0, column.columnGroup.index);
			} catch (IndexOutOfBoundsException e){};
			
			if(columnGroupTable == null)
			{
				columnGroupTable = new DivTable();
				columnGroupTable.setWidget(0, 0, column.columnGroup.header);			
				column.columnGroup.header.getParent().setStyleName("");
				if(!columnGroupTable.getStyleName().contains("header"))
				{
					columnGroupTable.removeStyleName("even");
					columnGroupTable.addStyleName("header");
				}
				
				headerSection.setWidget(0, column.columnGroup.index, columnGroupTable);
				columnGroupTable.getParent().setStyleName(getStyleProperties("columnGroup", column.columnGroup.index, column.index-column.columnGroup.index));
			}
			
			columnGroupTable.setWidget(1, column.index-column.columnGroup.index, headerWrapper, getStyleProperties("column", column.index, column.index));
		}
		else
		{
			DivRow divRow = headerSection.setWidget(0, column.index, headerWrapper);
			if(!divRow.getStyleName().contains("header"))
			{
				divRow.removeStyleName("even");
				divRow.addStyleName("header");
			}
		}
	}

	private void handleRowRefreshCache(final PagedDataProvider<T> dataProvider)
	{
		dataProvider.addDataLoadedHandler(new DataLoadedHandler() 
		{
			@Override
			public void onLoaded(DataLoadedEvent event) 
			{
				dataProvider.addPageRequestedHandler(new PageRequestedHandler() 
				{
					@Override
					public void onPageRequested(PageRequestedEvent event) 
					{
						if(PageableDataGrid.this.pager == null || !PageableDataGrid.this.pager.supportsInfiniteScroll())
						{
							refreshRowCache();
						}
					}
				});
			}
		});
	}

	private void handleSelectionStrategy(final int dataProviderRowIndex, final DivRow divRow, Row<T> row)
	{
		if(row.onSelectionHandlerRegistration == null)
		{
			HandlerRegistration selectHandler = divRow.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					boolean selected = divRow.getStyleName().contains("-selected");
					getDataProvider().select(dataProviderRowIndex, !selected);
				}
			});
			row.onSelectionHandlerRegistration = selectHandler;
		}

		if(row.selected)
		{
			divRow.addStyleDependentName("-selected");
		}
		else
		{
			divRow.removeStyleDependentName("-selected");
		}
	}

	private void handleSortEvents(final PageableDataGrid<T>.Column<?> column, SelectableFlowPanel headerWrapper)
	{
		if(column.sortable && (!getDataProvider().isDirty() || getDataProvider() instanceof LazyProvider))
		{
			FlowPanel wrapperButtons = new FlowPanel();
			
			//create button UP
			final Button arrowUp = new Button();
			arrowUp.setText("U");
			arrowUp.addStyleName("arrowUp");
			wrapperButtons.add(arrowUp);

			//create button Down
			final Button arrowDown = new Button();
			arrowDown.setVisible(false);
			arrowDown.setText("D");
			arrowDown.addStyleName("arrowDown");
			wrapperButtons.add(arrowDown);
			
			//create buttons
			headerWrapper.add(wrapperButtons);

			headerWrapper.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					linkedComparators.remove(column.columnComparator);
					column.columnComparator.multiplier *= -1;
					linkedComparators.addFirst(column.columnComparator);
					column.sort();
				}
			});
			
			//set up visibility
			if(column.columnComparator != null)
			{
				if(column.columnComparator.multiplier > 0)
				{
					arrowUp.setVisible(false);
					arrowDown.setVisible(true);
				}
				else
				{
					arrowUp.setVisible(true);
					arrowDown.setVisible(false);
				}
			}
		}
	}

	@Override
	protected DivTable initializePagePanel()
	{
		DivTable divTable = new DivTable();
		return divTable;
	}

	//Render method is caching rendered rows in order to gain performance
	void refreshRowCache()
	{
		if(rows != null)
		{
			//remove row selecion handlers
			for(int i=0; i<rows.size();i++)
			{
				Row<T> row = rows.get(i);
				if(row.onSelectionHandlerRegistration != null)
				{
					row.onSelectionHandlerRegistration.removeHandler();
				}
			}
			//remove rows
			rows.clear();
		}
	}

	/**
	 * Rolls back any transaction started in the edition mode.
	 */
	@Override
	public void rollback() 
	{
		for(int i=0; i<rows.size();i++)
		{
			rows.get(i).undoChanges();
		}

		super.rollback();
		refreshRowCache();
	}

	@Override
	protected void setForEdition(int index, T object)
	{
		super.setForEdition(index, object);
	}
	
	public void setRowSelectStrategy(RowSelectStrategy rowSelectStrategy)
	{
		assert(rowSelectStrategy != null) : "the selection strategy cannot be null.";
		this.rowSelectStrategy = rowSelectStrategy;
	}
}
