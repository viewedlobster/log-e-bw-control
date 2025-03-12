package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
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

  static final int CONTROL_BANK_CC_STATUS_BYTE = 0xb0 + CONTROL_BANK_MIDI_CHANNEL;

  static final int LIGHT_ON = 127;
  static final int LIGHT_OFF = 0;

  private MidiIn midi0In;
  private MidiOut midi0Out;

  private CursorDevice device;
  private CursorRemoteControlsPage controlsF;
  private CursorRemoteControlsPage controlsK;

  private CursorRemoteControlsPage activeCursor;

  private int[] lights;

  public HWControlBankA(HardwareSurface surface) {
    super(surface, CONTROL_BANK_ID);

    device = primaryInstrument();
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
    if (activeCursor.pageCount().get() > 0) {
      activeCursor.selectedPageIndex().set(0);
    }

    controlsK.selectedPageIndex().addValueObserver(val -> updateLights());
    controlsK.pageCount().addValueObserver(val -> updateLights());
    controlsF.selectedPageIndex().addValueObserver(val -> updateLights());
    controlsF.pageCount().addValueObserver(val -> updateLights());

  }

  private void toggleActiveCursor() {
    if (activeCursor == controlsK) {
      activeCursor = controlsF;
    } else {
      activeCursor = controlsK;
    }
  }

  private void pickActiveCursorPage(int i) {
    assert(i >= 0);

    int pageCount = activeCursor.pageCount().get();
    if (pageCount <= i) {
      i = pageCount - 1;
    }

    if (i < 0) {
      return;
    }

    println(String.format("Setting page index: %d", i));
    /* Triggers a light update via callback */
    activeCursor.selectedPageIndex().set(i);
  }

  private void updateLights() {

    /* reset lights */
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      lights[i] = LIGHT_OFF;
    }

    /* active cursor light */
    if (activeCursor == controlsF) {
      lights[MPK261_NUM_CONTROL_STRIPS - 1] = LIGHT_ON;
    }

    /* page light */
    int pageIdx = activeCursor.selectedPageIndex().get();
    println(String.format("page-idx: %d", pageIdx));
    if (pageIdx >= 0) {

      assert(pageIdx < MPK261_NUM_CONTROL_STRIPS - 1);

      lights[pageIdx] = LIGHT_ON;
    }

    println(String.format("bank-a-lights: { %d, %d, %d, %d, %d, %d, %d, %d }",
      lights[0], lights[1], lights[2], lights[3], lights[4], lights[5], lights[6], lights[7]));
    /* dont send data if midi output is not initialized yet */
    if (midi0Out == null)
      return;
    
    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      midi0Out.sendMidi(CONTROL_BANK_CC_STATUS_BYTE, CONTROL_BANK_SOLO_CC[i], lights[i]);
    }
  }

  private boolean onMidi0Action(ShortMidiMessage msg) {
    int i;

    if (msg.getStatusByte() != CONTROL_BANK_CC_STATUS_BYTE) {
      return false;
    }

    if (msg.getChannel() != CONTROL_BANK_MIDI_CHANNEL) {
      return false;
    }

    for (i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      if (msg.getData1() == CONTROL_BANK_SOLO_CC[i])
        break;
    }

    if (i > MPK261_NUM_CONTROL_STRIPS) return false;

    println(String.format("got solo button press: %d", i));
    if (msg.getData2() == CONTROL_BANK_SOLO_PRESSED_VAL) {
      switch (i) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
          pickActiveCursorPage(i);
          return true;
        case 7:
          toggleActiveCursor();
          return true;
        default:
          return false;
      }
    } else {
      updateLights();
      return true;
    }
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    midi0In = midiIn;

    for (int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++) {
      //S[i].pressedAction().setActionMatcher(
      //  midiIn.createCCActionMatcher(CONTROL_BANK_MIDI_CHANNEL,
      //    CONTROL_BANK_SOLO_CC[i], CONTROL_BANK_SOLO_PRESSED_VAL));

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

    registerMidiCallback(midi0In, (msg) -> onMidi0Action(msg));
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
    updateLights();
  }



}
