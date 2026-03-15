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

module dev.javai18n.fx.test
{
    requires static org.junit.jupiter.api;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;
    requires dev.javai18n.fx;
    requires dev.javai18n.swing;
    exports dev.javai18n.fx.test;
    opens dev.javai18n.fx.test;
    uses dev.javai18n.fx.test.spi.AppStageProvider;
    uses dev.javai18n.fx.test.spi.TestComponentSourceProvider;
    provides dev.javai18n.fx.test.spi.AppStageProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
    provides dev.javai18n.fx.test.spi.TestComponentSourceProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
    uses dev.javai18n.fx.test.spi.DemoStageProvider;
    provides dev.javai18n.fx.test.spi.DemoStageProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
    uses dev.javai18n.fx.test.spi.FXMLContactStageProvider;
    provides dev.javai18n.fx.test.spi.FXMLContactStageProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
    uses dev.javai18n.fx.test.spi.SwingInteropStageProvider;
    provides dev.javai18n.fx.test.spi.SwingInteropStageProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
    uses dev.javai18n.fx.test.spi.DemoPopupProvider;
    provides dev.javai18n.fx.test.spi.DemoPopupProvider
        with dev.javai18n.fx.test.spi.ModuleProviderImpl;
}
