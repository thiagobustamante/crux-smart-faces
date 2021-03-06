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
package org.cruxframework.crux.smartfaces.themes.client.common;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Claudio Holanda Junior
 */
public interface SmartFacesResourcesCommon extends ClientBundle
{
	@Source("org/cruxframework/crux/smartfaces/themes/client/common/svg-icon-close.svg")
	DataResource svgIconClose();
	
	@Source("org/cruxframework/crux/smartfaces/themes/client/common/svg-icon-close-white.svg")
	DataResource svgIconCloseWhite();
	
	@Source("org/cruxframework/crux/smartfaces/themes/client/common/svg-icon-arrow.svg")
	DataResource svgIconArrow();
	
	@Source("svg-icon-paginator-first.svg")
	DataResource svgIconPaginatorFirst();
	
	@Source("svg-icon-paginator-last.svg")
	DataResource svgIconPaginatorLast();
	
	@Source("svg-icon-paginator-next.svg")
	DataResource svgIconPaginatorNext();
	
	@Source("svg-icon-paginator-prev.svg")
	DataResource svgIconPaginatorPrev();
	
	@Source("svg-icon-arrow-down.svg")
	DataResource svgIconArrowDown();
	
	@Source("svg-icon-arrow-up-down-white.svg")
	DataResource svgIconArrowUpDownWhite();
	
	@Source("svg-icon-arrow-up-white.svg")
	DataResource svgIconArrowUpWhite();
	
	@Source("svg-icon-arrow-down-white.svg")
	DataResource svgIconArrowDownWhite();
	
}
