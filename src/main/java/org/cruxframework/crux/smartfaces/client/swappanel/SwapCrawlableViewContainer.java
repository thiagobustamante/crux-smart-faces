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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.ChangeViewEvent;
import org.cruxframework.crux.core.client.screen.views.ChangeViewHandler;
import org.cruxframework.crux.core.client.screen.views.HasChangeViewHandlers;
import org.cruxframework.crux.core.client.screen.views.SingleCrawlableViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation.SwapAnimationCallback;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapViewContainer.Direction;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author bruno.rafael
 *
 * - EXPERIMENTAL - THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class SwapCrawlableViewContainer extends SingleCrawlableViewContainer implements HasChangeViewHandlers
{
	/**
	 * Default css style of the component.
	 */
	public static final String DEFAULT_STYLE_NAME = "faces-SwapCrawlableViewContainer";

	private Panel active;
	private SwapAnimation animationBackward;
	private boolean animationEnabled = true;
	private SwapAnimation animationForward;
	private boolean autoRemoveInactiveViews = false;
	private SwapAnimation defaultAnimation = SwapAnimation.bounceLeft;
	private Direction defaultDirection = Direction.FORWARD;
	private boolean isAnimationRunning = false;
	private Panel swap;
	private SwapPanel swapPanel;

	/**
	 * Simple constructor.
	 */
	public SwapCrawlableViewContainer()
	{
		this(false);
	}

	/**
	 * Default constructor.
	 * 
	 * @param clearPanelsForDeactivatedViews if true will clean panels if a view is deactivated.
	 */
	public SwapCrawlableViewContainer(boolean clearPanelsForDeactivatedViews)
	{
		super(new SwapPanel(), clearPanelsForDeactivatedViews);
		swapPanel = getMainWidget();
		swapPanel.setStyleName(DEFAULT_STYLE_NAME);
		active = new SimplePanel();
		swap = new SimplePanel();
	}

	@Override
	public HandlerRegistration addChangeViewHandler(ChangeViewHandler handler)
	{
		return addHandler(handler, ChangeViewEvent.getType());
	}

	/**
	 * @return the animationBackward
	 */
	public SwapAnimation getAnimationBackward()
	{
		return animationBackward;
	}

	/**
	 * @return the animationForward
	 */
	public SwapAnimation getAnimationForward()
	{
		return animationForward;
	}

	/**
	 * @return the default animation.
	 */
	public SwapAnimation getDefaultAnimation()
	{
		return defaultAnimation;
	}

	/**
	 * @return - if the animation is enabled
	 */
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * Return if the animation is running.
	 * 
	 * @return boolean
	 */
	public boolean isAnimationRunning()
	{
		return isAnimationRunning;
	}

	/**
	 * @return boolean - If true the auto remove inactive views is activate.
	 */
	public boolean isAutoRemoveInactiveViews()
	{
		return autoRemoveInactiveViews;
	}

	/**
	 * @param animationBackward the animationBackward to set
	 */
	public void setAnimationBackward(SwapAnimation animationBackward)
	{
		this.animationBackward = animationBackward;
	}

	/**
	 * Set the duration for the animations
	 * 
	 * @param duration animations duration in seconds
	 */
	public void setAnimationDuration(double duration)
	{
		swapPanel.setAnimationDuration(duration);
	}

	/**
	 * @param enabled - if true the animation will be enabled
	 */
	public void setAnimationEnabled(boolean enabled)
	{
		animationEnabled = enabled;
	}

	/**
	 * @param enabled - if true the animation will be enabled
	 * @param device - type of device
	 */
	public void setAnimationEnabled(boolean enabled, Device device)
	{
		if (device == Device.all || Screen.getCurrentDevice() == device)
		{
			animationEnabled = enabled;
		}
	}
	
	/**
	 * 
	 * @param enabled - if true the animation will be enabled
	 * @param size - screen size
	 */
	public void setAnimationEnabled(boolean enabled, Size size)
	{
		if (Screen.getCurrentDevice().getSize() == size)
		{
			animationEnabled = enabled;
		}
	}

	/**
	 * Enable animation to large display device.
	 * 
	 * @param enabled - boolean
	 */
	public void setAnimationEnabledForLargeDevices(boolean enabled)
	{
		setAnimationEnabled(enabled, Size.large);
	}

	/**
	 * Enable animation to small display device.
	 * 
	 * @param enabled - boolean
	 */
	public void setAnimationEnabledForSmallDevices(boolean enabled)
	{
		setAnimationEnabled(enabled, Size.small);
	}

	/**
	 * @param animationForward the animationForward to set
	 */
	public void setAnimationForward(SwapAnimation animationForward)
	{
		this.animationForward = animationForward;
	}

	/**
	 * @param autoRemoveInactiveViews - If true the swapViewContainer will remove automatically.
	 */
	public void setAutoRemoveInactiveViews(boolean autoRemoveInactiveViews)
	{
		this.autoRemoveInactiveViews = autoRemoveInactiveViews;
	}

	/**
	 * @param defaultAnimation the default animation.
	 */
	public void setDefaultAnimation(SwapAnimation defaultAnimation)
	{
		this.defaultAnimation = defaultAnimation;
	}

	/**
	 * Define the default direction for showView animations.
	 * 
	 * @param direction
	 */
	public void setDefaultDirection(Direction direction)
	{
		defaultDirection = direction;
	}

	/**
	 * @param viewName - The name of view will be show
	 */
	@Override
	public void showView(String viewName)
	{
		showView(viewName, viewName, null);
	}

	/**
	 * @param viewName - The name of view will be show
	 * @param viewId - The id of view will be show
	 * @param parameter
	 */
	public void showView(String viewName, final String viewId, final Object parameter)
	{
		SwapAnimation animation = (defaultDirection == Direction.BACKWARDS)?animationBackward:animationForward;
		if (animation == null)
		{
			animation = defaultAnimation;
		}
		
		showView(viewName, viewId, animation, null, parameter);
	}

	/**
	 * @param viewName - The name of view will be show
	 * @param viewId - The id of view will be show
	 * @param animation
	 * @param animationCallback - the callback that will be called when the animation finished
	 * @param parameter
	 */
	public void showView(String viewName, final String viewId, final SwapAnimation animation,
	    final SwapAnimationCallback animationCallback, final Object parameter)
	{
		if (views.containsKey(viewId))
		{
			renderView(getView(viewId), animation, animationCallback, parameter);
		}
		else
		{
			createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					if (addView(view, false, parameter))
					{
						renderView(view, animation, animationCallback, parameter);
					}
					else
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
					}
				}
			});
		}
	}

	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
		boolean activated = super.activate(view, containerPanel, parameter);
		if (activated)
		{
			swapPanelVariables();
		}
		return activated;
	}

	@Override
	protected Panel getContainerPanel(View view)
	{
		assert (view != null) : "Can not retrieve a container for a null view";
		if (activeView != null && activeView.getId().equals(view.getId()))
		{
			return active;
		}
		return swap;
	}

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean renderView(View view, Object parameter)
	{
		SwapAnimation animation = (defaultDirection == Direction.BACKWARDS)?animationBackward:animationForward;
		if (animation == null)
		{
			animation = defaultAnimation;
		}
		
		return renderView(view, animation, null, parameter);
	}

	protected boolean renderView(View view, SwapAnimation animation, final SwapAnimationCallback animationCallback, Object parameter)
	{
		if (activeView == null || !activeView.getId().equals(view.getId()))
		{
			if (!isAnimationRunning)
			{
				isAnimationRunning = true;
				final View previous = activeView;
				final View next = view;
				boolean rendered = super.renderView(view, parameter);
				if (rendered)
				{
					if (previous == null || !animationEnabled)
					{
						swapPanel.setCurrentWidget(active);
						concludeViewsSwapping(previous, next);
						isAnimationRunning = false;
					}
					else
					{
						swapPanel.transitTo(active, animation, animationEnabled, new SwapAnimationCallback()
						{
							@Override
							public void onAnimationCompleted()
							{
								concludeViewsSwapping(previous, next);
								if (animationCallback != null)
								{
									animationCallback.onAnimationCompleted();
								}
								isAnimationRunning = false;
							}
						});
					}
				}
			}
		}
		return false;
	}

	@Override
	protected void showView(String viewName, boolean backButtonPressed)
	{
		if (backButtonPressed)
		{
			showView(viewName, viewName, this.animationBackward != null ? this.animationBackward : this.defaultAnimation, null, null);
		}
		else
		{
			showView(viewName, viewName, this.animationForward != null ? this.animationForward : this.defaultAnimation, null, null);
		}
	}

	private void concludeViewsSwapping(final View previous, final View next)
	{
		swap.clear();
		ChangeViewEvent.fire(SwapCrawlableViewContainer.this, previous, next);
		if (previous != null && autoRemoveInactiveViews)
		{
			previous.removeFromContainer();
		}
	}

	private void swapPanelVariables()
	{
		Panel temp = active;
		active = swap;
		swap = temp;
	}
}
