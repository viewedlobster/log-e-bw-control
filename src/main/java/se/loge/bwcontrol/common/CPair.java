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

public class CPair<T1, T2> {
  public final T1 fst;
  public final T2 snd;

  private CPair(T1 t1, T2 t2) {
    this.fst = t1;
    this.snd = t2;
  }

  public static <T1, T2>  CPair<T1, T2> p(T1 t1, T2 t2) {
    return new CPair<>(t1, t2);
  }

  @Override
  public String toString() {
    return String.format("(%s, %s)", fst, snd);
  }
}