/*
 *  CandleStick.java of project jchart2d, <enterpurposehere>. 
 *  Copyright (C) 2002 - 2012, Achim Westermann, created on Oct 9, 2012
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 *
 *
 * File   : $Source: /cvsroot/jchart2d/jchart2d/codetemplates.xml,v $
 * Date   : $Date: 2009/02/24 16:45:41 $
 * Version: $Revision: 1.2 $
 */

package info.monitorenter.gui.chart.tracepoints;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterBackgroundColor;

import java.awt.*;
import java.util.LinkedList;


/**
 * Faked tracepoint that adds the properties to contain all data for a
 * candlestick.
 * <p>
 * 
 * See <a target="_blank" href="http://en.wikipedia.org/wiki/Candlestick_chart"
 * >http://en.wikipedia.org/wiki/Candlestick_chart</a>
 * <p>
 * 
 * This implementation only works correctly with a special candlestick point
 * painter.
 * <p>
 * The original {@link #getY()} method is mapped to. The
 * original {@link #getX()} method is sufficient to be related to all other y
 * values as a candlestick has a single point in time (x value).
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 */
public class BackgroundColor extends TracePoint2D {

  /**
   * @param xValue
   *          the x coordinate.
   * 
   * @param yValue
   *          the y value.
   */
  public BackgroundColor(final double xValue, final double yValue, final Color bgColor) {
    super(xValue, yValue);
    this.m_bgColor = bgColor;
  }

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -135311007801611830L;

  /**
   * Returns the high y value.
   * <p>
   * 
   * @return the high y value.
   */
  Color m_bgColor;
  public Color getColor() {
    return this.m_bgColor;
  }

  @Override
  public double getX() {
      return super.getX();// + valueWidth;
  }

  @Override
  public double getY() {
      //Y is irrelevant for background painters, so we'll always return the midpoint Y
      if(getListener() != null && this.getListener().getRenderer() != null) {
          IAxis axis_Y = this.getListener().getRenderer().getAxisY(this.getListener());
          return (axis_Y.getMin() + axis_Y.getMax()) /2;
      }
    return super.getY();
  }

  TracePainterBackgroundColor tracePainter;
  public void setTracePainter(TracePainterBackgroundColor tracePainter) {
    this.tracePainter = tracePainter;
  }

  @Override
  public boolean isVisibleOnAxisX() {
    boolean result = !this.isDiscontinuation();
    if (result) {

      AAxis axis = (AAxis) this.getListener().getRenderer().getAxisX(this.getListener());

      LinkedList<ITracePoint2D> points = ((Trace2DSimple)this.getListener()).copyPointList();

      int indexOfMe = points.indexOf(this);

      ITracePoint2D nextPoint = null;
        if(points.size() > indexOfMe+1) {
          nextPoint = points.get(indexOfMe+1);
        }

      double
        minX = axis.getMin(),
        maxX = axis.getMax(),
        startX = this.getX(),
        endX = nextPoint == null ? maxX : nextPoint.getX(),
        valueWidth = endX - startX;

      if (startX + valueWidth < minX)
        result = false;
      else if (startX > maxX)
        result = false;
    }
    return result;
  }

  /*
  @Override
  public boolean isVisibleOnAxisX() {
    boolean result = !this.isDiscontinuation();
    AAxis axis = (AAxis) this.getListener().getRenderer().getAxisX(this.getListener());

    //todo: upgrade for time-skip axis support
    double minX = axis.getMin();
    double maxX = axis.getMax();
    if (result) {
      double myX = this.getX();
      if (myX + valueWidth < minX)
        result = false;
      else if (myX > maxX) 
        result = false;
    }
    return result;
  }*/
}
