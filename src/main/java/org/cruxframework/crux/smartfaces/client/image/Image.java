/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.image;

import org.cruxframework.crux.core.client.image.GWTFixImage.ConditionalImageRenderer;
import org.cruxframework.crux.core.client.select.SelectableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class Image extends SelectableWidget implements HasLoadHandlers, HasErrorHandlers, HasEnabled
{
	private static final ConditionalImageRenderer conditionalImageRenderer = GWT.create(ConditionalImageRenderer.class);
	private static final String DEFAULT_STYLE_NAME = "faces-Image";
	private static final String STYLE_DISABLED_SUFIX = "-disabled";
	private com.google.gwt.user.client.ui.Image image;
	
	public Image()
	{
		this(new com.google.gwt.user.client.ui.Image());
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Image(ImageElement element)
	{
		this(new InternalImage(element));
	}

	public Image(ImageResource resource)
	{
		this(new com.google.gwt.user.client.ui.Image(resource));
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Image(SafeUri url)
	{
		this(new com.google.gwt.user.client.ui.Image(url));
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Image(final SafeUri url, final int left, final int top, final int width, final int height)
	{
		this(new com.google.gwt.user.client.ui.Image(url, left, top, width, height));
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Image(String url)
	{
		this(new com.google.gwt.user.client.ui.Image(url));
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Image(final String url, final int left, final int top, final int width, final int height)
	{
		this(new com.google.gwt.user.client.ui.Image(url, left, top, width, height));
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	protected Image(com.google.gwt.user.client.ui.Image image)
	{
		this.image = image;
		initWidget(this.image);
	}
	
	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler)
	{
		return image.addErrorHandler(handler);
	}

	@Override
	public HandlerRegistration addLoadHandler(LoadHandler handler)
	{
		return image.addLoadHandler(handler);
	}

	public String getAltText()
	{
		return image.getAltText();
	}

	public int getOriginLeft()
	{
		return image.getOriginLeft();
	}

	public int getOriginTop()
	{
		return image.getOriginTop();
	}

	public String getUrl()
	{
		return image.getUrl();
	}

	@Override
	public boolean isEnabled()
	{
		return getSelectEventsHandler().isEnabled();
	}

	public void setAltText(String altText)
	{
		image.setAltText(altText);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if (enabled != isEnabled())
		{
			getSelectEventsHandler().setEnabled(enabled);
			if (enabled)
			{
				removeStyleDependentName(STYLE_DISABLED_SUFIX);
			}
			else
			{
				addStyleDependentName(STYLE_DISABLED_SUFIX);
			}
		}
	}

	public void setResource(ImageResource resource)
	{
		image.setResource(resource);
	}
	
	public void setStyleName(String style)
	{
		image.setStyleName(style);
	}
	
	public void setTitle(String title)
	{
		image.setTitle(title);
	}

	public void setUrl(SafeUri url)
	{
		image.setUrl(url);
	}

	public void setUrl(String url)
	{
		image.setUrl(url);
	}

	public void setUrlAndVisibleRect(final SafeUri url, final int left, final int top, final int width, final int height)
	{
		conditionalImageRenderer.renderImage(image, url, left, top, width, height);
	}

	public void setUrlAndVisibleRect(final String url, final int left, final int top, final int width, final int height)
	{
		conditionalImageRenderer.renderImage(image, url, left, top, width, height);
	}
	
	public void setVisible(boolean visible)
	{
		image.setVisible(visible);
	}

	public void setVisibleRect(final int left, final int top, final int width, final int height)
	{
		conditionalImageRenderer.renderImage(image, left, top, width, height);
	}
	
	@Override
	protected HandlerRegistration addClickHandler(ClickHandler handler)
	{
	    return image.addClickHandler(handler);
	}

	@Override
	protected HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return image.addHandler(handler, TouchEndEvent.getType());
	}

	@Override
	protected HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return image.addHandler(handler, TouchMoveEvent.getType());
	}

	@Override
	protected HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return image.addHandler(handler, TouchStartEvent.getType());
	}
	
	private static class InternalImage extends com.google.gwt.user.client.ui.Image
	{
		public InternalImage(ImageElement element)
		{
			super(element);
		}
	}
}
