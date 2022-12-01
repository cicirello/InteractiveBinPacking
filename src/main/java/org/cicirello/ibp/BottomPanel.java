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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * This class implements the bottom panel of the UI, which includes the UI elements for choosing an
 * action (item to move and destination bin to move it to).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class BottomPanel extends JPanel {

  /** Application. */
  private InteractiveBinPacking f;

  /** Maintains application state. */
  private ApplicationState state;

  /** Combo box of destinations. */
  private JComboBox<Bin> destinations;

  /** Combo box of items. */
  private JComboBox<Item> itemList;

  /** Called when item moved. */
  private CallBack onMove;

  /**
   * Constructs the panel.
   *
   * @param f The main frame of the application (for setting the parent when dialogs are opened).
   * @param state The state of the bins, which objects are in which bins, and other application
   *     state data such as the mode the application is in.
   * @param onMove The call method of this CallBack object will be called upon completing a move
   *     operation.
   */
  public BottomPanel(InteractiveBinPacking f, ApplicationState state, CallBack onMove) {
    super();
    this.state = state;
    this.f = f;
    this.onMove = onMove;
    setLayout(new FlowLayout(FlowLayout.CENTER, 20, ((FlowLayout) getLayout()).getVgap()));
    initCombos();
    initMove();
    initReset();
    setBorder(new EtchedBorder());
    setBackground(Color.WHITE);
  }

  /** Refreshes the UI elements in the panel. */
  public void refresh() {
    itemList.removeAllItems();
    destinations.removeItemAt(destinations.getItemCount() - 1);
    destinations.addItem(state.getFloor());
    ArrayList<Item> items = state.getItems();
    for (Item i : items) itemList.addItem(i);
  }

  /*
   * initializes the combo boxes
   */
  private void initCombos() {
    itemList = new JComboBox<Item>(state.getItems().toArray(new Item[0]));
    itemList.setFont(InteractiveBinPacking.font);
    destinations = new JComboBox<Bin>(state.getBins().toArray(new Bin[0]));
    destinations.setFont(InteractiveBinPacking.font);
    destinations.addItem(state.getFloor());
    JPanel move = new JPanel();
    move.setBackground(Color.WHITE);
    JLabel l = new JLabel("Move:");
    l.setFont(InteractiveBinPacking.font);
    move.add(l);
    move.add(itemList);
    add(move);
    JPanel to = new JPanel();
    to.setBackground(Color.WHITE);
    l = new JLabel("To:");
    l.setFont(InteractiveBinPacking.font);
    to.add(l);
    to.add(destinations);
    add(to);
  }

  /*
   * Initializes the reset button
   */
  private void initReset() {
    JButton reset = new JButton("RESET");
    reset.setFont(InteractiveBinPacking.font);
    reset.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            state.reset();
          }
        });
    add(reset);
  }

  /*
   * Initializes the move button.
   */
  private void initMove() {
    JButton step = new JButton("MOVE ITEM");
    step.setFont(InteractiveBinPacking.font);
    class MoveListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        Bin dest = (Bin) destinations.getSelectedItem();
        Item i = (Item) itemList.getSelectedItem();
        if (dest.contains(i)) {
          String message = dest + " already contains item " + i.name() + ".";
          state.moveAttempted();
          displayMessage(message, "Item already at destination", false);
        } else {
          int modeSelection = state.getMode();
          String modeString = state.getModeString();
          String modeName = state.getModeName();
          Floor theFloor = state.getFloor();
          if ((modeSelection == ApplicationState.MODE_FIRST_FIT
                  || modeSelection == ApplicationState.MODE_BEST_FIT)
              && !i.equals(theFloor.peek())) {
            state.moveAttempted();
            inOrderMessage(modeName, i, modeString);
          } else if ((modeSelection == ApplicationState.MODE_FIRST_FIT_DECREASING
                  || modeSelection == ApplicationState.MODE_BEST_FIT_DECREASING)
              && !theFloor.isLargest(i)) {
            state.moveAttempted();
            decreasingOrderMessage(modeName, i, modeString);
          } else if ((modeSelection == ApplicationState.MODE_FIRST_FIT
                  || modeSelection == ApplicationState.MODE_FIRST_FIT_DECREASING)
              && state.firstFitBin(i) != dest) {
            state.moveAttempted();
            firstFitMessage(modeName, dest, modeString);
          } else if ((modeSelection == ApplicationState.MODE_BEST_FIT
                  || modeSelection == ApplicationState.MODE_BEST_FIT_DECREASING)
              && !state.isBestFitBin(i, dest)) {
            state.moveAttempted();
            bestFitMessage(modeName, dest, i, modeString);
          } else {
            if (dest.fits(i)) {
              if (modeSelection != ApplicationState.MODE_PRACTICE) {
                String message =
                    "Good job! Your chosen item and bin\nare correct for the "
                        + modeName
                        + " heuristic.";
                displayMessage(message, modeString, true);
              }
              state.moveItem(i, dest);
              if (modeSelection != ApplicationState.MODE_PRACTICE && theFloor.isEmpty()) {
                String message =
                    "Good job! You successfully used the "
                        + modeName
                        + "\nheuristic to assign all items to bins.\nSwitch into Practice mode and see if you can\nfind a way to use fewer bins.";
                displayMessage(message, modeString, true);
                state.completedHeuristicMode();
              }
            } else {
              String message = "Item " + i.name() + " doesn't fit in that bin.";
              state.moveAttempted();
              displayMessage(message, "Insufficient capacity", false);
            }
            onMove.call();
          }
        }
      }
    }
    step.addActionListener(new MoveListener());
    add(step);
  }

  private void inOrderMessage(String heuristic, Item i, String modeString) {
    String message =
        "When using "
            + heuristic
            + ", you take the items in the order given.\nItem "
            + i.name()
            + " is not the next item.";
    displayMessage(message, modeString, false);
  }

  private void decreasingOrderMessage(String heuristic, Item i, String modeString) {
    String message =
        "When using "
            + heuristic
            + ", you always choose the largest unassigned item.\nItem "
            + i.name()
            + " is not the largest.";
    displayMessage(message, modeString, false);
  }

  private void firstFitMessage(String heuristic, Bin dest, String modeString) {
    String message =
        "When using "
            + heuristic
            + ", you place the item in the first bin with sufficient space.\n"
            + dest
            + " is not the first bin with room.";
    displayMessage(message, modeString, false);
  }

  private void bestFitMessage(String heuristic, Bin dest, Item i, String modeString) {
    String message =
        "When using "
            + heuristic
            + ", you place the item in the bin closest to capacity with sufficient space.\n"
            + dest
            + " is not the best-fit bin for Item "
            + i.name()
            + ".";
    displayMessage(message, modeString, false);
  }

  /*
   * package private to support testing
   */
  void displayMessage(String message, String modeString, boolean success) {
    JOptionPane.showMessageDialog(f, message, modeString, JOptionPane.INFORMATION_MESSAGE);
  }
}
