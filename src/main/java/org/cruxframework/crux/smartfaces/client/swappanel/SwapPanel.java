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
package org.cruxframework.crux.smartfaces.client.swappanel;


import org.cruxframework.crux.core.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.core.client.event.swap.SwapEvent;
import org.cruxframework.crux.core.client.event.swap.SwapHandler;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation.SwapAnimationCallback;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation.SwapAnimationHandler;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that allows animated swap between widgets.
 * 
 * @author bruno.rafael
 *
 */
public class SwapPanel extends Composite implements HasSwapHandlers, HasAnimation
{
	/**
	 * Default css style of the current panel.
	 */
	public static final String CURRENT_STYLE_NAME = "faces-SwapPanel-currentPanel";
	
	/**
	 * Default css style of the component.
	 */
	public static final String DEFAULT_STYLE_NAME = "faces-SwapPanel";
	
	/**
	 * Default css style of the next panel. 
	 */
	public static final String NEXT_STYLE_NAME = "faces-SwapPanel-nextPanel";
	
	private boolean animating = false;
	private SwapAnimation animation;
	private double animationDuration = -1;
	
	private boolean animationEnabled = true;
	private FlowPanel contentPanel;
	private SimplePanel currentPanel = new SimplePanel();
	private boolean fitToChildrenHeight = true;
	
	private String height = "auto";

	private SimplePanel nextPanel = new SimplePanel();
	
	/**
	 *  The default constructor.
	 */
	public SwapPanel()
    {	
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		
		contentPanel = new FlowPanel();
		initWidget(contentPanel);
		
		setStyleName(DEFAULT_STYLE_NAME);
		currentPanel.setStyleName(CURRENT_STYLE_NAME);
		nextPanel.setStyleName(NEXT_STYLE_NAME);
		
		contentPanel.add(currentPanel);
		contentPanel.add(nextPanel);
    }
	
	/**
	 * @param animation Type of animation
	 */
	public SwapPanel(SwapAnimation animation)
    {	
		this();
		this.animation = animation;
    }
	
	/**
	 * @param animation type of animation 
	 * @param enabled If the animation is enabled
	 */
	public SwapPanel(SwapAnimation animation, boolean enabled)
	{
		this();
		this.animation = animation;
		this.animationEnabled = enabled;
	}

	@Override
    public HandlerRegistration addSwapHandler(SwapHandler handler)
    {
		return addHandler(handler, SwapEvent.getType());
    }
	
	/**
	 *  Clear the Panel.
	 */
	public void clear()
	{
		currentPanel.clear();
		nextPanel.clear();
	}
	
	/**
	 * @return Animation
	 */
	public SwapAnimation getAnimation()
	{
		return animation;
	}

	/** Return the current widget in the panel.
	 *  
	 * @return Widget the current widget
	 */
	public Widget getCurrentWidget()
	{
		return this.currentPanel.getWidget();
	}
	
	public boolean isAnimating()
	{
		return animating;
	}
	
	@Override
    public boolean isAnimationEnabled()
    {
	    return animationEnabled;
    }

    public boolean isFitToChildrenHeight()
	{
		return fitToChildrenHeight;
	}	
    
	/**
	 * @param animation type Animation type
	 */
	public void setAnimation(SwapAnimation animation)
	{
		this.animation =  animation;
	}
	
	/**
	 * Set the duration for the animations
	 * @param duration animations duration in seconds
	 */
	public void setAnimationDuration(double duration)
	{
		this.animationDuration = duration;
	}
	
	@Override
    public void setAnimationEnabled(boolean enable)
    {
	    this.animationEnabled = enable;
	    
    }
	
	/**
	 * Sets the widget that will be initially visible on this panel. 
	 * @param widget widget that will be initially
	 */
	public void setCurrentWidget(final Widget widget) 
	{
		if (widget != null) 
		{
			if (isAnimating())
			{
				completedAnimation(null);
			}
			setPanelHeightOnWidgetAttached(widget);
			this.currentPanel.clear();
			this.currentPanel.add(widget);
		}
	}
	
	public void setFitToChildrenHeight(boolean fitToChildrenHeight)
	{
		if (!this.fitToChildrenHeight && fitToChildrenHeight)
		{
			currentPanel.setHeight("auto");
			nextPanel.setHeight("auto");
		}
		else if (!fitToChildrenHeight)
		{
			setHeight(height);
		}
		this.fitToChildrenHeight = fitToChildrenHeight;
	}
	
	@Override
	public void setHeight(String height)
	{
	    this.height = height;
		super.setHeight(height);
	    
	    if (!fitToChildrenHeight)
	    {
	    	currentPanel.setHeight(height);
	    	nextPanel.setHeight(height);
	    }
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneSwapPanel());
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneSwapPanel());
		}
	}

	/**
	 * Changes the widget being shown on this widget.
	 * @param widget - the widget will be insert in the swapPanel
	 */
	public void transitTo(final Widget widget) 
	{
		transitTo(widget, animation, this.animationEnabled, null);
	}

	/**
	 * Changes the widget being shown on this widget.
	 * @param widget - the widget will be insert in the swapPanel
	 * @param animation - type of animation
	 */
	public void transitTo(final Widget widget, SwapAnimation animation) 
	{
		transitTo(widget, animation, this.animationEnabled, null);
	}

	/**
	 * Changes the widget being shown on this widget.
	 * @param widget - the widget will be insert in the swapPanel
	 * @param animation - type of animation
	 * @param animationEnabled - type of animation
	 */
	public void transitTo(final Widget widget, SwapAnimation animation, boolean animationEnabled, final SwapAnimationCallback callback)
	{
		if (!animating)
		{
			setPanelHeightOnWidgetAttached(widget);
			
			animating = true;
			nextPanel.clear();
			nextPanel.add(widget);

			if (animationEnabled && animation != null)
			{
				animation.animate(nextPanel, currentPanel, new SwapAnimationHandler()
				{
					@Override
					public void setInElementFinalState(Widget in)
					{
						Style style = in.getElement().getStyle();
						style.setDisplay(Display.NONE);
					}

					@Override
					public void setInElementInitialState(Widget in)
					{
						Style style = in.getElement().getStyle();
						style.setDisplay(Display.BLOCK);
						style.setTop(0, Unit.PX);
					}

					@Override
					public void setOutElementFinalState(Widget out)
					{
						Style style = out.getElement().getStyle();
						style.setDisplay(Display.BLOCK);
					}

					@Override
					public void setOutElementInitialState(Widget out)
					{
						Style style = out.getElement().getStyle();
						style.setDisplay(Display.NONE);
						style.setTop(0, Unit.PX);
					}
				}, new SwapAnimationCallback()
				{

					@Override
					public void onAnimationCompleted()
					{
						completedAnimation(callback);
					}
				}, animationDuration);
			}
			else
			{
				completedAnimation(callback);
			}
		}
	}

	private void completedAnimation(final SwapAnimationCallback callback)
    {
    	if (animating)
	    {
	    	currentPanel.clear();
	    	currentPanel.add(nextPanel.getWidget());
	    	nextPanel.clear();
	    	animating = false;
	    	SwapEvent.fire(SwapPanel.this);
	    	if (callback != null)
	    	{
	    		callback.onAnimationCompleted();
	    	}
	    }
    }

	private void setPanelHeightOnWidgetAttached(final Widget widget)
	{
		if (fitToChildrenHeight)
		{
			widget.addAttachHandler(new Handler() 
			{
				@Override
				public void onAttachOrDetach(AttachEvent event) 
				{
					if (event.isAttached())
					{
						SwapPanel.this.setHeight(widget.getOffsetHeight() + "px");	
					}
				}
			});
		}
	}
}
