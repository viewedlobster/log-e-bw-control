package se.loge.bwcontrol.common;

import java.util.LinkedList;
import java.util.List;

public class CallbackRegistry<T> {

  public interface MatchingCallback<S> {
    public boolean runAndMatch(S arg);
  }
  private final List<MatchingCallback<T>> callbacks;


  public CallbackRegistry() {
    callbacks = new LinkedList<>();
  }

  public Object register(MatchingCallback<T> callback) {
    callbacks.add(callback);
    return callback;
  }

  public boolean unregister(Object o) {
    boolean removed = callbacks.remove(o);
    return removed;
  }

  public boolean invoke(T arg) {
    for (MatchingCallback<T> callback : callbacks) {
      if (callback.runAndMatch(arg))
        return true;
    }
    return false;
  }
}
