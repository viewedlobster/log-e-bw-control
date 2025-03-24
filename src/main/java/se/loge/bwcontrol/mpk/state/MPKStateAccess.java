package se.loge.bwcontrol.mpk.state;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.pad.HWPads;

public class MPKStateAccess implements HWIHasHost {

  private HWPads pads;

  public MPKStateAccess() {}

  public HWPads.PadMode getPadMode() {
    assert(pads != null);
    return pads.getMode();
  }

  public void setPadMode(HWPads.PadMode m) {
    assert(pads != null);
    pads.setMode(m);
  }

  public void revertPadMode(HWPads.PadMode m) {
    assert(pads != null);
    if (m == pads.getMode()) {
      pads.revertMode();
    } else {
      errorln(
        String.format("reverting pad mode %s when actual mode is %s",
          m.toString(), getPadMode().toString()));
    }
  }

  public void setHWPads(HWPads pads) {
    this.pads = pads;
  }


}
