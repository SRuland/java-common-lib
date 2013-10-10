/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2013  Dirk Beyer
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
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.common.collect;

import java.util.Collections;
import java.util.SortedMap;

import com.google.common.collect.Collections2;
import com.google.common.primitives.Chars;

/**
 * Utility class similar to {@link Collections} and {@link Collections2}.
 */
public class Collections3 {

  private Collections3() { }

  /**
   * Given a {@link SortedMap} with {@link String}s as key,
   * return a partial map (similar to {@link SortedMap#subMap(Object, Object)})
   * of all keys that have a given prefix.
   *
   * @param map The map to filter.
   * @param prefix The prefix that all keys in the result need to have.
   * @return A partial map of the input.
   */
  public static <V> SortedMap<String, V> subMapWithPrefix(SortedMap<String, V> map, String prefix) {
    // As the end marker of the set, create the string that is
    // the next bigger string than all possible strings with the given prefix
    // by taking the prefix and incrementing the value of the last character by one.
    StringBuilder end = new StringBuilder(prefix);

    int lastPos = end.length()-1;
    // This is basically end[lastPos] += 1
    end.setCharAt(lastPos, Chars.checkedCast((end.charAt(lastPos) + 1)));

    return map.subMap(prefix, end.toString());
  }
}