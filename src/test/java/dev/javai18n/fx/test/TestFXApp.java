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
import javafx.stage.Stage;

/**
 * Entry point for the i18n-fx visual demonstration application.
 *
 * <p>Run via {@code main()} to launch the JavaFX file-explorer demo that
 * showcases locale-aware JavaFX components from the i18n-fx library.</p>
 */
public class TestFXApp extends Application
{
    /**
     * JavaFX entry point. Creates and shows the DemoStage.
     * The primary stage provided by the platform is unused; DemoStage
     * creates its own Stage instance extending LocalizableStage.
     *
     * @param ignored The primary stage provided by the JavaFX platform.
     */
    @Override
    public void start(Stage ignored)
    {
        FXTestModuleRegistrar.ensureRegistered();
        DemoStage stage = DemoStage.create();
        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args)
    {
        // Set the macOS application name property before launching the JavaFX application
        System.setProperty("apple.awt.application.name", "File Explorer FX");
        launch(args);
    }
}
