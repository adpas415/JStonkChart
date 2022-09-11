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

import java.awt.*;


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
 * The original {@link #getY()} method is mapped to {@link #getStart()}. The
 * original {@link #getX()} method is sufficient to be related to all other y
 * values as a candlestick has a single point in time (x value).
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 */
public class Tag extends TracePoint2D {

  /**
   * @param xValue
   *          the x coordinate.
   * 
   * @param yValue
   *          the y value.
   * 
   * @param xTag
   *          the image x inset in pixels
   * 
   * @param yTag
   *          the  image y inset in pixels
   * 
   * @param tag
   *          the image itself
   * 
   * @param xText
   *          the text x inset in pixels
   * 
   * @param yText
   *          the  text y inset in pixels
   */
  public Tag(final double xValue, final double yValue, final int xTag, final int yTag, final Image tag, final int xText, final int yText, final String textValue, final boolean adjustForText, final Color textColor) {
    super(xValue, yValue);
    this.m_Tag = tag;
    this.m_text = textValue;
    this.m_adjust = adjustForText;
    this.m_textColor = textColor;
    this.m_xTag = xTag;
    this.m_yTag = yTag;
    this.m_xText = xText;
    this.m_yText = yText;
  }

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -135311007801611830L;

  String m_text;
  /**
   * Returns the high y value.
   * <p>
   * 
   * @return the high y value.
   */
      
  public String getText() {
    return this.m_text;
  }
  Color m_textColor;
  public Color getColor() {
    return this.m_textColor;
  }
  Image m_Tag;
  public Image getTag() {
      return this.m_Tag;
  }
  int m_xTag;
  public int getTagX() {
      return this.m_xTag;
  }
  int m_yTag;
  public int getTagY() {
      return this.m_yTag;
  }
  int m_xText;
  public int getTextX() {
      return this.m_xText;
  }
  int m_yText;
  public int getTextY() {
      return this.m_yText;
  }
  boolean m_adjust;
  public boolean getAdjust() {
      return this.m_adjust;
  }
  

}
