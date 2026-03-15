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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import dev.javai18n.fx.FXLogger;
import dev.javai18n.fx.LocalizableStage;

/**
 * A contact-form window whose UI is defined entirely in FXML.
 *
 * <p>This stage demonstrates the two-phase FXML initialization pattern for i18n-fx
 * Resourceful components:</p>
 * <ol>
 *   <li>{@link FXMLLoader} instantiates each {@code Resourceful*} component declared in
 *       {@code FXMLContact.fxml} via its public no-arg constructor. At this point the
 *       components exist in the scene graph but have no locale source, and will throw
 *       {@link IllegalStateException} if added to a scene before the next step.</li>
 *   <li>{@link FXMLContactController#postLoad(LocalizableStage)} is called immediately
 *       after {@code FXMLLoader.load()} returns. It calls
 *       {@code component.initialize(new Resource(stage, key))} on every Resourceful
 *       component, wiring each one to this stage as its locale-event source.</li>
 * </ol>
 *
 * <p>After that point the components behave identically to those created via
 * {@code ResourcefulXxx.create(resource)}: they respond to locale changes fired by
 * {@link #setBundleLocale(Locale)} and update their text, prompt, and tooltip on the
 * JavaFX Application Thread.</p>
 */
public class FXMLContactStage extends LocalizableStage
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
    }

    /**
     * Create a new FXMLContactStage.
     * Loads {@code FXMLContact.fxml}, completes Resourceful component initialization
     * via {@link FXMLContactController#postLoad(LocalizableStage)}, and sets the scene.
     * Must be called on the JavaFX Application Thread.
     *
     * @return A fully initialized FXMLContactStage.
     * @throws UncheckedIOException if the FXML file cannot be loaded.
     */
    public static FXMLContactStage create()
    {
        try
        {
            FXMLContactStage stage = new FXMLContactStage();

            FXMLLoader loader = new FXMLLoader(
                FXMLContactStage.class.getResource("FXMLContact.fxml"));
            Parent root = loader.load();

            // Phase 2: wire every Resourceful component to this stage as its locale source.
            FXMLContactController controller = loader.getController();
            controller.postLoad(stage);

            stage.setScene(new Scene(root, 540, 420));
            stage.updateLocaleSpecificValues();
            return stage;
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    protected FXMLContactStage()
    {
    }

    /**
     * Returns the same locale set used by the main demo application.
     */
    @Override
    public Locale[] getAvailableLocales()
    {
        return FXLogger.FX_LOGGER.getAvailableLocales();
    }
}
