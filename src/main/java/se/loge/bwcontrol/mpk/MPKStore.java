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

package se.loge.bwcontrol.mpk;

import java.util.LinkedList;
import java.util.Queue;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.CallbackPair;
import se.loge.bwcontrol.common.CallbackRegistry;
import se.loge.bwcontrol.common.ExtensionStore;
import se.loge.bwcontrol.common.CallbackRegistry.MatchingCallback;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasOutputState;
import se.loge.bwcontrol.mpk.state.MPKStateAccess;

public class MPKStore extends ExtensionStore {
  private ControllerHost host;
  private Transport transport;
  private CursorTrack primaryTrackCursor;
  private CursorDevice primaryDeviceCursor;
  private CursorDevice primaryInstrumentCursor;
  private MidiIn midi0;
  private CallbackRegistry<ShortMidiMessage> midi0Callback;
  private CallbackRegistry<String> sysex0Callback;
  private Queue<HWIHasOutputState> needsUpdate;
  private MPKStateAccess extra;

  static final String MPK_PRIMARY_CURSOR_NAME = "Primary";
  static final String MPK_PRIMARY_INSTRUMENT_NAME = "Primary Instrument";
  static final int MPK_PRIMARY_CURSOR_TRACK_NUM_SENDS = 0;
  static final int MPK_PRIMARY_CURSOR_TRACK_NUM_SCENES = 8;
  static final boolean MPK_PRIMARY_CURSOR_TRACK_FOLLOW = true;

  private MPKStore(ControllerHost h) {
    this.midi0Callback = new CallbackRegistry<>();
    this.sysex0Callback = new CallbackRegistry<>();
    this.host = h;
    this.needsUpdate = new LinkedList<>();
    this.extra = new MPKStateAccess();
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
      primaryTrackCursor = host.createCursorTrack(
        "mpk_primary_track", MPK_PRIMARY_CURSOR_NAME,
        MPK_PRIMARY_CURSOR_TRACK_NUM_SENDS,
        MPK_PRIMARY_CURSOR_TRACK_NUM_SCENES, 
        MPK_PRIMARY_CURSOR_TRACK_FOLLOW);
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
  public void signalHardwareUpdate(HWIHasOutputState elm) {
    needsUpdate.add(elm);
  }

  @Override
  public boolean shouldHardwareUpdate() {
    return (!needsUpdate.isEmpty());
  }

  @Override
  public void updateHardware() {
    while (!needsUpdate.isEmpty()) {
      needsUpdate.poll().onHardwareUpdate();
    }
  }

  @Override
  public Object extra() {
    return this.extra;
  }
}