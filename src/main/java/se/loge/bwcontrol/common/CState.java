package se.loge.bwcontrol.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;

public class CState<S, E> implements HWIHasHost {
  private S st;
  private Function<SPair<S, E>, S> trans;
  private List<CStateConn<S, E>> conns;

  private CState<S, E> self = this;

  public CState(S s, Function<SPair<S, E>, S> t) {
    this.st = s;
    this.trans = t;
    this.conns = new ArrayList<>();
  }

  public CState(Function<SPair<S, E>, S> t) {
    this(null, t);
  }

  public S get() {
    return st;
  }

  private void handle(E e) {
    if (this.st == null) {
      throw new CStateError("Uninitialized CState object received event");
    }

    S ol = this.st;
    S nw = trans.apply(SPair.p(ol, e));


    println(String.format("%s -{ %s }-> %s", ol, e, nw));

    if ( ! nw.equals(ol) ) {
      this.st = nw;
      onUpdate(nw);
    }
  }

  private void onUpdate(S nw) {
    for (CStateConn<S, E> conn : conns) {
      conn.onUpdate(nw);
    }
  }

  public CStateConn<S, E> connect(CStateCallback<S> onUpd) {
    CStateConn<S, E> conn = new CStateConn<S, E>() {
      public S get() {
        return self.st;
      }

      public void send(E e) {
        handle(e);
      }

      void onUpdate(S s) {
        onUpd.run(s);
      }
    };

    conns.add(conn);

    return conn;
  }

  public void init(S s) {
    if (this.st != null) {
      throw new CStateError("Initialization of already initialized CState object");
    }
    this.st = s;
    onUpdate(s);
  }

  public interface CStateCallback<S> {
    public abstract void run(S s);
  }

  public abstract class CStateConn<S, E> {
    public abstract S get();
    public abstract void send(E evt);
    abstract void onUpdate(S s);
  }

  public static class CStateError extends HWError {
    protected CStateError(String err) {
      super(err);
    }

    protected CStateError(String format, Object... objs) {
      super(String.format(format, objs));
    }
  }
}
