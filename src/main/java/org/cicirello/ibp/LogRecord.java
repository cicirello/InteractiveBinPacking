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

import java.io.Serializable;
import java.time.Instant;

/** A record for a SessionLog. */
final class LogRecord implements Serializable {
  private static final long serialVersionUID = 1L;

  private String type;
  private String data;
  private long timestamp;

  LogRecord(String type, String data) {
    this.type = type;
    this.data = data;
    timestamp = Instant.now().toEpochMilli();
  }

  LogRecord(String type, String data, String strTimeStamp) {
    this.type = type;
    this.data = data;
    timestamp = Long.parseLong(strTimeStamp);
  }

  String getType() {
    return type;
  }

  String getData() {
    return data;
  }

  long getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || !(other instanceof LogRecord)) {
      return false;
    }
    LogRecord o = (LogRecord) other;
    return type.equals(o.type) && data.equals(o.data) && timestamp == o.timestamp;
  }

  @Override
  public int hashCode() {
    int h = Long.hashCode(timestamp);
    h = 31 * h + type.hashCode();
    h = 31 * h + data.hashCode();
    return h;
  }

  @Override
  public String toString() {
    // Used in generating session log file contents.
    return String.format(
        "<action>\n<type>%s</type>\n<data>%s</data>\n<timestamp>%d</timestamp>\n</action>\n",
        type, data, timestamp);
  }
}
