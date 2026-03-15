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

import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.FXLogger;
import dev.javai18n.fx.LocalizablePopup;
import dev.javai18n.fx.ResourcefulButton;
import dev.javai18n.fx.ResourcefulLabel;

/**
 * A demo {@link LocalizablePopup} shown in the Widgets tab of {@link DemoStage}.
 * Its content (title, message, and close button) updates automatically when the locale changes.
 */
public class DemoPopup extends LocalizablePopup
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
    }

    /**
     * Create and initialize a new DemoPopup with the given initial locale.
     * The locale is set before the UI is built so all components start with the correct locale.
     *
     * @param initialLocale The initial locale for this popup.
     * @return A DemoPopup ready to be shown.
     */
    public static DemoPopup create(Locale initialLocale)
    {
        DemoPopup popup = new DemoPopup();
        popup.setBundleLocale(initialLocale);
        popup.buildUI();
        return popup;
    }

    private DemoPopup()
    {
    }

    @Override
    public Locale[] getAvailableLocales()
    {
        return FXLogger.FX_LOGGER.getAvailableLocales();
    }

    private void buildUI()
    {
        ResourcefulLabel titleLabel = ResourcefulLabel.create(new Resource(this, "demoPopupTitleProps"));
        titleLabel.setStyle("-fx-font-weight: bold;");

        ResourcefulLabel messageLabel = ResourcefulLabel.create(new Resource(this, "demoPopupMessageProps"));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);

        Separator separator = new Separator(Orientation.HORIZONTAL);

        ResourcefulButton closeButton = ResourcefulButton.create(new Resource(this, "demoPopupCloseButtonProps"));
        closeButton.setOnAction(e -> hide());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox buttonRow = new HBox(spacer, closeButton);

        VBox content = new VBox(8, titleLabel, separator, messageLabel, buttonRow);
        content.setPadding(new Insets(12));
        content.setMinWidth(300);
        content.setStyle("-fx-background-color: -fx-background; -fx-border-color: -fx-box-border; -fx-border-width: 1;");

        setAutoHide(true);
        getContent().add(content);
    }
}
