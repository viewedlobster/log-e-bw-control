package se.loge.bwcontrol.mpk.hardware.button;

import java.util.function.Function;

import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareActionBinding;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.SettableBooleanValue;

import se.loge.bwcontrol.common.ifc.HasOutputState;
import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.ifc.CMidiIn;
import se.loge.bwcontrol.common.ifc.CMidiOut;
import se.loge.bwcontrol.common.ifc.HasBWHost;

public class HWCCToggleButton implements HasBWHost, CMidiIn, CMidiOut, HasOutputState {

  public enum ButtonState {
    PRESSED,
    RELEASED;
  }

  static final int CC_STATUS_BYTE_BASE = 0xb0;

  @SuppressWarnings("unused")
  private final String id;
  private final int pressedVal;
  private final int releasedVal;
  private final int cc;
  private final int ccStatusByte;
  private final int midiChan;

  private final HardwareButton hwButton;

  private ButtonState state;

  @SuppressWarnings("unused")
  private HardwareActionBinding statePressedBinding;
  @SuppressWarnings("unused")
  private HardwareActionBinding stateReleasedBinding;

  private HardwareActionBinding pressedBinding;
  private HardwareActionBinding releasedBinding;

  private MidiIn midi0In;
  private MidiOut midi0Out;


  public HWCCToggleButton(String id, int midiChan, int cc, int pressedVal, int releasedVal) {
    this.id = id;
    this.midiChan = midiChan;
    this.cc = cc;
    this.ccStatusByte = CC_STATUS_BYTE_BASE + midiChan;
    this.pressedVal = pressedVal;
    this.releasedVal = releasedVal;

    hwButton = surface().createHardwareButton(id);

    this.state = ButtonState.RELEASED;
  }

  public void setPressedAction(HardwareActionBindable act) {
    if ( pressedBinding != null ) {
      pressedBinding.removeBinding();;
    }
    pressedBinding = hwButton.pressedAction().addBinding(act);
  }

  public void setReleasedAction(HardwareActionBindable act) {
    if ( releasedBinding != null ) {
      releasedBinding.removeBinding();
    }
    releasedBinding = hwButton.releasedAction().addBinding(act);
  }

  // setting state without recieving pressed/released cc
  // does not trigger action
  public void setState(ButtonState s) {
    if ( ! s.equals(state) ) {
      state = s;
      signalHardwareUpdate();
    }
  }

  public void setState(boolean pressed) {
    setState(pressed ? ButtonState.PRESSED : ButtonState.RELEASED);
  }

  public <S, E> void bindTo(CStateField<S, E> state,
      Function<S, Boolean> buttonState, E onPress, E onRelease) {
    CStateField<S, E>.CStateConn<S, E> conn = state.connect(
      (s) -> { setState(buttonState.apply(s)); }
    );
    setPressedAction(conn.sendAction(onPress));
    setReleasedAction(conn.sendAction(onRelease));
  }

  public void bindTo(SettableBooleanValue b) {
    setPressedAction(b.setToTrueAction());
    setReleasedAction(b.setToFalseAction());
    b.addValueObserver((bv) -> { setState(bv); });
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    this.midi0In = midiIn;

    hwButton.pressedAction().setActionMatcher(midi0In.createCCActionMatcher(midiChan, cc, pressedVal));
    hwButton.releasedAction().setActionMatcher(midi0In.createCCActionMatcher(midiChan, cc, releasedVal));
  }

  @Override
  public void bindMidiIn() {
    statePressedBinding = hwButton.pressedAction()
      .addBinding(customAction(() -> { state = ButtonState.PRESSED; }));
    stateReleasedBinding = hwButton.releasedAction()
      .addBinding(customAction(() -> { state = ButtonState.RELEASED; }));
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiIns) {
    this.midi0Out = midiOut;

    signalHardwareUpdate();
  }

  @Override
  public void onHardwareUpdate() {
    switch (state) {
      case PRESSED:
        midi0Out.sendMidi(ccStatusByte, cc, pressedVal);
        break;
      case RELEASED:
        midi0Out.sendMidi(ccStatusByte, cc, releasedVal);
        break;
    }
  }

}
