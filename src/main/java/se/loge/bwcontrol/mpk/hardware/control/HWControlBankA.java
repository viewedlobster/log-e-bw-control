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

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.MPKConst;
import se.loge.bwcontrol.mpk.hardware.button.HWCCToggleButton.ButtonState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMPKStateAccess;
import se.loge.bwcontrol.mpk.state.MPKCState;
import se.loge.bwcontrol.mpk.state.MPKCState.ControlPager;
import se.loge.bwcontrol.mpk.state.MPKCState.PagerEvt;

public class HWControlBankA extends HWControlBank implements HasBWHost, HWIMPKStateAccess {
  static final String CONTROL_BANK_ID = "A";

  static final int[] CONTROL_BANK_KNOB_CC = { 3, 9, 14, 15, 16, 17, 20, 19 };
  static final int[] CONTROL_BANK_FADER_CC = { 18, 21, 22, 23, 24, 25, 26, 27, };
  static final int[] CONTROL_BANK_SOLO_CC = { 28, 29, 30, 31, 35, 41, 46, 47 };

  static final int CONTROL_BANK_CC_STATUS_BYTE = 0xb0 + CONTROL_BANK_MIDI_CHANNEL;

  private CStateField<ControlPager, PagerEvt>.CStateConn<ControlPager, PagerEvt> pager;

  public HWControlBankA() {
    super(CONTROL_BANK_ID, CONTROL_BANK_KNOB_CC, CONTROL_BANK_FADER_CC, CONTROL_BANK_SOLO_CC);

    this.pager = state().instrumentPager().connect((pager) -> onPagerStateUpdate(pager));
  }

  private void onPagerStateUpdate(MPKCState.ControlPager pager) {
    switch (pager.activePager) {
      case KNOB:
        S(MPKConst.MPK261_NUM_CONTROL_STRIPS).setState(ButtonState.RELEASED); // S8

        for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS - 1; i++) {
          if (pager.knobPage == i) {
            S(i + 1).setState(ButtonState.PRESSED);
          } else {
            S(i + 1).setState(ButtonState.RELEASED);
          }
        }
        break;
      case FADER:
        S(MPKConst.MPK261_NUM_CONTROL_STRIPS).setState(ButtonState.PRESSED); // S8

        for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS - 1; i++) {
          if (pager.faderPage == i) {
            S(i + 1).setState(ButtonState.PRESSED);
          } else {
            S(i + 1).setState(ButtonState.RELEASED);
          }
        }
        break;
    }
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

  // TODO remove this comment...
  /*
   * This looks like a mess so here is a rundown:
   * 
   * controlsK/F are cursors into remote control pages tagged as such.
   * We first bind knobs K[0-7] and faders F[0-7] to these cursor pages
   * 
   * S[0-6] are then bound to select what page is active, with S[7]
   * bound to select what selection is active
   * -- knobs: S[7] == released
   * -- faders: S[7] == pressed
   * 
   * S[0-7] are all in toggle mode, which means we have to add a couple of
   * additional callback actions just to keep the lights in sync with the
   * controller extension state (which is why there is so many calls to
   * syncLight).
   */
  @Override
  public void bindMidiIn() {

    state().bitwig().bindInstrumentFaderRemotes(F);
    state().bitwig().bindInstrumentKnobRemotes(K);

    // page select (0-6) for S1-S7
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS - 1; i++) {
      final int icpy = i;
      S[i].setPressedAction(customAction(() -> {
        pager.send(PagerEvt.selectPage(icpy));
      }));
      S[i].setReleasedAction(customAction(() -> { syncButtonState(); }));
    }

    // pager toggle (fader/knob) for S8 (pressed/released)
    S(MPKConst.MPK261_NUM_CONTROL_STRIPS).setPressedAction(customAction(() -> {
      pager.send(PagerEvt.switchPager(PagerEvt.FADER_PAGER));
    }));
    S(MPKConst.MPK261_NUM_CONTROL_STRIPS).setReleasedAction(customAction(() -> {
      pager.send(PagerEvt.switchPager(PagerEvt.KNOB_PAGER));
    }));
  }

  private void syncButtonState() {
    onPagerStateUpdate(pager.get());
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++) {
      S[i].connectMidiOut(midiOut, midiOuts);
    }
  }



}
