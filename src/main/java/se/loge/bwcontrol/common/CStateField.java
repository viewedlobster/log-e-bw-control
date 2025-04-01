package se.loge.bwcontrol.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

import com.bitwig.extension.controller.api.HardwareActionBindable;

import se.loge.bwcontrol.common.ifc.HasBWHost;

public class CStateField<S, E> implements HasBWHost {
  private S st;
  private Function<CPair<S, E>, S> trans;
  private List<CStateConn<S, E>> conns;
  private Queue<E> evts;
  private boolean handlingEvts;

  private CStateField<S, E> self = this;

  private CStateField(S s, Function<CPair<S, E>, S> t) {
    this.st = s;
    this.trans = t;
    this.conns = new ArrayList<>();
    this.evts = new LinkedList<>();
    this.handlingEvts = false;
  }

  public CStateField(Function<CPair<S, E>, S> t) {
    this(null, t);
  }

  public S get() {
    return st;
  }

  private void handleEvts() {
    if ( handlingEvts )
      return;

    handlingEvts = true;
    while ( ! evts.isEmpty() ) {
      handle(evts.poll());
    }
    handlingEvts = false;
  }

  private void handle(E e) {
    if (this.st == null) {
      throw new CStateError("Uninitialized CState object received event");
    }

    S ol = this.st;
    S nw = trans.apply(CPair.p(ol, e));

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

  private boolean recv(E e) {
    if ( st != null ) {
      evts.add(e);
      handleEvts();
      return true;
    }
    return false;
  }

  public CStateConn<S, E> connect(CStateCallback<S> onUpd) {
    CStateConn<S, E> conn = new CStateConn<S, E>() {
      public S get() {
        return self.st;
      }

      public boolean send(E e) {
        return recv(e);
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
    public abstract boolean send(E evt);
    public HardwareActionBindable sendAction(E evt) {
      return customAction(() -> this.send(evt));
    }
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
