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
import javax.swing.JTextField;

/**
 * This class implements the center panel of the UI, which lists the contents of all of the bins.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class CenterPanel extends JPanel {

  /** Contents of the bins. */
  private final ArrayList<JTextField> binContents;

  /** Maintains application state. */
  private final ApplicationState state;

  /**
   * Constructs the panel.
   *
   * @param numBins The number of bins to display
   * @param state The state of the application, enabling the panel to have access to the bin
   *     contents needed to display the bin contents.
   */
  public CenterPanel(int numBins, ApplicationState state) {
    super();
    this.state = state;
    binContents = new ArrayList<JTextField>();
    setLayout(new GridLayout(numBins, 1));
    ArrayList<Bin> bins = state.getBins();
    String s = "Bin ";
    for (int i = 1; i <= numBins; i++) {
      JPanel row = new JPanel();
      row.setLayout(new FlowLayout(FlowLayout.LEFT));
      JLabel l = new JLabel(s + i + ":");
      l.setFont(InteractiveBinPacking.font);
      row.add(l);
      int fieldWidth = i < 10 ? 25 : 24;
      JTextField t = new JTextField(bins.get(i - 1).contentsToString(), fieldWidth);
      t.setFont(InteractiveBinPacking.font);
      binContents.add(t);
      row.add(binContents.get(i - 1));
      row.setBackground(Color.WHITE);
      binContents.get(i - 1).setEditable(false);
      add(row);
    }
    setBackground(Color.WHITE);
  }

  /** Updates the display of the contents of the bins. */
  public void refresh() {
    ArrayList<Bin> bins = state.getBins();
    for (int i = 0; i < bins.size(); i++) {
      binContents.get(i).setText(bins.get(i).contentsToString());
    }
  }
}
