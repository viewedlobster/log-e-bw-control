package se.loge.bwcontrol.mpk.hardware.pad;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.DrumPad;
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareActionBinding;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.MultiStateHardwareLight;

import se.loge.bwcontrol.common.SysexBuilder;
import se.loge.bwcontrol.mpk.MPKConst;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads.PadMode;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasOutputState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;

public class HWPad implements HWIHasHost, HWIHasOutputState, HWIMidiIn,  HWIMidiOut, HWPads.UsingPadMode {
  final static int MPK_PADS_MIDI_CHANNEL = 0;

  final int padIdx;
  final HardwareButton pad;
  final MultiStateHardwareLight light;
  final HardwareSurface surface;
  PadColor color;
  PadColor pressedColor;
  MidiOut midiRemoteOut;
  int note;
  HardwareActionBinding binding;
  DrumPad drumPad;
  private ClipLauncherSlot primaryClip;
  private HardwareActionBindable recAction;

  static final PadColor REC_OCCUPIED_COLOR = PadColor.RED;
  static final PadColor REC_EMPTY_COLOR = PadColor.GREY;
  static final PadColor REC_NOPE_COLOR = PadColor.OFF;

  public HWPad(HardwareSurface surface, int padIdx, String bankId,
               int note, DrumPad pad, ClipLauncherSlot primaryClip) {
    this.surface = surface;
    this.padIdx = padIdx;
    this.pad = surface.createHardwareButton(
      String.format("pad_%s_%d", bankId, padIdx));
    this.light = surface.createMultiStateHardwareLight(
      String.format("pad_led_%s_%d", bankId, padIdx));
    this.color = PadColor.RED;
    this.pressedColor = PadColor.GREY;
    this.note = note;
    this.drumPad = pad;
    this.primaryClip = primaryClip;

    // recAction depends on primary clip slot existing
    if (this.primaryClip != null) {
      this.recAction = customAction(
        () -> {
          assert(getPadMode() == PadMode.MPK_PAD_CLIP_START_RECORD);
          if (primaryClip.exists().get()) {
            primaryClip.record();
            revertPadMode(PadMode.MPK_PAD_CLIP_START_RECORD);
          }
        }
      );
    } else {
      this.recAction = null;
    }
    // TODO can recAction be replaced by two actions?

    //light.setColorToStateFunction((c) -> {
    //  color = PadColor.match(c);
    //  return new PadLightState(color, pressedColor);
    //});

    primaryTrack().color().markInterested();
    setColor(primaryTrack().color().get(), false);

    drumPad.color().addValueObserver(
      (r, g, b) -> { updateColorFromDrumPad(); }
    );
    drumPad.exists().addValueObserver(
      (exsts) -> { updateColorFromDrumPad(); }
    );
    //drumPad.exists().addValueObserver(
    //  (exsts) -> {
    //    updateColors();
    //  }
    //);

    if (primaryClip != null) {
      primaryClip.exists().markInterested();
      primaryClip.hasContent().markInterested();
      primaryClip.exists().addValueObserver(
        (exsts) -> updateColorFromPrimaryClip()
      );
      primaryClip.hasContent().addValueObserver(
        (hasCont) -> updateColorFromPrimaryClip()
      );
    }
  }

  public void modeRemap(PadMode ol, PadMode nw) {
    if (ol != nw) {

      switch (nw) {
      case MPK_PAD_NOTES_PLAY:
        setPressedBinding(null);
        break;
      case MPK_PAD_CLIP_START_RECORD:
        setPressedBinding(recAction);
        break;
      case MPK_PAD_CLIP_TRIGGER:
        break;
      case MPK_PAD_NOPE:
        break;
      default:
      }
    }
  }

  private void updateColorFromPrimaryClip() {
    switch (getPadMode()) {
    case MPK_PAD_CLIP_START_RECORD:
      if (! primaryClip.exists().get()) {
        setColor(PadColor.OFF);
        break;
      }
      setColor(primaryClip.hasContent().get() ? PadColor.RED : PadColor.GREY);
      break;
      default:
    }
  }

  private void updateColorFromDrumPad() {
    switch (getPadMode()) {
    case MPK_PAD_NOTES_PLAY:
      if (! drumPad.exists().get()) {
        setColor(primaryTrack().color().get());
        break;
      }
      setColor(drumPad.color().get());
      default:
    }
  }

  // color setters
  // this is the basis of all setters, handling update signaling as well
  public void setColor(PadColor c, boolean signal) {
    this.color = c;
    if (signal) {
      signalHardwareUpdate();
    }
  }

  // color setters: convienience methods
  public void setColor(Color c, boolean signal) {
    setColor(PadColor.match(c), signal);
  }

  public void setColor(PadColor c) {
    boolean needsUpdate = c != this.color;
    setColor(c, needsUpdate);
  }

  public void setColor(Color c) {
    setColor(PadColor.match(c));
  }

  public void setColor(double r, double g, double b) {
    setColor(Color.fromRGB(r, g, b));
  }

  // pressed color setters
  // TODO make these actually update hardware
  public void setPressedColor(PadColor c) {
    this.pressedColor = c;
  }

  public void setPressedColor(Color c) {
    setPressedColor(PadColor.match(c));
  }

  public void setPressedColor(double r, double g, double b) {
    setPressedColor(Color.fromRGB(r, g, b));
  }

  public byte colorByte() {
    if (color == null) {
      return PadColor.OFF.v();
    }

    return color.v();
  }

  public byte pressedColorByte() {
    if (pressedColor == null) {
      return PadColor.OFF.v();
    }

    return pressedColor.v();
  }

  public void ledPlayMode(boolean signal) {
    setColor(primaryTrack().color().get(), signal);
  }

  public void ledPlayModeDrumPad(boolean signal) {
    if (drumPad.exists().get()) {
      setColor(drumPad.color().get(), signal);
    } else {
      setColor(PadColor.OFF, signal);
    }
  }

  public void ledClipRecordMode(boolean signal) {
    if (primaryClip != null && primaryClip.exists().get()) {
      setColor(
        primaryClip.hasContent().get() ? REC_OCCUPIED_COLOR : REC_EMPTY_COLOR,
        signal
      );
    } else {
      setColor(REC_NOPE_COLOR, signal);
    }
  }

  // for now just sends regular color and not pressed color
  @Override
  public void onHardwareUpdate() {
    SysexBuilder sysex = new SysexBuilder();
    int ident = MPKConst.MPK_PAD_LIGHT_COLOR_MIN + padIdx;

    sysex.add("F0 47 00");
    sysex.add(MPKConst.MPK261_PRODUCT_ID);
    sysex.add("31 00 04 01");
    sysex.add(SysexBuilder.msb7(ident));
    sysex.add(SysexBuilder.lsb7(ident));
    sysex.add(colorByte());
    sysex.add("F7");

    midiRemoteOut.sendSysex(sysex.build());
  }

  void setPressedBinding(HardwareActionBindable act) {
    if (binding != null) {
      binding.removeBinding();
    }
    if (act != null) {
      binding = act.addBinding(pad.pressedAction());
    } else {
      binding = null;
    }
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    pad.pressedAction().setActionMatcher(
      midiIn.createNoteOnActionMatcher(MPK_PADS_MIDI_CHANNEL,
        note));
    pad.releasedAction().setActionMatcher(
      midiIn.createNoteOffActionMatcher(MPK_PADS_MIDI_CHANNEL,
        note));
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    midiRemoteOut = midiOuts[0];
  //  light.onUpdateHardware(() -> {
  //    signalHardwareUpdate(MPKConstants.UPDATE_TYPE_PAD_COLOR_ALL);
  //    signalHardwareUpdate(MPKConstants.UPDATE_TYPE_PAD_PRESSED_COLOR_ALL);
  //  });
  }

  /* Helper classes */
  public enum PadColor {
    OFF(0x00, 0, 0, 0),
    RED(0x01, 255, 0, 0),
    ORANGE(0x02, 255, 165, 0), // TODO g 190?
    AMBER(0x03, 255, 192, 0),
    YELLOW(0x04, 255, 255, 0),
    GREEN(0x05, 0, 255, 0),
    GREEN_BLUE(0x06,64,224,208),
    AQUA(0x07, 0, 255, 255),
    LIGHT_BLUE(0x08, 0x57, 0xB9, 0xFF),
    BLUE(0x09, 0, 0, 255),
    PURPLE(0x0A, 157, 0, 255),
    PINK(0x0B, 223, 115, 255),
    HOT_PINK(0x0C, 255, 70, 162),
    PASTEL_PURPLE(0x0D, 179, 158, 181),
    PASTEL_GREEN(0x0E, 173, 235, 179),
    PASTEL_PINK(0x0F, 255, 197, 211),
    GREY(0x10, 128, 128, 128);

    private int v;
    private double rn, gn, bn;

    private PadColor(int v, int r, int g, int b) {
      this.v = v;

      double norm = Math.sqrt(r*r + g*g + b*b);
      rn = r/norm;
      gn = g/norm;
      bn = b/norm;
    }

    public byte v() {
      return (byte)v;
    }

    static PadColor match(Color c) {
      PadColor pcbest = OFF;
      double projbest = 0;

      // TODO check if we need the special cases
      // handling special cases
      if (c.equals(Color.nullColor())) {
        return OFF;
      } else if (c.equals(Color.blackColor())) {
        return OFF;
      } else if (c.equals(Color.whiteColor())) {
        return GREY;
      }

      for (PadColor pc : PadColor.values()) {
        double proj = pc.rn * c.getRed() + pc.gn * c.getGreen() + pc.bn * c.getBlue();
        if (proj > projbest) {
          projbest = proj;
          pcbest = pc;
        }
      }

      return pcbest;
    }

    Color bColor() {
      return Color.fromRGB(rn, gn, bn);
    }
  }

  // TODO remove?
  //private class PadLightState extends InternalHardwareLightState {

  //  private PadColor color;
  //  private PadColor pressedColor;

  //  PadLightState(PadColor initOn, PadColor initOff) {
  //    color = initOn;
  //    pressedColor = initOff;
  //  }

  //  @Override
  //  public HardwareLightVisualState getVisualState() {
  //    return HardwareLightVisualState.createForColor(color.bColor());
  //  }

  //  @Override
  //  public boolean equals(Object o) {
  //    if (o instanceof PadLightState) {
  //      PadLightState st = (PadLightState)o;
  //      return st.color == this.color && st.pressedColor == this.pressedColor;
  //    }

  //    return false;
  //  }
  //}
}
