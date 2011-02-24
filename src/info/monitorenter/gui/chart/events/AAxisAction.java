/*
 *  AChart2DAction, base for actions to trigger on charts.
 *  Copyright (C) Achim Westermann, created on 10.12.2004, 13:48:55
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
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
 */
package info.monitorenter.gui.chart.events;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;

import java.beans.PropertyChangeListener;

/**
 * The base class that connects triggered actions with an
 * {@link info.monitorenter.gui.chart.axis.AAxis} instance.
 * <p>
 * Every subclass may delegate it's constructor-given <code>Axis</code>
 * instance as protected member <code>m_axis</code>.
 * </p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 * @version $Revision: 1.5 $
 * 
 */
public abstract class AAxisAction
    extends AChart2DAction implements PropertyChangeListener {

  /** The target of this action. */
  private int m_axis;

  /**
   * Create an <code>Action</code> that accesses the chart's axis by argument
   * <code>axis</code> and identifies itself with the given action String.
   * <p>
   * 
   * @param chart
   *          the owner of the axis to trigger actions upon.
   * 
   * @param axis
   *          needed to identify the axis of the chart: one of {@link Chart2D#X},
   *          {@link Chart2D#Y}.
   * 
   * @param description
   *          the descriptive <code>String</code> that will be displayed by
   *          {@link javax.swing.AbstractButton} subclasses that get this
   *          <code>Action</code> assigned (
   *          {@link javax.swing.AbstractButton#setAction(javax.swing.Action)}).
   * 
   */
  public AAxisAction(final Chart2D chart, final String description, final int axis) {
    super(chart, description);
    this.m_axis = axis;
  }

  /**
   * Returns the axis that is controlled.
   * <p>
   * Note that several calls may return different instances (<code>a.getAxis() == a.getAxis()</code>
   * may be false) in case the corresponding chart of the former axis gets a new
   * axis assigned.
   * <p>
   * 
   * @return the axis that is controlled.
   */
  protected IAxis getAxis() {
    // update in case the corresponding chart has a new axis:
    IAxis axis = null;
    switch (this.m_axis) {
      case Chart2D.X:
        axis = this.m_chart.getAxisX();
        break;
      case Chart2D.Y:
        axis = this.m_chart.getAxisY();
        break;
      default:
        break;
    }
    return axis;
  }
}