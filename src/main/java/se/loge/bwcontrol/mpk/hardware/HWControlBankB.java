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

package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

public class HWControlBankB extends HWControlBank {
  static final String CONTROL_BANK_ID = "B";

  // TODO set correct values
  static final int CONTROL_BANK_MIDI_CHANNEL = 0;
  static final int[] CONTROL_BANK_KNOB_CC = { 52, 53, 54, 55, 57, 58, 59, 60 };
  static final int[] CONTROL_BANK_FADER_CC = { 61, 62, 63, 70, 71, 72, 73, 74 };
  static final int[] CONTROL_BANK_SOLO_CC = { 75, 76, 77, 78, 79, 80, 81, 82 };

  static final int CONTROL_BANK_SOLO_PRESSED_VAL = 127;

  public HWControlBankB(HardwareSurface surface) {
    super(surface, CONTROL_BANK_ID);
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    // TODO Auto-generated method stub
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      S[i].pressedAction().setActionMatcher(
        midiIn.createCCActionMatcher(CONTROL_BANK_MIDI_CHANNEL,
          CONTROL_BANK_SOLO_CC[i], CONTROL_BANK_SOLO_PRESSED_VAL));

      F[i].setAdjustValueMatcher(midiIn.createAbsoluteCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_FADER_CC[i]));

      K[i].setAdjustValueMatcher(midiIn.createRelative2sComplementCCValueMatcher(
        CONTROL_BANK_MIDI_CHANNEL, CONTROL_BANK_KNOB_CC[i], KNOB_ROTATION_VAL));
    }
  }

  @Override
  public void bindCCActions() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiIns) {
    // TODO Auto-generated method stub
    
  }



}
