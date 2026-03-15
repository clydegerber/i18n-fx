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

import javafx.application.Application;

/**
 * Classpath-mode entry point for the i18n-fx demonstration application.
 *
 * <p>A separate non-{@code Application} class is required as the main entry
 * point when running from the classpath (unnamed module). Java 11+ blocks
 * direct launch of an {@code Application} subclass from the classpath with
 * "JavaFX runtime components are missing"; this indirection avoids that check.
 * Use {@link TestFXApp} as the entry point when running on the module path.</p>
 */
public class TestFXAppLauncher
{
    public static void main(String[] args)
    {
        System.setProperty("apple.awt.application.name", "File Explorer FX");
        Application.launch(TestFXApp.class, args);
    }
}
