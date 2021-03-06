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
package org.sosy_lab.common.configuration.converters;

import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import javax.annotation.Nullable;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.OptionDetailAnnotation;
import org.sosy_lab.common.log.LogManager;

/**
 * TypeConverters are used to parse Strings into instances of specific types during configuration
 * option injection as performed by {@link Configuration#inject(Object)}. Each type converter may
 * handle one or more types.
 *
 * <p>TypeConverters need not to be thread safe as long as no injection is performed in parallel,
 * however, they should have no mutable state anyway.
 *
 * <p>Primitive types will be converted into their corresponding wrapper type before any type
 * converter is called, so they do not need to care at all about primitive types.
 */
public interface TypeConverter {

  /**
   * Convert a String given by the user to an instance of a given type.
   *
   * <p>Although the signature of this method does not enforce it, the class of the returned value
   * needs to be assignable to "type" as defined by {@link
   * TypeToken#isSupertypeOf(java.lang.reflect.Type)}.
   *
   * <p>Before this method is called, the caller ensures that all requirements for the option
   * defined with the {@link Option} annotation are met.
   *
   * @param optionName The name of the option (should only be used for nice error messages).
   * @param value The string to parse.
   * @param type The target type.
   * @param secondaryOption An optional second annotation for the option (this is one of the
   *     annotations marked with {@link OptionDetailAnnotation}).
   * @param source The file where the configuration option was read from. May contain a dummy value
   *     if the option was given somehow else.
   * @param logger A logger for warnings etc.
   * @return An instance of the target type produced from the string representation-
   * @throws UnsupportedOperationException If the option specification in the source code is invalid
   *     (e.g., a missing annotation).
   * @throws InvalidConfigurationException If the user specified an invalid value.
   */
  @Nullable
  Object convert(
      String optionName,
      String value,
      TypeToken<?> type,
      @Nullable Annotation secondaryOption,
      Path source,
      LogManager logger)
      throws InvalidConfigurationException;

  /**
   * Optionally convert the default value for an option that was given in the source code. This
   * method is called if the user gave no explicit value.
   *
   * @param optionName The name of the option (should only be used for nice error messages).
   * @param value The default value (may be null).
   * @param type The target type.
   * @param secondaryOption An optional second annotation for the option
   * @return An instance of the target type.
   * @throws InvalidConfigurationException If the default value is invalid for this option.
   * @throws UnsupportedOperationException If the option specification in the source code is invalid
   *     (e.g., a missing annotation).
   */
  @Nullable
  <T> T convertDefaultValue(
      String optionName, @Nullable T value, TypeToken<T> type, @Nullable Annotation secondaryOption)
      throws InvalidConfigurationException;
}
