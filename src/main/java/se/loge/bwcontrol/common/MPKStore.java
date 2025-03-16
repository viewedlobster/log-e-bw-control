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

package se.loge.bwcontrol.common;

import java.util.function.Function;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.CallbackRegistry.MatchingCallback;
import se.loge.bwcontrol.mpk.MPKConstants;

public class MPKStore extends ExtensionStore {
  private ControllerHost host;
  private Transport transport;
  private CursorTrack primaryTrackCursor;
  private CursorDevice primaryDeviceCursor;
  private CursorDevice primaryInstrumentCursor;
  private MidiIn midi0;
  private CallbackRegistry<ShortMidiMessage> midi0Callback;
  private CallbackRegistry<String> sysex0Callback;
  private boolean padColorUp, padPressedColorUp;
  private Runnable padColorUpCallback, padPressedColorUpCallback;

  static final String MPK_PRIMARY_CURSOR_NAME = "Primary";
  static final String MPK_PRIMARY_INSTRUMENT_NAME = "Primary Instrument";

  private MPKStore(ControllerHost h) {
    midi0Callback = new CallbackRegistry<>();
    sysex0Callback = new CallbackRegistry<>();
    host = h;
    padColorUp = padPressedColorUp = false;
  }

  public static ExtensionStore initStore(ControllerHost host)
  {
    assert(store == null);
    store = new MPKStore(host);
    return store;
  }

  @Override
  public CallbackPair<CallbackRegistry<ShortMidiMessage>, CallbackRegistry<String>> registerMidiIn(MidiIn midiIn) {
    assert(midi0 == null);
    midi0 = midiIn;
    return new CallbackPair<>(midi0Callback, sysex0Callback);
  }

  @Override
  public void registerMidiCallback(MidiIn midiIn, MatchingCallback<ShortMidiMessage> cb) {
    assert(midi0 != null && midiIn == midi0);
    midi0Callback.register(cb);
  }

  @Override
  public void registerSysexCallback(MidiIn midiIn, MatchingCallback<String> cb) {
    assert(midi0 != null && midiIn == midi0);
    sysex0Callback.register(cb);
  }

  @Override
  public ControllerHost getHost() {
    return host;
  }

  @Override
  public Transport getTransport() {
    if (transport == null) {
      transport = host.createTransport();
    }
    return transport;
  }

  @Override
  public CursorTrack getPrimaryTrackCursor() {
    if (primaryTrackCursor == null) {
      primaryTrackCursor = host.createCursorTrack("mpk_primary_track", MPK_PRIMARY_CURSOR_NAME, 0, 0, true);
    }
    return primaryTrackCursor;
  }

  @Override
  public CursorDevice getPrimaryDeviceCursor() {
    if (primaryDeviceCursor == null) {
       primaryDeviceCursor = getPrimaryTrackCursor().createCursorDevice("mpk_primary_device", MPK_PRIMARY_CURSOR_NAME, 0, CursorDeviceFollowMode.FOLLOW_SELECTION);
    }
    return primaryDeviceCursor;
  }

  @Override
  public CursorDevice getPrimaryInstrumentCursor() {
    if (primaryInstrumentCursor == null) {
      primaryInstrumentCursor = getPrimaryTrackCursor().createCursorDevice(
        "mpk_primary_instrument", MPK_PRIMARY_INSTRUMENT_NAME, 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
    }
    return primaryInstrumentCursor;
  }

  @Override
  public void signalHardwareUpdate(int type) {
    switch (type) {
    case MPKConstants.UPDATE_TYPE_PAD_COLOR_ALL:
      padColorUp = true;
      break;
    case MPKConstants.UPDATE_TYPE_PAD_PRESSED_COLOR_ALL:
      padPressedColorUp = true;
      break;
    default:
      getHost().println(String.format("signalUpdateHardware: Update type %d unrecognized", type));
      break;
    }
  }

  @Override
  public boolean shouldHardwareUpdate() {
    return (padColorUp || padPressedColorUp);
  }

  @Override
  public void registerHardwareUpdateCallback(int type, Runnable f) {
    switch (type) {
    case MPKConstants.UPDATE_TYPE_PAD_COLOR_ALL:
      if (padColorUpCallback != null) {
        getHost().println("warning: registerHardwareUpdateCallback: replacing pad update callback");
      }
      padColorUpCallback = f;
      break;
    case MPKConstants.UPDATE_TYPE_PAD_PRESSED_COLOR_ALL:
      if (padPressedColorUpCallback != null) {
        getHost().println("warning: registerHardwareUpdateCallback: replacing pad update callback");
      }
      padPressedColorUpCallback = f;
      break;
    default:
      throw new HWError(String.format("unrecognized hardware update type %d", type));
    }
  }

  @Override
  public void updateHardware() {
    if (padColorUp) {
      padColorUpCallback.run();
      padColorUp = false;
    }
    if (padPressedColorUp) {
      padPressedColorUpCallback.run();
      padPressedColorUp = false;
    }
  }
}