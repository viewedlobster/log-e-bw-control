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
