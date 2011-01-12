/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({
	"unchecked", "rawtypes"
})
public abstract class PluginFacetInstallDataModelProvider extends FacetInstallDataModelProvider
	implements IPluginProjectDataModelProperties {

	protected LibraryInstallDelegate libraryDelegate = null;

	public PluginFacetInstallDataModelProvider() {
		super();
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return getPluginFacetId();
		}
		else if (propertyName.equals(LIFERAY_PLUGIN_LIBRARY_DELEGATE)) {
			if (libraryDelegate == null) {
				libraryDelegate =
					new LibraryInstallDelegate(
						(IFacetedProjectWorkingCopy) getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY),
						(IProjectFacetVersion) getProperty(IFacetDataModelProperties.FACET_VERSION));
			}
			
			return libraryDelegate;
		}
		
		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propNames = super.getPropertyNames();
		
		propNames.add(LIFERAY_PLUGIN_LIBRARY_DELEGATE);
		propNames.add(LIFERAY_SDK_NAME);
		
		return propNames;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (propertyName.equals(IFacetDataModelProperties.FACET_VERSION)) {
			if (this.libraryDelegate != null) {
				libraryDelegate.setProjectFacetVersion((IProjectFacetVersion) propertyValue);
			}
		}
		
		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String name) {
		if (LIFERAY_SDK_NAME.equals(name)) {
			String sdkName = getStringProperty(LIFERAY_SDK_NAME);
			
			if (CoreUtil.isNullOrEmpty(sdkName)) {
				return ProjectCorePlugin.createErrorStatus("No Plugin SDK configured.");
			}
			
			SDK sdk = SDKManager.getInstance().getSDK(sdkName);
			
			if (sdk == null) {
				return ProjectCorePlugin.createErrorStatus("Plugin SDK (" + sdkName + ") is not defined.");
			}
			
			return Status.OK_STATUS;
		}
		
		return super.validate(name);
	}

	protected abstract String getPluginFacetId();

}
