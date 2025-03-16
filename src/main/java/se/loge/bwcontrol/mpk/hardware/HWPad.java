package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.InternalHardwareLightState;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.MultiStateHardwareLight;

import se.loge.bwcontrol.mpk.MPKConstants;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;
import se.loge.bwcontrol.mpk.hardware.ifc.HWISignalsHardwareUpdate;

public class HWPad implements HWIHasHost, HWISignalsHardwareUpdate, HWIMidiIn,  HWIMidiOut {
  final static int MPK_PADS_MIDI_CHANNEL = 0;

  final int padIdx;
  final HardwareButton pad;
  final MultiStateHardwareLight light;
  final HardwareSurface surface;
  PadColor color;
  PadColor pressedColor;
  MidiOut midiRemoteOut;
  int note;


  public HWPad(HardwareSurface surface, int padIdx, String bankId, int note) {
    this.surface = surface;
    this.padIdx = padIdx;
    this.pad = surface.createHardwareButton(String.format("pad_%s_%d", bankId, padIdx));
    this.light = surface.createMultiStateHardwareLight(String.format("pad_led_%s_%d", bankId, padIdx));
    this.color = PadColor.GREY;
    this.pressedColor = PadColor.RED;
    this.note = note;

    light.setColorToStateFunction((c) -> {
      pressedColor = PadColor.match(c);
      return new PadLightState(color, pressedColor);
    });
  }

  //private int getIdentOff() {
  //  return MPK_PAD_LIGHT_OFF_COLOR_MIN + padIdx;
  //}

  public byte pressedColorByte() {
    if (pressedColor == null) {
      return PadColor.OFF.v();
    }

    return pressedColor.v();
  }

  public byte colorByte() {
    if (color == null) {
      return PadColor.OFF.v();
    }

    return color.v();
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
    light.onUpdateHardware(() -> {
      signalHardwareUpdate(MPKConstants.UPDATE_TYPE_PAD_COLOR_ALL);
      signalHardwareUpdate(MPKConstants.UPDATE_TYPE_PAD_PRESSED_COLOR_ALL);
    });
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

  private class PadLightState extends InternalHardwareLightState {

    private PadColor color;
    private PadColor pressedColor;

    PadLightState(PadColor initOn, PadColor initOff) {
      color = initOn;
      pressedColor = initOff;
    }

    @Override
    public HardwareLightVisualState getVisualState() {
      return HardwareLightVisualState.createForColor(color.bColor());
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof PadLightState) {
        PadLightState st = (PadLightState)o;
        return st.color == this.color && st.pressedColor == this.pressedColor;
      }

      return false;
    }
  }
}
