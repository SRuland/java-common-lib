/*
 *  SoSy-Lab Common is a library of useful utilities.
 *  This file is part of SoSy-Lab Common.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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

import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import javax.annotation.Nonnull;

/**
 * {@link Comparator} implementations for {@link Optional}, {@link OptionalInt}, {@link
 * OptionalLong}, and {@link OptionalDouble}.
 */
final class OptionalComparators {

  private OptionalComparators() {}

  static final Ordering<OptionalInt> INT_EMTPY_FIRST = new OptionalIntComparator(true);
  static final Ordering<OptionalInt> INT_EMTPY_LAST = new OptionalIntComparator(false);

  private static final class OptionalIntComparator extends Ordering<OptionalInt>
      implements Serializable {

    private static final long serialVersionUID = 4448617946052179218L;
    private final boolean emptyFirst;

    private OptionalIntComparator(boolean pEmptyFirst) {
      emptyFirst = pEmptyFirst;
    }

    @Override
    public int compare(@Nonnull OptionalInt left, @Nonnull OptionalInt right) {
      if (!left.isPresent()) {
        if (right.isPresent()) {
          // left no, right yes
          return emptyFirst ? -1 : 1;
        } else {
          return 0;
        }
      }
      if (!right.isPresent()) {
        // left yes, right no
        return emptyFirst ? 1 : -1;
      }
      return Integer.compare(left.getAsInt(), right.getAsInt());
    }

    @Override
    public String toString() {
      return "Optionals.comparingIntEmpty" + (emptyFirst ? "First" : "Last") + "()";
    }

    private Object readResolve() {
      // Multiton avoids need for equals()/hashCode()
      return emptyFirst ? INT_EMTPY_FIRST : INT_EMTPY_LAST;
    }
  }

  static final Ordering<OptionalLong> LONG_EMTPY_FIRST = new OptionalLongComparator(true);
  static final Ordering<OptionalLong> LONG_EMTPY_LAST = new OptionalLongComparator(false);

  private static final class OptionalLongComparator extends Ordering<OptionalLong>
      implements Serializable {

    private static final long serialVersionUID = -8237349997441501776L;
    private final boolean emptyFirst;

    private OptionalLongComparator(boolean pEmptyFirst) {
      emptyFirst = pEmptyFirst;
    }

    @Override
    public int compare(@Nonnull OptionalLong left, @Nonnull OptionalLong right) {
      if (!left.isPresent()) {
        if (right.isPresent()) {
          // left no, right yes
          return emptyFirst ? -1 : 1;
        } else {
          return 0;
        }
      }
      if (!right.isPresent()) {
        // left yes, right no
        return emptyFirst ? 1 : -1;
      }
      return Long.compare(left.getAsLong(), right.getAsLong());
    }

    @Override
    public String toString() {
      return "Optionals.comparingLongEmpty" + (emptyFirst ? "First" : "Last") + "()";
    }

    private Object readResolve() {
      // Multiton avoids need for equals()/hashCode()
      return emptyFirst ? LONG_EMTPY_FIRST : LONG_EMTPY_LAST;
    }
  }

  static final Ordering<OptionalDouble> DOUBLE_EMTPY_FIRST = new OptionalDoubleComparator(true);
  static final Ordering<OptionalDouble> DOUBLE_EMTPY_LAST = new OptionalDoubleComparator(false);

  private static final class OptionalDoubleComparator extends Ordering<OptionalDouble>
      implements Serializable {

    private static final long serialVersionUID = -3210510142079410508L;
    private final boolean emptyFirst;

    private OptionalDoubleComparator(boolean pEmptyFirst) {
      emptyFirst = pEmptyFirst;
    }

    @Override
    public int compare(@Nonnull OptionalDouble left, @Nonnull OptionalDouble right) {
      if (!left.isPresent()) {
        if (right.isPresent()) {
          // left no, right yes
          return emptyFirst ? -1 : 1;
        } else {
          return 0;
        }
      }
      if (!right.isPresent()) {
        // left yes, right no
        return emptyFirst ? 1 : -1;
      }
      return Double.compare(left.getAsDouble(), right.getAsDouble());
    }

    @Override
    public String toString() {
      return "Optionals.comparingDoubleEmpty" + (emptyFirst ? "First" : "Last") + "()";
    }

    private Object readResolve() {
      // Multiton avoids need for equals()/hashCode()
      return emptyFirst ? DOUBLE_EMTPY_FIRST : DOUBLE_EMTPY_LAST;
    }
  }
}
