/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
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
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.common.configuration;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sosy_lab.common.Files;
import org.sosy_lab.common.configuration.Parser.InvalidConfigurationFileException;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

public class ParserTest {

  private static final String TEST_FILE_SUFFIX = ".properties";
  private static final String TEST_FILE_PREFIX = "SoSy-Lab_Common_ParserTest";

  public ParserTest() { }

  private String basePath;

  @Before
  public void resetBasePath() {
    basePath = "";
  }

  private Map<String, String> test(String content) throws IOException, InvalidConfigurationException {
    InputStream stream = new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));

    return Parser.parse(stream, basePath, "test").getFirst();
  }

  private void testEmpty(String content) {
    try {
      Map<String, String> parsedOptions = test(content);

      assertEquals(Collections.emptyMap(), parsedOptions);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void testSingleOption(String content, String key, String value) {
    try {
      Map<String, String> parsedOptions = test(content);

      Map<String, String> expectedOptions = Collections.singletonMap(key, value);

      assertEquals(expectedOptions, parsedOptions);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public final void empty() {
    testEmpty("");
    testEmpty("   \t  ");
    testEmpty(" \n \n  \t \n ");
    testEmpty("[foo]");
    testEmpty("# comment\n [foo] \n [] \n \n");
  }

  @Test
  public final void simpleOptions() {
    testSingleOption("foo.bar=ab cde ef", "foo.bar", "ab cde ef");
    testSingleOption(" foo.bar \t= 123 4 5 6 ", "foo.bar", "123 4 5 6");
  }


  @Test
  public final void specialChar() {
    testSingleOption("foo.bar= äöüß ", "foo.bar", "äöüß");
    testSingleOption("foo.bar= abc\t123 ", "foo.bar", "abc\t123");
    testSingleOption("foo.bar= abc=123 ", "foo.bar", "abc=123");
    testSingleOption("foo.bar====", "foo.bar", "===");
    testSingleOption("foo.bar= \"abc cde\"", "foo.bar", "\"abc cde\"");
    testSingleOption("foo.bar= a=1, b=2, c=3", "foo.bar", "a=1, b=2, c=3");
  }

  @Test
  public final void category() {
    testSingleOption("[foo]\n bar=abc", "foo.bar", "abc");
    testSingleOption("  [  foo  ]  \n bar=abc", "foo.bar", "abc");
    testSingleOption("[foo.bar]\n abc=123", "foo.bar.abc", "123");
    testSingleOption("[]\n foo.bar=123", "foo.bar", "123");
    testSingleOption("[]\n [foo]\n bar=123", "foo.bar", "123");
    testSingleOption("[foo]\n []\n bar=123", "bar", "123");
  }

  @Test
  public final void emptyLine() {
    testSingleOption("\n\n\n foo.bar=abc \n\n\n", "foo.bar", "abc");
    testSingleOption(" \n\t\n \t \n foo.bar=abc \n\n\n", "foo.bar", "abc");
  }

  @Test
  public final void comment() {
    testSingleOption("# comment \n foo.bar=abc", "foo.bar", "abc");
    testSingleOption("// comment \n foo.bar=abc", "foo.bar", "abc");
    testSingleOption("// comment \n foo.bar=abc \n # comment", "foo.bar", "abc");
    testSingleOption("foo.bar=abc # no comment", "foo.bar", "abc # no comment");
    testSingleOption("foo.bar=abc // no comment", "foo.bar", "abc // no comment");
  }

  @Test
  public final void lineContinuation() {
    testSingleOption("foo.bar=abc\\\n123", "foo.bar", "abc123");
    testSingleOption("foo.bar=abc\\\n", "foo.bar", "abc");
    testSingleOption("foo.bar=abc\\", "foo.bar", "abc");
    testSingleOption("foo.bar=abc \\\n // no comment", "foo.bar", "abc // no comment");
    testSingleOption("foo.bar=abc \\\n #include no include", "foo.bar", "abc #include no include");
    testSingleOption("foo.bar=abc \\  \n   123", "foo.bar", "abc 123");
    testSingleOption("foo.bar= \\  \n   123", "foo.bar", "123");
    testSingleOption("foo.bar= a=1,\\\n b=2,\\\n c=3", "foo.bar", "a=1,b=2,c=3");
    testSingleOption("foo.bar=abc\\\n \\\n \\\n 123", "foo.bar", "abc123");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalLine1() throws IOException, InvalidConfigurationException {
    test("a");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalLine2() throws IOException, InvalidConfigurationException {
    test("abc.bar");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalLine3() throws IOException, InvalidConfigurationException {
    test("[foo.bar");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalKey1() throws IOException, InvalidConfigurationException {
    test("foo bar = abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalKey2() throws IOException, InvalidConfigurationException {
    test("fooäöüßbar = abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalKey3() throws IOException, InvalidConfigurationException {
    test("foo\tbar = abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalKey4() throws IOException, InvalidConfigurationException {
    test("foo\\bar = abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalCategory1() throws IOException, InvalidConfigurationException {
    test("[foo bar]");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalCategory2() throws IOException, InvalidConfigurationException {
    test("[fooäöüßbar]");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalCategory3() throws IOException, InvalidConfigurationException {
    test("[foo\tbar]");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalCategory4() throws IOException, InvalidConfigurationException {
    test("[foo\\bar]");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalDirective() throws IOException, InvalidConfigurationException {
    test("#comment");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalInclude1() throws IOException, InvalidConfigurationException {
    test("#include");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void illegalInclude2() throws IOException, InvalidConfigurationException {
    test("#include  \t");
  }

  @Test(expected=FileNotFoundException.class)
  public final void illegalInclude3() throws IOException, InvalidConfigurationException {
    test("#include .");
  }

  @Test(expected=FileNotFoundException.class)
  public final void illegalInclude4() throws IOException, InvalidConfigurationException {
    test("#include \\");
  }

  @Test(expected=FileNotFoundException.class)
  public final void illegalInclude5() throws IOException, InvalidConfigurationException {
    test("#include /");
  }

  @Test(expected=FileNotFoundException.class)
  public final void illegalInclude6() throws IOException, InvalidConfigurationException {
    test("#include ./SoSy-Lab Common Tests/Non-Existing-File");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void duplicateOption1() throws IOException, InvalidConfigurationException {
    test("foo.bar=abc \n foo.bar=abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void duplicateOption2() throws IOException, InvalidConfigurationException {
    test("foo.bar=abc \n foo.bar=123");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void duplicateOption3() throws IOException, InvalidConfigurationException {
    test("foo.bar=abc \n [foo] \n bar=abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void duplicateOption4() throws IOException, InvalidConfigurationException {
    test("[foo] \n bar=abc \n [foo] \n bar=abc");
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void duplicateOption5() throws IOException, InvalidConfigurationException {
    test("[foo] \n bar=abc \n [] \n foo.bar=abc");
  }

  @Test
  public final void simpleInclude() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    try {
      testSingleOption(" #include  " + included.getAbsolutePath() + "\t", "foo.bar", "abc");
    } finally {
      included.delete();
    }
  }

  @Test
  public final void includeWithSpecialCharsFilename() throws IOException {
    File included = Files.createTempFile("SoSy-Lab Common\tParserTestÄöüß", TEST_FILE_SUFFIX, "foo.bar=abc");
    try {
      testSingleOption(" #include " + included.getAbsolutePath() + "\t", "foo.bar", "abc");
    } finally {
      included.delete();
    }
  }

  @Test
  public final void includeDepthTwo() throws IOException {
    File included1 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included1.getAbsolutePath());
    try {
      testSingleOption("#include " + included2.getAbsolutePath(), "foo.bar", "abc");
    } finally {
      included1.delete();
      included2.delete();
    }
  }

  private static final int MAX_INCLUDE_TEST_DEPTH = 10;
  @Test
  public final void includeDepthN() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");

    List<File> allFiles = Lists.newArrayList();
    allFiles.add(included);

    for (int i = 0; i < MAX_INCLUDE_TEST_DEPTH; i++) {
      included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included.getAbsolutePath());
      allFiles.add(included);
    }

    try {
      testSingleOption("#include " + included.getAbsolutePath(), "foo.bar", "abc");
    } finally {
      for (File toDelete : allFiles) {
        toDelete.delete();
      }
    }
  }

  @Test
  public final void includeTwice() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    try {
      testSingleOption("#include " + included.getAbsolutePath() + "\n#include " + included.getAbsolutePath(), "foo.bar", "abc");
    } finally {
      included.delete();
    }
  }


  @Test(expected=InvalidConfigurationFileException.class)
  public final void recursiveInclude() throws IOException, InvalidConfigurationException {
    File included = File.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX);
    Files.writeFile(included, "#include " + included.getAbsolutePath());
    try {
      test("#include " + included.getAbsolutePath());
    } finally {
      included.delete();
    }
  }

  @Test(expected=InvalidConfigurationFileException.class)
  public final void recursiveIncludeDepthTwo() throws IOException, InvalidConfigurationException {
    File included1 = File.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX);
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included1.getAbsolutePath());
    Files.writeFile(included1, "#include " + included2.getAbsolutePath());
    try {
      test("#include " + included1.getAbsolutePath());
    } finally {
      included1.delete();
      included2.delete();
    }
  }

  private static final int MAX_RECURSIVE_INCLUDE_TEST_DEPTH = 10;
  @Test(expected=InvalidConfigurationFileException.class)
  public final void recursiveIncludeDepthN() throws IOException, InvalidConfigurationException {
    File firstIncluded = File.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX);

    List<File> allFiles = Lists.newArrayList();
    allFiles.add(firstIncluded);
    File included = firstIncluded;

    for (int i = 0; i < MAX_RECURSIVE_INCLUDE_TEST_DEPTH; i++) {
      included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included.getAbsolutePath());
      allFiles.add(included);
    }

    Files.writeFile(firstIncluded, "#include " + included.getAbsolutePath());

    try {
      test("#include " + included.getAbsolutePath());
    } finally {
      for (File toDelete : allFiles) {
        toDelete.delete();
      }
    }
  }


  @Test
  public final void overwriteIncludedOptionBefore() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    try {
      testSingleOption("foo.bar=123 \n#include " + included.getAbsolutePath(), "foo.bar", "123");
    } finally {
      included.delete();
    }
  }

  @Test
  public final void overwriteIncludedOptionAfter() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    try {
      testSingleOption("#include " + included.getAbsolutePath() + " \n foo.bar=123", "foo.bar", "123");
    } finally {
      included.delete();
    }
  }

  @Test
  public final void overwriteIncludedDepthTwo1() throws IOException {
    File included1 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included1.getAbsolutePath() + "\n foo.bar=xyz");
    try {
      testSingleOption("#include " + included2.getAbsolutePath(), "foo.bar", "xyz");
    } finally {
      included1.delete();
      included2.delete();
    }
  }

  @Test
  public final void overwriteIncludedDepthTwo2() throws IOException {
    File included1 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included1.getAbsolutePath() + "\n foo.bar=xyz");
    try {
      testSingleOption("foo.bar=123 \n#include " + included2.getAbsolutePath(), "foo.bar", "123");
    } finally {
      included1.delete();
      included2.delete();
    }
  }


  @Test
  public final void contradictoryIncludes() throws IOException {
    File included1 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=xyz");
    try {
      testSingleOption("#include " + included1.getAbsolutePath() + "\n#include " + included2.getAbsolutePath(), "foo.bar", "xyz");
    } finally {
      included1.delete();
      included2.delete();
    }
  }


  @Test
  public final void relativePath1() throws IOException {
    File included = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    basePath = included.getParent();

    try {
      testSingleOption("#include " + included.getName(), "foo.bar", "abc");
    } finally {
      included.delete();
    }
  }

  @Test
  public final void relativePath2() throws IOException {
    File included1 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "foo.bar=abc");
    File included2 = Files.createTempFile(TEST_FILE_PREFIX, TEST_FILE_SUFFIX, "#include " + included1.getName());

    try {
      testSingleOption("#include " + included2.getAbsolutePath(), "foo.bar", "abc");
    } finally {
      included1.delete();
      included2.delete();
    }
  }
}
