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

import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.ifc.HasOutputState;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.MPKConst;
import se.loge.bwcontrol.mpk.hardware.button.HWCCToggleButton.ButtonState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMPKStateAccess;
import se.loge.bwcontrol.mpk.state.MPKCState.PadEvt;
import se.loge.bwcontrol.mpk.state.MPKCState.PadMode;

public class HWControlBankC extends HWControlBank implements HasBWHost, HWIMPKStateAccess {
  static final String CONTROL_BANK_ID = "C";

  static final int CONTROL_BANK_MIDI_CHANNEL = 0;
  static final int[] CONTROL_BANK_KNOB_CC = { 83, 85, 86, 87, 88, 89, 90, 91 };
  static final int[] CONTROL_BANK_FADER_CC = { 92, 93, 94, 95, 102, 103, 104, 105};
  static final int[] CONTROL_BANK_SOLO_CC = { 106, 107, 108, 109, 110, 111, 112, 113 };

  static final int CONTROL_BANK_CC_STATUS_BYTE = 0xb0 + CONTROL_BANK_MIDI_CHANNEL;

  static final int LIGHT_ON = 127;
  static final int LIGHT_OFF = 0;

  private static final int REC_MODE_BUTTON = 8;
  private static final int CLIP_OVERDUB_BUTTON = 7;

  public HWControlBankC() {
    super(CONTROL_BANK_ID, CONTROL_BANK_KNOB_CC,
      CONTROL_BANK_FADER_CC, CONTROL_BANK_SOLO_CC);
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++) {
      S[i].connectMidiIn(midiIn, midiIns);

      F[i].setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_FADER_CC[i]));

      K[i].setAdjustValueMatcher(midiIn.createRelative2sComplementCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_KNOB_CC[i], KNOB_ROTATION_VAL));
    }
  }

  @Override
  public void bindMidiIn() {
    S(REC_MODE_BUTTON).bindTo(state().padMode(), (mode) -> mode.rec(), PadEvt.CLIP_REC_BUTTON_ON, PadEvt.CLIP_REC_BUTTON_OFF);
    S(CLIP_OVERDUB_BUTTON).bindTo(state().bitwig().clipOverdub());
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++) {
      S[i].connectMidiOut(midiOut, midiOuts);
    }
  }

}
