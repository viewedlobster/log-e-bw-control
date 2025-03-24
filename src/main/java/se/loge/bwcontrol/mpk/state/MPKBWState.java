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

package se.loge.bwcontrol.mpk.state;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.SettableBooleanValue;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.MPKConst;
import se.loge.bwcontrol.mpk.state.MPKCState.ControlPager;
import se.loge.bwcontrol.mpk.state.MPKCState.PagerEvt;

public class MPKBWState implements HasBWHost {

  private CursorRemoteControlsPage controlBankARemoteKnobs;
  private CursorRemoteControlsPage controlBankARemoteFaders;

  private CStateField<ControlPager, PagerEvt>.CStateConn<ControlPager, PagerEvt> pager;

  MPKBWState() {
    controlBankARemoteKnobs = primaryInstrument().createCursorRemoteControlsPage(
      "MPK Bank A Knobs", MPKConst.MPK261_NUM_CONTROL_STRIPS, "mpk-bank-a-knobs");
    controlBankARemoteKnobs.selectedPageIndex().markInterested();
    controlBankARemoteKnobs.pageCount().markInterested();

    controlBankARemoteFaders = primaryInstrument().createCursorRemoteControlsPage(
      "MPK Bank A Faders", MPKConst.MPK261_NUM_CONTROL_STRIPS, "mpk-bank-a-faders");
    controlBankARemoteFaders.selectedPageIndex().markInterested();
    controlBankARemoteFaders.pageCount().markInterested();
  }

  public void bindInstrumentKnobRemotes(RelativeHardwareKnob[] knobs) {
    RemoteControl r;
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++) {
      /* K1-K8 are bound to remote controls page */
      r = controlBankARemoteKnobs.getParameter(i);
      r.setIndication(true);
      r.addBinding(knobs[i]);
    }
  }

  public void bindInstrumentFaderRemotes(HardwareSlider[] faders) {
    RemoteControl r;
    for (int i = 0; i < MPKConst.MPK261_NUM_CONTROL_STRIPS; i++) {
      /* F1-F8 are bound to remote controls page */
      r = controlBankARemoteFaders.getParameter(i);
      r.setIndication(true);
      r.addBinding(faders[i]);
    }
  }

  public SettableBooleanValue clipOverdub() {
    return transport().isClipLauncherOverdubEnabled();
  }

  public SettableBooleanValue arrangerOverdub() {
    return transport().isArrangerOverdubEnabled();
  }

  public SettableBooleanValue metronome() {
    return transport().isMetronomeEnabled();
  }

  void connectMPKState(MPKCState s) {
    // connect to pager
    pager = s.instrumentPager.connect((p) -> onPagerUpdate(p));

    // update pager page count when necessary
    controlBankARemoteKnobs.pageCount().addValueObserver((v) -> {
      pager.send(PagerEvt.bwPageCount(v, controlBankARemoteFaders.pageCount().get()));
    });
    controlBankARemoteFaders.pageCount().addValueObserver((v) -> {
      pager.send(PagerEvt.bwPageCount(controlBankARemoteKnobs.pageCount().get(), v));
    });
  }

  private void onPagerUpdate(ControlPager pagerUpd) {
    boolean overIndexed = false;

    if (pagerUpd.knobPage < 0) {
      // TODO does this fuck things up?
      controlBankARemoteKnobs.selectedPageIndex().set(-1);
    } else if (pagerUpd.knobPage <
        controlBankARemoteKnobs.pageCount().get() ) {
      controlBankARemoteKnobs.selectedPageIndex().set(pagerUpd.knobPage);
    } else {
      overIndexed = true;
    }

    if (pagerUpd.faderPage < 0) {
      // TODO does this fuck things up?
      controlBankARemoteFaders.selectedPageIndex().set(-1);
    } else if (pagerUpd.faderPage <
        controlBankARemoteFaders.pageCount().get()) {
      controlBankARemoteFaders.selectedPageIndex().set(pagerUpd.faderPage);
    } else {
      overIndexed = true;
    }

    if (overIndexed) {
      pager.send(PagerEvt.bwPageCount(
        controlBankARemoteKnobs.pageCount().get(), controlBankARemoteFaders.pageCount().get()));
    }

  }
}
