/*
 * Interactive Bin Packing.
 * Copyright (C) 2021-2023 Vincent A. Cicirello
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

import java.util.ArrayList;

/** A list of records for the SessionLog. */
final class RecordList extends ArrayList<LogRecord> {
  private static final long serialVersionUID = 1L;

  @Override
  @SuppressWarnings("EqualsUnsafeCast")
  public boolean equals(Object other) {
    /* // UNNECESSARY CHECK... access control prevents these cases
    if (other == null || !(other instanceof RecordList)) {
    	return false;
    }
    */
    RecordList r = (RecordList) other;
    if (size() != r.size()) {
      return false;
    }
    for (int i = 0; i < size(); i++) {
      if (!get(i).equals(r.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (LogRecord r : this) {
      h = 31 * h + r.hashCode();
    }
    return h;
  }

  @Override
  public String toString() {
    // Used in generating session log file contents.
    StringBuilder s = new StringBuilder();
    s.append("<actions>\n");
    for (LogRecord r : this) {
      s.append(r.toString());
    }
    s.append("</actions>\n");
    return s.toString();
  }
}
