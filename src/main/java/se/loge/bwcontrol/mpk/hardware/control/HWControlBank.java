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

import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiBinding;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;

public abstract class HWControlBank implements HWIMidiIn, HWIMidiOut, HWIMidiBinding {
  public final int MPK261_NUM_CONTROL_STRIPS = 8;
  
  static int KNOB_ROTATION_VAL = 127;

  static final int CONTROL_BANK_SOLO_PRESSED_VAL = 127;
  static final int CONTROL_BANK_SOLO_RELEASED_VAL = 0;

  final HardwareButton[] S;
  final RelativeHardwareKnob[] K;
  final HardwareSlider[] F;

  String id;

  /*
   * control bank A: instrument: macros, filter 
   * control bank B: daw tracks; volume, arm, mute?, solo?
   * control bank C: global controls and control mode
   */

  public HWControlBank(HardwareSurface hwsurface, String bankId) {
    S = new HardwareButton[MPK261_NUM_CONTROL_STRIPS];
    K = new RelativeHardwareKnob[MPK261_NUM_CONTROL_STRIPS];
    F = new HardwareSlider[MPK261_NUM_CONTROL_STRIPS];
    id = bankId;

    for ( int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++ ) {
      S[i] = hwsurface.createHardwareButton(String.format("cbank_%s_S_%d", id, i));
      K[i] = hwsurface.createRelativeHardwareKnob(String.format("cbank_%s_K_%d", id, i));
      F[i] = hwsurface.createHardwareSlider(String.format("cbank_%s_F_%d", id, i));
    }

    // TODO: add the lights
  }

}
