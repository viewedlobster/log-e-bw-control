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

package se.loge.bwcontrol.common.ifc;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.BWHost;

public interface HasBWHost {
  static public ControllerHost host() {
    return BWHost.host();
  }

  public default Transport transport() {
    return BWHost.transport();
  }

  public default CursorTrack primaryTrack() {
    return BWHost.primaryTrack();
  }

  public default CursorDevice primaryDevice() {
    return BWHost.primaryDevice();
  }

  public default CursorDevice primaryInstrument() {
    return BWHost.primaryInstrument();
  }

  public default HardwareSurface surface() {
    return BWHost.surface();
  }

  public default HardwareActionBindable customAction(Runnable r, String descr) {
    return host().createAction(r, () -> descr);
  }

  public default HardwareActionBindable customAction(Runnable r) {
    return host().createAction(r, () -> { return "log-e-bw-control internal action"; });
  }

  public default void debugln(String fmt, Object... objs) {
    BWHost.debugln(fmt, objs);
  }

  public default void errorln(String fmt, Object... objs) {
    BWHost.errorln(fmt, objs);
  }

}
