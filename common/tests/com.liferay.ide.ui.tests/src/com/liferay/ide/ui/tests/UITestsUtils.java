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

package com.liferay.ide.ui.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ReflectionUtil;
import com.liferay.ide.core.util.StringUtil;

/**
 * Some methods are modified from eclipse wst sse tests
 *
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class UITestsUtils
{

    private static Map<IFile, IEditorPart> fileToEditorMap = new HashMap<IFile, IEditorPart>();

    public static StructuredTextEditor getEditor( IFile file )
    {
        StructuredTextEditor editor = (StructuredTextEditor) fileToEditorMap.get( file );

        if( editor == null )
        {
            try
            {
                final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                final IWorkbenchPage page = workbenchWindow.getActivePage();

                final IEditorPart editorPart = IDE.openEditor( page, file, true, true );

                assertNotNull( editorPart );

                if( editorPart instanceof SapphireEditorForXml )
                {
                    editor = ( (SapphireEditorForXml) editorPart ).getXmlEditor();
                }
                else if( editorPart instanceof StructuredTextEditor )
                {
                    editor = ( (StructuredTextEditor) editorPart );
                }
                else if( editorPart instanceof XMLMultiPageEditorPart )
                {
                    XMLMultiPageEditorPart xmlEditorPart = (XMLMultiPageEditorPart) editorPart;
                    editor = (StructuredTextEditor) xmlEditorPart.getAdapter( StructuredTextEditor.class );
                }

                assertNotNull( editor );
                standardizeLineEndings( editor );
                fileToEditorMap.put( file, editor );
            }
            catch( Exception e )
            {
                fail( "Could not open editor for " + file + " exception: " + e.getMessage() );
            }
        }

        return editor;
    }

    public static SourceViewerConfiguration getSourceViewerConfiguraion( IFile file ) throws Exception
    {
        Object obj =
            ExtendedConfigurationBuilder.getInstance().getConfiguration(
                ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION,
                file.getContentDescription().getContentType().getId() );

        if( obj != null && obj instanceof SourceViewerConfiguration )
        {
            return (SourceViewerConfiguration) obj;
        }
        else
        {
            StructuredTextEditor editor = getEditor( file );
            Method getConfMethod =
                ReflectionUtil.getDeclaredMethod( editor.getClass(), "getSourceViewerConfiguration", true );
            assertNotNull( getConfMethod );
            getConfMethod.setAccessible( true );
            obj = getConfMethod.invoke( editor );

            if( obj != null && obj instanceof SourceViewerConfiguration )
            {
                return (SourceViewerConfiguration) obj;
            }
        }

        return null;
    }

    public static void standardizeLineEndings( StructuredTextEditor editor )
    {
        final IDocument doc = editor.getTextViewer().getDocument();
        String contents = doc.get();
        contents = StringUtil.replace( contents, "\r\n", "\n" );
        contents = StringUtil.replace( contents, "\r", "\n" );
        doc.set( contents );
    }

    public static void deleteOtherProjects( IProject project ) throws Exception
    {
        final IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();

        for( IProject proj : projects )
        {
            if( !proj.getName().equals( project.getName() ) )
            {
                proj.delete( true, true, new NullProgressMonitor() );
            }
        }
    }

}