/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020-2021  Vincent A. Cicirello
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

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * This class implements the user interface, specifically organizing the various panels, etc.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class UI extends JPanel {

  /** The top panel. */
  private TopPanel top;

  /** The bottom panel. */
  private BottomPanel bottom;

  /** The panel to the left. */
  private WestPanel west;

  /** The center panel. */
  private CenterPanel center;

  /** Maintains application state. */
  private ApplicationState state;

  /**
   * Constructor for objects of class UI
   *
   * @param f The frame for the application.
   */
  public UI(InteractiveBinPacking f) {
    final int NUM_BINS = 9;

    int[] weights = {36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 10, 22, 11, 33, 9, 17, 30};
    CallBack onSetInstance =
        new CallBack() {
          @Override
          public void call() {
            bottom.refresh();
            refresh();
          }
        };
    CallBack executeRefresh =
        new CallBack() {
          @Override
          public void call() {
            refresh();
          }
        };
    state =
        new ApplicationState(9, new Floor(weights), onSetInstance, executeRefresh, executeRefresh);

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(5, 5, 5, 5));

    top = new TopPanel(state);
    west = new WestPanel(NUM_BINS, state);
    center = new CenterPanel(NUM_BINS, state);
    bottom = new BottomPanel(f, state, executeRefresh);

    add(BorderLayout.NORTH, top);
    add(BorderLayout.WEST, west);
    JScrollPane scroll = new JScrollPane(center);
    scroll.setBorder(new EtchedBorder());
    add(BorderLayout.CENTER, scroll);
    add(BorderLayout.SOUTH, bottom);

    setBackground(Color.WHITE);
  }

  /**
   * Gets the ApplicationState object.
   *
   * @return the state of the application.
   */
  public ApplicationState getApplicationState() {
    return state;
  }

  /*
   * for ui refresh
   */
  private void refresh() {
    top.refresh();
    west.refresh();
    center.refresh();
    repaint();
  }
}
