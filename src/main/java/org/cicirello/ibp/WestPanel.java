/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020-2023 Vincent A. Cicirello
 *
 * This file is part of Interactive Bin Packing.
 *
 * Interactive Bin Packing is free software:
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Interactive Bin Packing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.cicirello.ibp;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * This class implements the west panel of the UI, which lists the capacities of the bins and how
 * much of it is used.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class WestPanel extends JPanel {

  /** List of how much of each bin is used. */
  private final ArrayList<JLabel> usedLabels;

  /** Maintains application state. */
  private final ApplicationState state;

  /**
   * Constructs the panel.
   *
   * @param numBins The number of bins to display
   * @param state The state of the application, enabling the panel to have access to the bin
   *     contents needed to be able to keep the labels up to date.
   */
  public WestPanel(int numBins, ApplicationState state) {
    super();
    this.state = state;
    setLayout(new GridLayout(numBins, 1));
    usedLabels = new ArrayList<JLabel>();
    ArrayList<Bin> bins = state.getBins();
    for (int i = 1; i <= numBins; i++) {
      JPanel row = new JPanel();
      row.setLayout(new FlowLayout(FlowLayout.LEFT));
      row.setBackground(Color.WHITE);
      JLabel l =
          new JLabel("Capacity: " + bins.get(i - 1).capacity() + "     ", SwingConstants.LEFT);
      l.setFont(InteractiveBinPacking.font);
      row.add(l);
      int u_i = bins.get(i - 1).used();
      String spaces = u_i < 10 ? "        " : (u_i < 100 ? "     " : "    ");
      String u = "Used: " + u_i + spaces;
      l = new JLabel(u, SwingConstants.LEFT);
      l.setFont(InteractiveBinPacking.font);
      usedLabels.add(l);
      row.add(usedLabels.get(i - 1));
      add(row);
    }
    setBorder(new EtchedBorder());
    setBackground(Color.WHITE);
  }

  /** Update the labels displaying how much of the capacity of the bins is used. */
  public void refresh() {
    ArrayList<Bin> bins = state.getBins();
    for (int i = 0; i < bins.size(); i++) {
      int u_i = bins.get(i).used();
      String spaces = u_i < 10 ? "        " : (u_i < 100 ? "     " : "    ");
      String u = "Used: " + u_i + spaces;
      usedLabels.get(i).setText(u);
    }
  }
}
