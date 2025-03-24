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

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads.PadMode;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads.UsingPadMode;

public class HWControlBankC extends HWControlBank implements HWIHasHost, HWPads.UsingPadMode {
  static final String CONTROL_BANK_ID = "C";

  // TODO set correct values
  static final int CONTROL_BANK_MIDI_CHANNEL = 0;
  static final int[] CONTROL_BANK_KNOB_CC = { 83, 85, 86, 87, 88, 89, 90, 91 };
  static final int[] CONTROL_BANK_FADER_CC = { 92, 93, 94, 95, 102, 103, 104, 105};
  static final int[] CONTROL_BANK_SOLO_CC = { 106, 107, 108, 109, 110, 111, 112, 113 };

  static final int CONTROL_BANK_SOLO_PRESSED_VAL = 127;

  private final HardwareActionBindable recModeEnable;
  private final HardwareActionBindable recModeDisable;

  private MidiOut midi0Out;

  public HWControlBankC(HardwareSurface surface) {
    super(surface, CONTROL_BANK_ID);

    recModeEnable = customAction(() -> { setPadMode(PadMode.MPK_PAD_CLIP_START_RECORD); });
    recModeDisable = customAction(() -> { 
      assert(getPadMode() == PadMode.MPK_PAD_CLIP_START_RECORD);
      revertPadMode(PadMode.MPK_PAD_CLIP_START_RECORD);
    });
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      S[i].pressedAction().setActionMatcher(
        midiIn.createCCActionMatcher(CONTROL_BANK_MIDI_CHANNEL,
          CONTROL_BANK_SOLO_CC[i], CONTROL_BANK_SOLO_PRESSED_VAL));
      S[i].releasedAction().setActionMatcher(
        midiIn.createCCActionMatcher(CONTROL_BANK_MIDI_CHANNEL,
          CONTROL_BANK_SOLO_CC[i], CONTROL_BANK_SOLO_RELEASED_VAL));

      F[i].setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_FADER_CC[i]));

      K[i].setAdjustValueMatcher(midiIn.createRelative2sComplementCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_KNOB_CC[i], KNOB_ROTATION_VAL));
    }
  }

  @Override
  public void bindCCActions() {

    // rec clip pad mode
    S[7].pressedAction().addBinding(recModeEnable);
    S[7].releasedAction().addBinding(recModeDisable);
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiIns) {
    midi0Out = midiOut;
  }

}
