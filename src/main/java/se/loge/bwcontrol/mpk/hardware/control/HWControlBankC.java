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

import se.loge.bwcontrol.common.CState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasOutputState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMPKStateAccess;
import se.loge.bwcontrol.mpk.state.MPKState.PadEvt;
import se.loge.bwcontrol.mpk.state.MPKState.PadMode;

public class HWControlBankC extends HWControlBank implements HWIHasHost, HWIMPKStateAccess, HWIHasOutputState {
  static final String CONTROL_BANK_ID = "C";

  static final int CONTROL_BANK_MIDI_CHANNEL = 0;
  static final int[] CONTROL_BANK_KNOB_CC = { 83, 85, 86, 87, 88, 89, 90, 91 };
  static final int[] CONTROL_BANK_FADER_CC = { 92, 93, 94, 95, 102, 103, 104, 105};
  static final int[] CONTROL_BANK_SOLO_CC = { 106, 107, 108, 109, 110, 111, 112, 113 };

  static final int CONTROL_BANK_CC_STATUS_BYTE = 0xb0 + CONTROL_BANK_MIDI_CHANNEL;

  static final int LIGHT_ON = 127;
  static final int LIGHT_OFF = 0;

  private static final int REC_MODE_LIGHT = 7;

  private final HardwareActionBindable recModeEnable;
  private final HardwareActionBindable recModeDisable;

  private MidiOut midi0Out;
  private int[] lights;
  private CState<PadMode, PadEvt>.CStateConn<PadMode, PadEvt> padMode;

  public HWControlBankC(HardwareSurface surface) {
    super(surface, CONTROL_BANK_ID);

    lights = new int[MPK261_NUM_CONTROL_STRIPS];
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      lights[i] = LIGHT_OFF;
    }

    padMode = controllerState().padModeUser((mode) -> this.onPadModeUpdate(mode));

    recModeEnable = customAction(() -> {
      lights[REC_MODE_LIGHT] = LIGHT_ON;
      this.padMode.send(PadEvt.CLIP_REC_BUTTON_ON);
    });
    recModeDisable = customAction(() -> { 
      lights[REC_MODE_LIGHT] = LIGHT_OFF;
      this.padMode.send(PadEvt.CLIP_REC_BUTTON_OFF);
    });
  }

  private void onPadModeUpdate(PadMode mode) {
    recModeLightUpdate(mode.rec());
  }

  private void recModeLightUpdate(boolean recMode) {
    if ( recMode && lights[REC_MODE_LIGHT] != LIGHT_ON ) {
      lights[REC_MODE_LIGHT] = LIGHT_ON;
      signalHardwareUpdate();
    } else if ( ! recMode && lights[REC_MODE_LIGHT] != LIGHT_OFF ) {
      lights[REC_MODE_LIGHT] = LIGHT_OFF;
      signalHardwareUpdate();
    }
  }

  @Override
  public void onHardwareUpdate() {
    for (int light = 0; light < MPK261_NUM_CONTROL_STRIPS; light++)
      syncLight(REC_MODE_LIGHT);
  }

  private void syncLight(int pageIdx) {
    if (midi0Out == null)
      return;

    midi0Out.sendMidi(CONTROL_BANK_CC_STATUS_BYTE, CONTROL_BANK_SOLO_CC[pageIdx], lights[pageIdx]);
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
  public void bindMidi() {

    // rec clip pad mode
    S[REC_MODE_LIGHT].pressedAction().addBinding(recModeEnable);
    S[REC_MODE_LIGHT].releasedAction().addBinding(recModeDisable);
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiIns) {
    midi0Out = midiOut;
    signalHardwareUpdate();
  }

}
