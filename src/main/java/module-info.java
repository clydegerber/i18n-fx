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

/**
 * Welcome to the dev/javai18n/fx API documentation.
 *
 * For general information, installation instructions, and examples, please see the
 * <a href="{@docRoot}/README.html">Project README</a>.
 */
module dev.javai18n.fx
{
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires transitive dev.javai18n.core;
    exports dev.javai18n.fx;
    exports dev.javai18n.fx.spi;
    opens dev.javai18n.fx;
    uses dev.javai18n.fx.spi.LocalizableStageProvider;
    uses dev.javai18n.fx.spi.FXLoggerProvider;
    provides dev.javai18n.fx.spi.LocalizableStageProvider
        with dev.javai18n.fx.spi.ModuleProviderImpl;
    provides dev.javai18n.fx.spi.FXLoggerProvider
        with dev.javai18n.fx.spi.ModuleProviderImpl;
    uses dev.javai18n.fx.spi.LocalizablePopupProvider;
    provides dev.javai18n.fx.spi.LocalizablePopupProvider
        with dev.javai18n.fx.spi.ModuleProviderImpl;
}
