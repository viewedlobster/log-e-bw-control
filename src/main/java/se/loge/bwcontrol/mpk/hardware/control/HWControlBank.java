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

package se.loge.bwcontrol.mpk.hardware.control;

import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;

import se.loge.bwcontrol.common.ifc.CMidiIn;
import se.loge.bwcontrol.common.ifc.CMidiOut;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.MPKConst;
import se.loge.bwcontrol.mpk.hardware.button.HWCCToggleButton;

public abstract class HWControlBank implements HasBWHost, CMidiIn, CMidiOut {
  static int KNOB_ROTATION_VAL = 127;

  static final int CONTROL_BANK_SOLO_PRESSED_VAL = 127;
  static final int CONTROL_BANK_SOLO_RELEASED_VAL = 0;
  static final int CONTROL_BANK_MIDI_CHANNEL = 0;

  final int[] knobCCs;
  final int[] faderCCs;
  final int[] soloCCs;

  public final RelativeHardwareKnob[] K;
  public final HardwareSlider[] F;

  public final HWCCToggleButton[] S;

  String id;

  /*
   * control bank A: instrument: macros, filter 
   * control bank B: daw tracks; volume, arm, mute?, solo?
   * control bank C: global controls and control mode
   */

  public HWControlBank(String bankId, int[] knobCCs, int[] faderCCs, int[] soloCCs) {
    K = new RelativeHardwareKnob[MPKConst.MPK261_NUM_CONTROL_STRIPS];
    F = new HardwareSlider[MPKConst.MPK261_NUM_CONTROL_STRIPS];
    S = new HWCCToggleButton[MPKConst.MPK261_NUM_CONTROL_STRIPS];
    id = bankId;

    this.knobCCs = knobCCs;
    this.faderCCs = faderCCs;
    this.soloCCs = soloCCs;

    for ( int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++ ) {
      K[i] = surface().createRelativeHardwareKnob(String.format("cbank_%s_K_%d", id, i));
      F[i] = surface().createHardwareSlider(String.format("cbank_%s_F_%d", id, i));
      S[i] = new HWCCToggleButton(String.format("cbank_%s_S_%d", id, i),
        CONTROL_BANK_MIDI_CHANNEL, this.soloCCs[i], CONTROL_BANK_SOLO_PRESSED_VAL,
        CONTROL_BANK_SOLO_RELEASED_VAL);
    }
  }

  // get K as named on hardware
  public RelativeHardwareKnob K(int hwIndex) {
    assert(hwIndex >= 1 && hwIndex <= MPKConst.MPK261_NUM_CONTROL_STRIPS);
    return K[hwIndex - 1];
  }

  // get F as named on hardware
  public HardwareSlider F(int hwIndex) {
    assert(hwIndex >= 1 && hwIndex <= MPKConst.MPK261_NUM_CONTROL_STRIPS);
    return F[hwIndex - 1];
  }

  // get S as named on hardware
  public HWCCToggleButton S(int hwIndex) {
    assert(hwIndex >= 1 && hwIndex <= MPKConst.MPK261_NUM_CONTROL_STRIPS);
    return S[hwIndex - 1];
  }
}
