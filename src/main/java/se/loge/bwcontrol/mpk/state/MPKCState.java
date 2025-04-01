package se.loge.bwcontrol.mpk.state;

import se.loge.bwcontrol.common.BWHost;
import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.HWError;
import se.loge.bwcontrol.common.ifc.HasBWHost;

public class MPKCState implements HasBWHost {

  /* Pad mode event */
  public final CStateField<PadMode, PadEvt> padMode;
  public static class PadMode {

    public final PlayMode play;
    public final RecMode rec;

    public enum PlayMode {
      NOTE_PLAY,
      CLIP_PLAY;
    }
    public enum RecMode {
      RECMODE_ON(true),
      RECMODE_OFF(false);

      private final boolean b;

      private RecMode(boolean b) {
        this.b = b;
      }

      public boolean b() {
        return b;
      }
    }

    private PadMode(PlayMode b, RecMode r) {
      this.play = b;
      this.rec = r;
    }

    @Override
    public String toString() {
      return String.format("PadMode(%s, %s)", this.play, this.rec);
    }

    public static final PadMode NOTE_PLAY = new PadMode(PlayMode.NOTE_PLAY, RecMode.RECMODE_OFF);
    public static final PadMode NOTE_PLAY_REC = new PadMode(PlayMode.NOTE_PLAY, RecMode.RECMODE_ON);
    public static final PadMode CLIP_PLAY = new PadMode(PlayMode.CLIP_PLAY, RecMode.RECMODE_OFF);
    public static final PadMode CLIP_PLAY_REC = new PadMode(PlayMode.CLIP_PLAY, RecMode.RECMODE_ON);

    public PadMode recOn() {
      return new PadMode(this.play, RecMode.RECMODE_ON);
    }
    public PadMode recOff() {
      return new PadMode(this.play, RecMode.RECMODE_OFF);
    }

    public boolean rec() {
      return this.rec.b();
    }
    
    @Override
    public boolean equals(Object o) {
      if ( ! (o instanceof PadMode) ) {
        return false;
      } else {
        PadMode om = (PadMode)o;
        return this.play == om.play && this.rec == om.rec;
      }
    }
  }
  public enum PadEvt {
    INIT,
    CLIP_REC_BUTTON_ON,
    CLIP_REC_BUTTON_OFF,
    CLIP_REC_PAD_PRESSED;
  }
  private PadMode padModeTrans(PadMode m, PadEvt e) {
    switch (e) {
    case INIT:
      return PadMode.NOTE_PLAY;
    case CLIP_REC_BUTTON_ON:
      return m.recOn();
    case CLIP_REC_BUTTON_OFF:
      return m.recOff();
    case CLIP_REC_PAD_PRESSED:
      return m.recOff();
    default:
      throw new MPKStateError("Unrecognized event %s", e.toString());
    }
  }
  

  
  /* Instrument control paging */
  public final CStateField<ControlPager, PagerEvt> instrumentPager;
  public static class ControlPager {
    public final int knobPage;
    public final int faderPage;
    public final Pager activePager;

    public static final int NUM_PAGES = 7;

    public enum Pager {
      KNOB,
      FADER;
    }

    private ControlPager(int k, int f, Pager p) {
      this.knobPage = k;
      this.faderPage = f;
      this.activePager = p;
    }

    @Override
    public String toString() {
      return String.format("ControlPager(%d, %d, %s)", this.knobPage, this.faderPage, this.activePager);
    }

    public static final ControlPager INIT = new ControlPager(0, 0, Pager.KNOB);

    public ControlPager page(int page) {
      assert(page >= 0 && page < NUM_PAGES);
      switch (this.activePager) {
      case KNOB:
        return new ControlPager(page, this.faderPage, this.activePager);
      case FADER:
        return new ControlPager(this.knobPage, page, this.activePager);
      default:
        throw new MPKStateError("ControlPager:Pager: unknown %s",
                                this.activePager.toString());
      }
    }

    public ControlPager bound(int kPages, int fPages) {
      int k = Math.min(this.knobPage, kPages - 1);
      int f = Math.min(this.faderPage, fPages - 1);
      return new ControlPager(k, f, this.activePager);
    }

    public ControlPager pager(Pager p) {
      return new ControlPager(this.knobPage, this.faderPage, p);
    }

    @Override
    public boolean equals(Object obj) {
      if ( ! (obj instanceof ControlPager) ) {
        return false;
      }
      ControlPager p = (ControlPager)obj;
      return (this.activePager == p.activePager &&
              this.faderPage == p.faderPage &&
              this.knobPage == p.knobPage);
    }
  }
  public static class PagerEvt {
    public final Type tpe;
    public final int data;

    public static final int KNOB_PAGER = 0;
    public static final int FADER_PAGER = 1;

    public enum Type {
      PAGE,
      PAGER,
      BW_PAGE_COUNT;
    }

    private PagerEvt(Type t, int data) {
      this.tpe = t;
      this.data = data;
    }

    public static PagerEvt selectPage(int i) {
      return new PagerEvt(Type.PAGE, i);
    }

    public static PagerEvt switchPager(int pager) {
      return new PagerEvt(Type.PAGER, pager);
    }

    public static PagerEvt bwPageCount(int kCount, int fCount) {
      if (kCount > 0xff) {
        BWHost.errorln("knob pager page count stupidly big (%d)", kCount);
      }
      return new PagerEvt(Type.BW_PAGE_COUNT, ((0xff & kCount) << 8) | (0xff & fCount));
    }
  }
  private static ControlPager instrumentPagerTrans(ControlPager p, PagerEvt e) {
    switch (e.tpe) {
    case PAGE:
      return p.page(e.data);
    case PAGER:
      return p.pager(e.data > 0 ? ControlPager.Pager.FADER: ControlPager.Pager.KNOB);
    case BW_PAGE_COUNT:
      return p.bound((e.data >> 8) & 0xff, e.data & 0xff);
    default:
      throw new MPKStateError("instrumentPagerTrans: unknown event type %s", e.toString());
    }
  }

  /* constructor */
  public MPKCState() {
    padMode = new CStateField<>((p) -> padModeTrans(p.fst, p.snd));
    instrumentPager = new CStateField<>((p) -> instrumentPagerTrans(p.fst, p.snd));
  }

  /* initialization */
  public void init() {
    padMode.init(PadMode.NOTE_PLAY);
    instrumentPager.init(ControlPager.INIT);
  }

  /* add user */
  public
  CStateField<PadMode, PadEvt>.CStateConn<PadMode, PadEvt>
  padModeUser(CStateField.CStateCallback<PadMode> onUpdate) {
    return padMode.connect(onUpdate);
  }
  public
  CStateField<ControlPager, PagerEvt>.CStateConn<ControlPager, PagerEvt>
  instrumentPagerUser(CStateField.CStateCallback<ControlPager> onUpdate) {
    return instrumentPager.connect(onUpdate);
  }
  

  public static class MPKStateError extends HWError {
    public MPKStateError(String err) {
      super(err);
    }

    public MPKStateError(String format, Object... objs) {
      super(String.format(format, objs));
    }
  }
}
