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

import java.awt.Desktop;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This class implements the a dialog window for displaying information to the user. The content is
 * formatted with html and supports hyperlinks, both externally in a browser as well as hyperlinks
 * within the dialog. It also supports scrolling. It relies on Java's JEditorPane, which only
 * supports html3 at the present time.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class InfoDialog extends JDialog {

  /**
   * Constructs an InfoDialog.
   *
   * @param f The frame for the application. The initial location of the InfoDialog will be centered
   *     over f.
   * @param title The title of the dialog
   * @param html Either the path, with filename, to the html file, or a string containing html.
   * @param frameAsParent true to set f as the parent
   * @param isModal true for a modal dialog
   * @param htmlIsFilePath true means that the html parameter is a path to a resource file, and
   *     false means html contains the actual html.
   */
  public InfoDialog(
      JFrame f,
      String title,
      String html,
      boolean frameAsParent,
      boolean isModal,
      boolean htmlIsFilePath) {
    super(frameAsParent ? f : (JFrame) null, title, isModal);

    boolean hideOnClose = !isModal;

    final URL url = htmlIsFilePath ? InteractiveBinPacking.class.getResource(html) : null;
    JEditorPane contents = new JEditorPane();
    contents.setEditable(false);
    contents.setMargin(new Insets(10, 10, 10, 10));
    contents.addHyperlinkListener(
        new HyperlinkListener() {
          @Override
          public void hyperlinkUpdate(HyperlinkEvent e) {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
              URL link = e.getURL();
              if (url != null && link != null && link.sameFile(url)) {
                contents.scrollToReference(link.getRef());
              } else if (url == null
                  && e.getDescription() != null
                  && e.getDescription().startsWith("#")) {
                contents.scrollToReference(e.getDescription().substring(1));
              } else if (link != null) {
                Desktop desktop = Desktop.getDesktop();
                try {
                  desktop.browse(link.toURI());
                } catch (Exception ex) {
                  JOptionPane.showMessageDialog(
                      f,
                      "Your system administrator doesn't allow me to open a web browser.\nYou will need to open a web browser and enter the address manually.",
                      "Opening Web Browser Disallowed by Administrator",
                      JOptionPane.INFORMATION_MESSAGE);
                }
              }
            }
          }
        });
    if (htmlIsFilePath) {
      try {
        contents.setPage(url);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
            f, "Unexpected error: Content file is missing.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      contents.setContentType("text/html");
      contents.setText(html);
      contents.setCaretPosition(0);
    }
    add(new JScrollPane(contents));
    setDefaultCloseOperation(hideOnClose ? JDialog.HIDE_ON_CLOSE : JDialog.DISPOSE_ON_CLOSE);
    setIconImage(InteractiveBinPacking.icon);
    pack();
    setSize(450, 475);
    setLocationRelativeTo(f);
    activate();
  }

  /**
   * Constructs an InfoDialog.
   *
   * @param f The frame for the application. The initial location of the InfoDialog will be centered
   *     over f.
   * @param title The title of the dialog
   * @param htmlFilePath The path, with filename, to the html file.
   */
  public InfoDialog(JFrame f, String title, String htmlFilePath) {
    this(f, title, htmlFilePath, false, false, true);
  }

  /** Makes the InfoDialog visible if it is not already visible. */
  public void activate() {
    setVisible(visibility);
    activationCount++;
  }

  /** Simple count of number of times the dialog box was activated. */
  private int activationCount;

  private static boolean visibility = true;

  /*
   * package private for testing purposes only
   */
  static void setTestingMode() {
    visibility = false;
  }

  /*
   * package private for testing purposes only
   */
  int getActivationCount() {
    return activationCount;
  }
}
