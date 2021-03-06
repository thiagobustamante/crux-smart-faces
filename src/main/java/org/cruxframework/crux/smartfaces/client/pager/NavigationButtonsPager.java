/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.pager;

import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPager;
import org.cruxframework.crux.core.client.dataprovider.pager.PageEvent;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.button.Button;

/**
 * Base implementation for navigation-buttons-based pager
 * @author Thiago da Rosa de Bustamante
 */
public abstract class NavigationButtonsPager<T> extends AbstractPager<T> 
{
	private static final String STYLE_FIRST_BUTTON = "firstButton";
	private static final String STYLE_LAST_BUTTON = "lastButton";
	private static final String STYLE_NEXT_BUTTON = "nextButton";
	private static final String STYLE_PREVIOUS_BUTTON = "previousButton";
	
	private Button firstButton;
	private Button lastButton;
	private Button nextButton;
	private Button previousButton;
	
	/**
	 * Creates the "first page" navigation button
	 * @return
	 */
	protected Button createFirstPageButton()
	{
		final NavigationButtonsPager<T> pager = this;
		
		Button panel = createNavigationButton(STYLE_FIRST_BUTTON, 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, 1);
						if(!pageEvent.isCanceled())
						{
							firstPage();
						}
					}
				}
			}
		);
		
		this.firstButton = panel;
		
		return panel;
	}
	
	/**
	 * Creates the "last page" navigation button
	 * @return
	 */
	protected Button createLastPageButton()
	{
		final NavigationButtonsPager<T> pager = this;
		
		Button label = createNavigationButton(STYLE_LAST_BUTTON, 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getPageCount());
						if(!pageEvent.isCanceled())
						{
							lastPage();
						}
					}
				}
			}
		);
		
		this.lastButton = label;
		
		return label;
	}
	
	/**
	 * Creates the "next page" navigation button
	 * @return
	 */
	protected Button createNextButton()
	{
		final NavigationButtonsPager<T> pager = this;
		
		Button panel = createNavigationButton(STYLE_NEXT_BUTTON, 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getCurrentPage() + 1);
						if(!pageEvent.isCanceled())
						{
							if(hasNextPage())
							{
								nextPage();
							}
						}
					}
				}
			}
		);
		
		this.nextButton = panel;
		nextButton.setText(" ");
		
		return panel;
	}
	
	/**
	 * Creates the "previous page" navigation button
	 * @return
	 */
	protected Button createPreviousButton()
	{
		final NavigationButtonsPager<T> pager = this;
		
		Button panel = createNavigationButton(STYLE_PREVIOUS_BUTTON, 
			new SelectHandler() 
			{
				public void onSelect(SelectEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getCurrentPage() - 1);
						if(!pageEvent.isCanceled())
						{
							if(getCurrentPage() > 1)
							{
								previousPage();
							}
						}
					}
				}
			}
		);
		
		this.previousButton = panel;
		
		return panel;
	}
	
	@Override
	protected void onUpdate()
	{
		if(this.previousButton != null)
		{
			if(!hasPreviousPage() || !isEnabled())
			{
				this.previousButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.previousButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.nextButton != null)
		{
			if(!hasNextPage() || !isEnabled())
			{
				this.nextButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.nextButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.firstButton != null)
		{
			if(!hasPreviousPage() || !isEnabled())
			{
				this.firstButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.firstButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.lastButton != null)
		{
			if(!hasNextPage() || !isEnabled())
			{
				this.lastButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.lastButton.removeStyleDependentName(DISABLED);
			}
		}
	}
	
	@Override
	protected void setInteractionEnabled(boolean enabled) 
	{
		super.setInteractionEnabled(enabled);
		previousButton.setEnabled(enabled);
		nextButton.setEnabled(enabled);
		firstButton.setEnabled(enabled);
		lastButton.setEnabled(enabled);
	}
	
	/**
	 * Creates a generic navigation button
	 * @param styleName
	 * @param selectHandler
	 * @return
	 */
	private Button createNavigationButton(String styleName, SelectHandler selectHandler)
	{
		Button button = new Button();
		button.addStyleName(styleName);
		button.addSelectHandler(selectHandler);
		return button;
	}
}