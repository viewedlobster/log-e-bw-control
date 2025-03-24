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

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads;

public class MPKStateAccess implements HWIHasHost {

  private HWPads pads;

  public MPKStateAccess() {}

  public HWPads.PadMode getPadMode() {
    assert(pads != null);
    return pads.getMode();
  }

  public void setPadMode(HWPads.PadMode m) {
    assert(pads != null);
    pads.setMode(m);
  }

  public void revertPadMode(HWPads.PadMode m) {
    assert(pads != null);
    if (m == pads.getMode()) {
      pads.revertMode();
    } else {
      errorln(
        String.format("reverting pad mode %s when actual mode is %s",
          m.toString(), getPadMode().toString()));
    }
  }

  public void setHWPads(HWPads pads) {
    this.pads = pads;
  }


}
