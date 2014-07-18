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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.ui.draw2d.ColumnFigure;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.policies.PortletColumnComponentEditPolicy;
import com.liferay.ide.layouttpl.ui.policies.PortletColumnLayoutEditPolicy;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletColumnEditPart extends PortletRowLayoutEditPart
{
    public static final int COLUMN_MARGIN = 10;

    public PortletColumnEditPart()
    {
        super();
    }

    
    
    protected void createEditPolicies()
    {
        // allow removal of the associated model element
        installEditPolicy( EditPolicy.COMPONENT_ROLE, new PortletColumnComponentEditPolicy() );
        installEditPolicy( EditPolicy.LAYOUT_ROLE, new PortletColumnLayoutEditPolicy() );
    }

    protected IFigure createFigure()
    {
        IFigure f;

        if( getModelChildren().isEmpty() )
        {
            f = createFigureForModel();
            f.setOpaque( true ); // non-transparent figure
        }
        else
        {
            f = super.createFigure();
        }

        f.setBackgroundColor( new Color( null, 232, 232, 232 ) );

        return f;
    }

    protected Figure createFigureForModel()
    {
        if( getModel() instanceof PortletColumn )
        {
            RoundedRectangle rect = new ColumnFigure();
            rect.setCornerDimensions( new Dimension( 20, 20 ) );

            return rect;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public GridData createGridData()
    {
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 );
        gd.heightHint = getCastedParent().getDefaultColumnHeight();

        return gd;
    }

    @Override
    protected Panel createPanel()
    {
        return new Panel()
        {
            @Override
            protected void paintFigure( Graphics graphics )
            {
                Rectangle r = Rectangle.SINGLETON.setBounds( getBounds() );
                r.width -= 1;
                r.height -= 1;

                graphics.drawRoundRectangle( r, 20, 20 ); //draw the outline

                r.width -= 1;
                r.height -= 1;
                r.x += 1;
                r.y += 1;

                graphics.fillRoundRectangle( r, 20, 20 ); //fill the color
            }
        };
    }

    @Override
    public PortletColumn getCastedModel()
    {
        return (PortletColumn) getModel();
    }

    public PortletLayoutEditPart getCastedParent()
    {
        return (PortletLayoutEditPart) getParent();
    }

    @Override
    public int getMargin()
    {
        return COLUMN_MARGIN;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletColumn.WEIGHT_PROP.equals( prop ) )
        {
            refreshVisuals();
        }
        else
        {
            super.propertyChange( evt );
        }
    }

    protected void refreshVisuals()
    {
        super.refreshVisuals();

        Object constraint =
            ( (GraphicalEditPart) getParent() ).getFigure().getLayoutManager().getConstraint( getFigure() );
        GridData gd = null;

        if( constraint instanceof GridData )
        {
            gd = (GridData) constraint;

            if( gd.heightHint == SWT.DEFAULT )
            {
                gd.heightHint = getCastedParent().getDefaultColumnHeight();
            }

        }
        else
        {
            gd = createGridData();
        }

        ( (GraphicalEditPart) getParent() ).setLayoutConstraint( this, getFigure(), gd );

        int columnWeight = getCastedModel().getWeight();

        if( columnWeight == PortletColumn.DEFAULT_WEIGHT )
        {
            columnWeight = 100;
        }

        if( getFigure() instanceof ColumnFigure )
        {
            ( (ColumnFigure) getFigure() ).setText( columnWeight + "%" ); //$NON-NLS-1$
        }
    }
}
