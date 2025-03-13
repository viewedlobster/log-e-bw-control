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

import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.RemoteControl;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIUsingCallbacks;

public class HWControlBankA extends HWControlBank implements HWIHasHost, HWIUsingCallbacks {
  static final String CONTROL_BANK_ID = "A";

  static final int CONTROL_BANK_MIDI_CHANNEL = 0;
  static final int[] CONTROL_BANK_KNOB_CC = { 3, 9, 14, 15, 16, 17, 20, 19 };
  static final int[] CONTROL_BANK_FADER_CC = { 18, 21, 22, 23, 24, 25, 26, 27, };
  static final int[] CONTROL_BANK_SOLO_CC = { 28, 29, 30, 31, 35, 41, 46, 47 };

  static final int CONTROL_BANK_SOLO_PRESSED_VAL = 127;
  static final int CONTROL_BANK_SOLO_RELEASED_VAL = 0;

  static final int CONTROL_BANK_CC_STATUS_BYTE = 0xb0 + CONTROL_BANK_MIDI_CHANNEL;

  static final int LIGHT_ON = 127;
  static final int LIGHT_OFF = 0;

  private MidiOut midi0Out;

  private CursorRemoteControlsPage controlsF;
  private CursorRemoteControlsPage controlsK;

  private CursorRemoteControlsPage activeCursor;

  private int[] lights;

  public HWControlBankA(HardwareSurface surface) {
    super(surface, CONTROL_BANK_ID);

    controlsK = primaryInstrument().createCursorRemoteControlsPage(
      "MPK Bank A Knobs", MPK261_NUM_CONTROL_STRIPS, "mpk-bank-a-knobs");
    controlsF = primaryInstrument().createCursorRemoteControlsPage(
      "MPK Bank A Faders", MPK261_NUM_CONTROL_STRIPS, "mpk-bank-a-faders");

    controlsK.selectedPageIndex().markInterested();
    controlsK.pageCount().markInterested();
    controlsF.selectedPageIndex().markInterested();
    controlsF.pageCount().markInterested();

    activeCursor = controlsK;
    lights = new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 };

    controlsK.selectedPageIndex().addValueObserver(val -> { if (isActive(controlsK)) updateLights(val, false); });
    controlsF.selectedPageIndex().addValueObserver(val -> { if (isActive(controlsF)) updateLights(val, true); });

  }

  boolean isActive(CursorRemoteControlsPage c) {
    return c == activeCursor;
  }

  private void updateLights(int pageIdx, boolean apLight) {

    /* reset lights */
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      lights[i] = LIGHT_OFF;
    }

    /* active cursor light */
    if (apLight) {
      lights[MPK261_NUM_CONTROL_STRIPS - 1] = LIGHT_ON;
    }

    /* page light */
    if (pageIdx >= 0 && pageIdx < MPK261_NUM_CONTROL_STRIPS) {

      assert(pageIdx < MPK261_NUM_CONTROL_STRIPS - 1);

      lights[pageIdx] = LIGHT_ON;
    }

    /* dont send data if midi output is not initialized yet */
    if (midi0Out == null)
      return;
    
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      midi0Out.sendMidi(CONTROL_BANK_CC_STATUS_BYTE, CONTROL_BANK_SOLO_CC[i], lights[i]);
    }
  }

  private void syncLight(int pageIdx) {
    if (midi0Out == null)
      return;

    midi0Out.sendMidi(CONTROL_BANK_CC_STATUS_BYTE, CONTROL_BANK_SOLO_CC[pageIdx], lights[pageIdx]);
  }

  @SuppressWarnings("unused")
  private void printLights() {
    println(String.format("bank-a-lights: { %d, %d, %d, %d, %d, %d, %d, %d }",
      lights[0], lights[1], lights[2], lights[3], lights[4], lights[5], lights[6], lights[7]));
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
    RemoteControl r;
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      /* K1-K8 and F1-F8 are bound to remote controls page */
      r = controlsK.getParameter(i); r.setIndication(true); r.addBinding(K[i]);
      r = controlsF.getParameter(i); r.setIndication(true); r.addBinding(F[i]);
    }

    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS - 1; i++) {
      final int icopy = i;
      S[i].pressedAction().addBinding(customAction(
        () -> {
          if (icopy < activeCursor.pageCount().get()) {
            activeCursor.selectedPageIndex().set(icopy);
          } else {
            syncLight(icopy);
          }
        }
      ));
    }

    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS - 1; i++) {
      final int icopy = i;
      S[i].releasedAction().addBinding(customAction(
        () -> {
          syncLight(icopy);
        }
      ));
    }

    S[MPK261_NUM_CONTROL_STRIPS-1].pressedAction().addBinding(customAction(
      () -> {
        if (activeCursor == controlsF) {
          println("OOPS -- cb:A:S8 pressed: state mismatch between controller and host");
        }
        activeCursor = controlsF;
      }
    ));

    S[MPK261_NUM_CONTROL_STRIPS-1].releasedAction().addBinding(customAction(
      () -> {
        if (activeCursor == controlsF) {
          println("OOPS -- cb:A:S8 released: state mismatch between controller and host");
        }
        activeCursor = controlsK;
      }
    ));

    // registerMidiCallback(midi0In, (msg) -> onMidi0Action(msg));
    /* S1-S4 are bound to knob and fader remote controls page navigation */ 
    /*S[0].pressedAction().addBinding(controlsK.selectPreviousAction());
    S[1].pressedAction().addBinding(controlsK.selectNextAction());
    S[2].pressedAction().addBinding(controlsF.selectPreviousAction());
    S[3].pressedAction().addBinding(controlsF.selectNextAction());
    */
    /* S5-S8 remain unbound */
    /* Ideas for use
     * - setting pad mode: 
     *   * play instrument
     *   * play clips current track,
     *   * rec clips current track, 
     *   * global queues/actions (i.e. bound manually)
     */
    // S[4].pressedAction().addBinding(null);
    // S[5].pressedAction().addBinding(null);
    // S[6].pressedAction().addBinding(null);
    // S[7].pressedAction().addBinding(null);
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiIns) {
    midi0Out = midiOut;
    updateLights(activeCursor.selectedPageIndex().get(), isActive(controlsF));
  }



}
