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
package org.sosy_lab.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Future implementation that can be used when a task should be executed
 * only lazily at the first time {@link #get()} is called.
 * I.e., it is not guaranteed that the task is run at all,
 * but it is called at most once.
 *
 * Execution of the task happens in the caller's thread,
 * a little bit similar to the use of
 * {@link com.google.common.util.concurrent.MoreExecutors#sameThreadExecutor()},
 * however, it is executed on the thread calling {@link #get()}
 * and not on the thread calling
 * {@link java.util.concurrent.ExecutorService#submit(Runnable)}.
 *
 * Important: Calling {@link #get(long, TimeUnit)} is not supported
 * and will always throw {@link UnsupportedOperationException}.
 *
 * Canceling this future works as expected.
 */
public class LazyFutureTask<V> extends FutureTask<V> {

  public LazyFutureTask(Callable<V> pCallable) {
    super(pCallable);
  }

  public LazyFutureTask(Runnable pRunnable, @Nullable V pResult) {
    super(pRunnable, pResult);
  }

  @Override
  public void run() {
    // Do nothing here, we execute the task only lazily in get().
  }

  @Override
  public @Nullable V get() throws InterruptedException, ExecutionException {
    if (!isDone()) {
      // Note that two threads calling this method at the same time is safe
      // (the task won't actually be executed twice)
      // because super.run() checks whether the future is currently in state
      // RUNNING and does nothing in this case.
      // (This is an advantage over using Guava's AbstractFuture,
      // where we would have to do this ourselves.)
      super.run();
    }

    return super.get();
  }

  /**
   * Always throws {@link UnsupportedOperationException}.
   * @throws UnsupportedOperationException
   */
  @Deprecated
  @Override
  public V get(long pTimeout, TimeUnit pUnit) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}
