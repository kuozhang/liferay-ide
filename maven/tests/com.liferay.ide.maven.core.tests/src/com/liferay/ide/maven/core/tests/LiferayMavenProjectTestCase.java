/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.util.Set;

import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.PossibleValuesService;
import org.osgi.framework.Version;



/**
 * @author Gregory Amerson
 *
 */
@SuppressWarnings( "restriction" )
public abstract class LiferayMavenProjectTestCase extends AbstractMavenProjectTestCase
{
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );

    protected final ProjectCoreBase base = new ProjectCoreBase();

    protected void createTestBundleProfile( NewLiferayPluginProjectOp op )
    {
        NewLiferayProfile profile = op.getNewLiferayProfiles().insert();

        Set<String> vals = profile.getLiferayVersion().service( PossibleValuesService.class ).values();

        Version greatest = new Version( "6.2.1" );

        for( final String val : vals )
        {
            try
            {
                final Version v = new Version( val );

                if( greatest == null )
                {
                    greatest = v;
                }
                else
                {
                    if( CoreUtil.compareVersions( greatest, v ) < 0 )
                    {
                        greatest = v;
                    }
                }
            }
            catch( Exception e )
            {
            }
        }

        profile.setLiferayVersion( greatest.getMajor() + "." + greatest.getMicro() + "." + greatest.getMinor() );
        profile.setId( "test-bundle" );
        profile.setRuntimeName( base.getRuntimeVersion() );
        profile.setProfileLocation( ProfileLocation.projectPom );

        op.setActiveProfilesValue( "test-bundle" );
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        base.setupPluginsSDKAndRuntime();
    }

    protected boolean shouldSkipBundleTests() { return "true".equals( skipBundleTests ); }

}
