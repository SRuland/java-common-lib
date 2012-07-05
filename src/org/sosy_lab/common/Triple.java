/*
 *  SoSy-Lab Common is a library of useful utilities.
 *  This file is part of SoSy-Lab Common.
 *
 *  Copyright (C) 2007-2012  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;


/**
 * A generic Triple class based on Pair.java
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Triple<A, B, C> {
    @Nullable private final A first;
    @Nullable private final B second;
    @Nullable private final C third;

    public Triple(@Nullable A first, @Nullable B second, @Nullable C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Triple<A, B, C> of(@Nullable A first, @Nullable B second, @Nullable C third) {
      return new Triple<A, B, C>(first, second, third);
    }

    @Nullable public final A getFirst() { return first; }
    @Nullable public final B getSecond() { return second; }
    @Nullable public final C getThird() { return third; }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    private static boolean equals(@Nullable Object x, @Nullable Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    @Override
    public boolean equals(@Nullable Object other) {
    return (other instanceof Triple<?,?,?>)
        && equals(first,  ((Triple<?,?,?>)other).first)
        && equals(second, ((Triple<?,?,?>)other).second)
        && equals(third,  ((Triple<?,?,?>)other).third);
    }

    @Override
    public int hashCode() {
        if (first == null && second == null) {
          return (third == null) ? 0 : third.hashCode() + 1;
        } else if (first == null && third == null) {
          return second.hashCode() + 2;
        } else if (first == null) {
          return second.hashCode() * 7 + third.hashCode();
        } else if (second == null && third == null) {
          return first.hashCode() + 3;
        } else if (second == null) {
          return first.hashCode() * 11 + third.hashCode();
        } else if (third == null) {
          return first.hashCode() * 13 + second.hashCode();
        } else {
          return first.hashCode() * 17 + second.hashCode() * 5 + third.hashCode();
        }
    }


    public static <T> Function<Triple<? extends T, ?, ?>, T> getProjectionToFirst() {
      return Holder.<T, Void>getInstance().PROJECTION_TO_FIRST;
    }

    public static <T> Function<Triple<?, ? extends T, ?>, T> getProjectionToSecond() {
      return Holder.<T, Void>getInstance().PROJECTION_TO_SECOND;
    }

    public static <T> Function<Triple<?, ?, ? extends T>, T> getProjectionToThird() {
      return Holder.<T, Void>getInstance().PROJECTION_TO_THIRD;
    }

    /*
     * Static holder class for several function objects because if these fields
     * were static fields of the Triple class, they couldn't be generic.
     */
    private static final class Holder<T, T2> {

      private static final Holder<?, ?> INSTANCE = new Holder<Void, Void>();

      // Cast is safe because class has no mutable state
      @SuppressWarnings("unchecked")
      public static <T, T2> Holder<T, T2> getInstance() {
        return (Holder<T, T2>) INSTANCE;
      }

      private final Function<Triple<? extends T, ?, ?>, T> PROJECTION_TO_FIRST = new Function<Triple<? extends T, ?, ?>, T>() {
        @Override
        public T apply(@Nonnull Triple<? extends T, ?, ?> pArg0) {
          return pArg0.getFirst();
        }
      };

      private final Function<Triple<?, ? extends T, ?>, T> PROJECTION_TO_SECOND = new Function<Triple<?, ? extends T, ?>, T>() {
        @Override
        public T apply(@Nonnull Triple<?, ? extends T, ?> pArg0) {
          return pArg0.getSecond();
        }
      };

      private final Function<Triple<?, ?, ? extends T>, T> PROJECTION_TO_THIRD = new Function<Triple<?, ?, ? extends T>, T>() {
        @Override
        public T apply(@Nonnull Triple<?, ?, ? extends T> pArg0) {
          return pArg0.getThird();
        }
      };
    }
}
