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

package se.loge.bwcontrol.mpk.state;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.mpk.state.MPKCState.ControlPager;
import se.loge.bwcontrol.mpk.state.MPKCState.PadEvt;
import se.loge.bwcontrol.mpk.state.MPKCState.PadMode;
import se.loge.bwcontrol.mpk.state.MPKCState.PagerEvt;

public class MPKState {
  private final MPKCState cstate;
  private final MPKBWState bwstate;

  public MPKState() {
    cstate = new MPKCState();
    bwstate = new MPKBWState();

    bwstate.connectMPKState(cstate);
  }

  public void init() {
    cstate.init();
  }

  public CStateField<ControlPager, PagerEvt> instrumentPager() {
    return cstate.instrumentPager;
  }

  public CStateField<PadMode, PadEvt> padMode() {
    return cstate.padMode;
  }

  public MPKBWState bitwig() {
    return bwstate;
  }
}
