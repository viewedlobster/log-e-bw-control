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

package se.loge.bwcontrol.mpk;

import com.bitwig.extension.controller.api.ControllerHost;

import se.loge.bwcontrol.common.BWHost;
import se.loge.bwcontrol.mpk.state.MPKState;

public class MPKHost extends BWHost {

  public static MPKState state() {
    return mpkState;
  }

  public static void setup(ControllerHost h) {
    setup(h, LogLevel.INFO);
  }

  public static void setup(ControllerHost h, LogLevel lvl) {
    BWHost.setup(h, lvl);

    mpkState = new MPKState();
  }

  public static void init() {
    mpkState.init();
  }

  private static MPKState mpkState;
}
