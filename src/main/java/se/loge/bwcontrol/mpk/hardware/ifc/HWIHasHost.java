/*
 * Copyright (C) 2025 Ellen Arvidsson
 *
 * This file is part of log-e-bw-control.
 *
 * log-e-bw-control is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * log-e-bw-control is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with log-e-bw-control. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package se.loge.bwcontrol.mpk.hardware.ifc;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIHasHost {
  public default ControllerHost host() {
    return ExtensionStore.getStore().getHost();
  }

  public default Transport transport() {
    return ExtensionStore.getStore().getTransport();
  }

  public default CursorTrack primaryTrack() {
    return ExtensionStore.getStore().getPrimaryTrackCursor();
  }

  public default CursorDevice primaryDevice() {
    return ExtensionStore.getStore().getPrimaryDeviceCursor();
  }

  public default CursorDevice primaryInstrument() {
    return ExtensionStore.getStore().getPrimaryInstrumentCursor();
  }

  public default void println(String s) {
    host().println(s);
  }
}
