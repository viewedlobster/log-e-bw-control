package se.loge.bwcontrol.mpk.state;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.mpk.state.MPKCState.ControlPager;
import se.loge.bwcontrol.mpk.state.MPKCState.PadEvt;
import se.loge.bwcontrol.mpk.state.MPKCState.PadMode;
import se.loge.bwcontrol.mpk.state.MPKCState.PagerEvt;

public class MPKState {
  private final MPKCState cstate;
  private final MPKBWState bwstate;

  public MPKState() {
    cstate = new MPKCState();
    bwstate = new MPKBWState();

    bwstate.connectMPKState(cstate);
  }

  public void init() {
    cstate.init();
  }

  public CStateField<ControlPager, PagerEvt> instrumentPager() {
    return cstate.instrumentPager;
  }

  public CStateField<PadMode, PadEvt> padMode() {
    return cstate.padMode;
  }

  public MPKBWState bitwig() {
    return bwstate;
  }
}
