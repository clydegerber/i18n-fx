/*
 * Copyright 2026 Clyde Gerber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.javai18n.fx.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/**
 * Utility class for JavaFX headless testing without TestFX.
 * Provides platform startup and FX-thread execution helpers.
 */
public final class FXTestHelper
{
    private static volatile boolean started = false;

    private FXTestHelper() {}

    /**
     * Ensure the JavaFX platform has been started. Idempotent and thread-safe.
     * Blocks the calling thread until the platform is ready.
     */
    public static void ensureStarted()
    {
        if (!started)
        {
            synchronized (FXTestHelper.class)
            {
                if (!started)
                {
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.startup(latch::countDown);
                    try { latch.await(); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    started = true;
                }
            }
        }
    }

    /**
     * Schedule the given action on the JavaFX Application Thread and wait for it to complete.
     * Waits up to 5 seconds for the action to finish.
     *
     * @param action The action to run on the FX thread.
     * @throws Exception If the action throws, or the wait is interrupted.
     */
    public static void runAndWait(Runnable action) throws Exception
    {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() ->
        {
            try { action.run(); }
            finally { latch.countDown(); }
        });
        if (!latch.await(5, TimeUnit.SECONDS))
            throw new AssertionError("FX action did not complete within 5 seconds");
    }
}
