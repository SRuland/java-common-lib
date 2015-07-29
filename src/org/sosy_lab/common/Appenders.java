/*
 *  SoSy-Lab Common is a library of useful utilities.
 *  This file is part of SoSy-Lab Common.
 *
 *  Copyright (C) 2007-2015  Dirk Beyer
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Utility class providing {@link Appender}s for various cases.
 */
public class Appenders {

  private Appenders() { }

  /**
   * Return an {@link Appender} for the given object.
   * If the object is an {@link Appender} itself, it is returned.
   * Otherwise an appender that calls {@link Object#toString()} is returned
   * (c.f. {@link #fromToStringMethod(Object)}.
   *
   * @param o The object which will be dumped, may be null.
   * @return an {@link Appender} instance
   */
  public static Appender createAppender(@Nullable final Object o) {
    if (o instanceof Appender) {
      return (Appender)o;
    } else {
      return fromToStringMethod(o);
    }
  }

  /**
   * Write the given object into the given output.
   * If the object is an {@link Appender}, its {@link Appender#appendTo(Appendable)}
   * method is called, otherwise the {@link Object#toString()} method is called.
   * The object may be {@code null}, in this case {@code "null"} is written.
   * @param output The appendable to write into.
   * @param o The object which will be dumped, may be null.
   * @throws IOException If the appendable throws an IOException
   */
  public static void appendTo(final Appendable output,  @Nullable final Object o)
      throws IOException {
    if (o instanceof Appender) {
      ((Appender)o).appendTo(output);
    } else {
      output.append(Objects.toString(o));
    }
  }

  /**
   * Return an {@link Appender} that writes an {@link Iterable} into the output
   * using a given {@link Joiner}.
   * @param joiner The joiner that will be used to create a string representation of the iterable.
   * @param it The iterable which will be dumped.
   * @return an {@link Appender} instance
   */
  public static Appender forIterable(final Joiner joiner, final Iterable<?> it) {
    checkNotNull(joiner);
    checkNotNull(it);

    return new AbstractAppender() {
      @Override
      public void appendTo(Appendable appendable) throws IOException {
        joiner.appendTo(appendable, it);
      }
    };
  }

  /**
   * Return an {@link Appender} that writes a {@link Map} into the output
   * using a given {@link Joiner}.
   * @param joiner The joiner that will be used to create a string representation of the map.
   * @param map The map which will be dumped.
   * @return an {@link Appender} instance
   */
  public static Appender forMap(final Joiner.MapJoiner joiner, final Map<?, ?> map) {
    checkNotNull(joiner);
    checkNotNull(map);

    return new AbstractAppender() {
      @Override
      public void appendTo(Appendable appendable) throws IOException {
        joiner.appendTo(appendable, map);
      }
    };
  }

  /**
   * Return an {@link Appender} that writes the result of the {@link Object#toString()}
   * method of an object into the output.
   *
   * This will not give the performance benefit that is expected from the use of
   * appenders, and should only be used to adapt classes not implementing this
   * interface themselves.
   *
   * If {@code null} is passed, the resulting appender will write {@code "null"}.
   * If an object is passed, the appender will call the {@link Object#toString()}
   * method once each time it is used (no caching is done).
   *
   * @param o The object which will be dumped, may be null.
   * @return an {@link Appender} instance
   */
  public static Appender fromToStringMethod(@Nullable final Object o) {
    return new AbstractAppender() {
      @Override
      public void appendTo(Appendable appendable) throws IOException {
        appendable.append(Objects.toString(o));
      }
    };
  }

  /**
   * Let an {@link Appender} dump itself into a {@link StringBuilder}.
   * This method is similar to passing the {@link StringBuilder} to
   * the {@link Appender#appendTo(Appendable)} method, just without the checked
   * exception.
   * @param sb The StringBuilder that will receive the content.
   * @param a The Appender to dump into the StringBuilder.
   * @return The passed StringBuilder to allow for method chaining.
   */
  public static StringBuilder appendTo(StringBuilder sb, Appender a) {
    checkNotNull(sb);
    try {
      a.appendTo(sb);
    } catch (IOException e) {
      throw new AssertionError("StringBuilder threw IOException", e);
    }
    return sb;
  }

  /**
   * Create a new {@link Appender} that consists of the sequential concatenation
   * of multiple appenders.
   * The given iterable is traversed once each time the resulting appender's
   * {@link Appender#appendTo(Appendable)} method is called.
   * The iterable may not contain nulls or be null itself..
   */
  public static Appender concat(final Iterable<Appender> pAppenders) {
    checkNotNull(pAppenders);
    return new AbstractAppender() {
      @Override
      public void appendTo(Appendable pAppendable) throws IOException {
        for (Appender appender : pAppenders) {
          appender.appendTo(pAppendable);
        }
      }
    };
  }

  /**
   * Create a new {@link Appender} that consists of the sequential concatenation
   * of multiple appenders.
   *
   * @throws NullPointerException if any of the provided appendables is null
   */
  public static Appender concat(final Appender... pAppenders) {
    return concat(ImmutableList.copyOf(pAppenders));
  }

  /**
   * Convert an {@link Appender} into a string by calling it's
   * {@link Appender#appendTo(Appendable)} method.
   *
   * Note that the contract of {@link Appender} specifies that you should be able
   * to call {@link Object#toString()} on the object and get the same result,
   * thus it should not be necessary to call this method from client code.
   *
   * However, it may be practical to implement the {@link Object#toString()}
   * method of an {@link Appender} by delegating to this method.
   *
   * @param a The {@link Appender} to convert into a string.
   * @return a string representation of the passed object.
   */
  public static String toString(Appender a) {
    return appendTo(new StringBuilder(), a).toString();
  }

  private static class SizeLimitReachedException extends IOException {
    private static final long serialVersionUID = 1855247676627224183L;
  }

  /**
   * Convert an {@link Appender} into a string by calling it's
   * {@link Appender#appendTo(Appendable)} method.
   *
   * This method truncates the returned string at a given length,
   * and tries to be more efficient than generating the full string
   * and truncating it at the end (though no guarantees are made).
   *
   * @param a The {@link Appender} to convert into a string.
   * @param truncateAt The maximum size of the returned string (>= 0).
   * @return a string representation of the passed object,
   * with a maximum size of <code>truncateAt</code>
   */
  public static String toStringWithTruncation(Appender a, final int truncateAt) {
    checkArgument(truncateAt >= 0, "Maximum size of String cannot be negative");
    final StringBuilder sb = new StringBuilder();
    Appendable limiter = new Appendable() {

      private void checkSize() throws SizeLimitReachedException {
        if (sb.length() >= truncateAt) {
          throw new SizeLimitReachedException();
        }
      }

      @Override
      public Appendable append(CharSequence pCsq, int pStart, int pEnd) throws IOException {
        sb.append(pCsq, pStart, pEnd);
        checkSize();
        return this;
      }

      @Override
      public Appendable append(char pC) throws IOException {
        sb.append(pC);
        checkSize();
        return this;
      }

      @Override
      public Appendable append(CharSequence pCsq) throws IOException {
        sb.append(pCsq);
        checkSize();
        return this;
      }
    };

    try {
      a.appendTo(limiter);
    } catch (SizeLimitReachedException e) {
      assert sb.length() >= truncateAt;
      sb.setLength(truncateAt);
    } catch (IOException e) {
      throw new AssertionError("StringBuilder threw IOException", e);
    }
    return sb.toString();
  }

  /**
   * Base implementation of {@link Appender} that ensures that the {@link #toString()}
   * method returns the same result that {@link #appendTo(Appendable)} produces
   * in order to ensure that the contract of {@link Appender} is fulfilled.
   */
  public static abstract class AbstractAppender implements Appender {

    @Override
    public String toString() {
      return Appenders.toString(this);
    }
  }
}
