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

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.LocalizableStage;
import dev.javai18n.fx.ResourcefulButton;
import dev.javai18n.fx.ResourcefulLabel;
import dev.javai18n.fx.ResourcefulTextArea;
import dev.javai18n.fx.ResourcefulTextField;

/**
 * FXML controller for the contact form demo.
 *
 * <p>FXMLLoader calls {@link #initialize()} after instantiating all components via their
 * public no-arg constructors. At that point every {@code Resourceful*} field exists in the
 * scene graph but has no locale source. {@link #postLoad(LocalizableStage)} must be called
 * immediately after {@code FXMLLoader.load()} to complete the second phase: it calls
 * {@code component.initialize(Resource)} on each Resourceful field, binding them to the
 * stage as their locale-event source.</p>
 */
public class FXMLContactController implements Localizable.LocaleEventListener
{
    // Resourceful components declared in FXML — instantiated by FXMLLoader via no-arg
    // constructors. Not usable until postLoad() calls initialize(Resource) on each one.
    @FXML private ResourcefulLabel     titleLabel;
    @FXML private ResourcefulLabel     givenNameLabel;
    @FXML private ResourcefulTextField givenNameField;
    @FXML private ResourcefulLabel     familyNameLabel;
    @FXML private ResourcefulTextField familyNameField;
    @FXML private ResourcefulLabel     messageLabel;
    @FXML private ResourcefulTextArea  messageArea;
    @FXML private ResourcefulLabel     languageLabel;
    @FXML private ResourcefulButton    submitButton;
    @FXML private ResourcefulButton    clearButton;
    @FXML private ResourcefulButton    closeButton;

    // Plain ChoiceBox — locale selection drives setBundleLocale(), so its own text
    // must not be subject to locale change.
    @FXML private ChoiceBox<String> localeChoice;

    private LocalizableStage source;
    private Locale[] sortedLocales;
    private boolean updatingLocaleChoice = false;

    /**
     * Called by FXMLLoader after component instantiation.
     * Resourceful components exist but have no Resource yet; do not use them here.
     */
    @FXML
    public void initialize()
    {
    }

    /**
     * Complete initialization by wiring all Resourceful components to the given source
     * and populating the locale selector. Must be called once immediately after
     * {@code FXMLLoader.load()}, before the stage is shown.
     *
     * @param source The LocalizableStage that owns this form.
     */
    public void postLoad(LocalizableStage source)
    {
        this.source = source;

        titleLabel.initialize(new Resource(source,      "titleLabelProps"));
        givenNameLabel.initialize(new Resource(source,  "givenNameLabelProps"));
        givenNameField.initialize(new Resource(source,  "givenNameFieldProps"));
        familyNameLabel.initialize(new Resource(source, "familyNameLabelProps"));
        familyNameField.initialize(new Resource(source, "familyNameFieldProps"));
        messageLabel.initialize(new Resource(source,    "messageLabelProps"));
        messageArea.initialize(new Resource(source,     "messageAreaProps"));
        languageLabel.initialize(new Resource(source,   "languageLabelProps"));
        submitButton.initialize(new Resource(source,    "submitButtonProps"));
        clearButton.initialize(new Resource(source,     "clearButtonProps"));
        closeButton.initialize(new Resource(source,     "closeButtonProps"));

        buildLocaleChoiceItems(source.getBundleLocale());
        localeChoice.getSelectionModel().selectedIndexProperty().addListener(
            (obs, old, idx) ->
            {
                if (!updatingLocaleChoice)
                    source.setBundleLocale(sortedLocales[idx.intValue()]);
            });
        source.addLocaleEventListener(this);
    }

    @Override
    public void processLocaleEvent(Localizable.LocaleEvent event)
    {
        Locale newLocale = event.getLocalizableSource().getBundleLocale();
        Platform.runLater(() -> buildLocaleChoiceItems(newLocale));
    }

    private void buildLocaleChoiceItems(Locale current)
    {
        List<Locale> sorted = new ArrayList<>(Arrays.asList(source.getAvailableLocales()));
        Collator collator = Collator.getInstance(current);
        sorted.sort((a, b) -> collator.compare(localeDisplayText(a, current), localeDisplayText(b, current)));
        sortedLocales = sorted.toArray(new Locale[0]);

        updatingLocaleChoice = true;
        try
        {
            localeChoice.getItems().clear();
            for (Locale locale : sortedLocales)
                localeChoice.getItems().add(localeDisplayText(locale, current));
            localeChoice.getSelectionModel().select(findBestMatchIndex(sortedLocales, current));
        }
        finally
        {
            updatingLocaleChoice = false;
        }
    }

    private static String localeDisplayText(Locale locale, Locale current)
    {
        if (Locale.ROOT.equals(locale)) return "ROOT";
        return locale.getDisplayName(current) + " [" + locale.getDisplayName(locale) + "]";
    }

    /**
     * Returns the index of the best match in {@code locales} for {@code target}:
     * exact match first, then language+country, then language-only, finally 0 (ROOT).
     */
    private static int findBestMatchIndex(Locale[] locales, Locale target)
    {
        int best = 0;
        int bestScore = 0;
        for (int i = 0; i < locales.length; i++)
        {
            Locale l = locales[i];
            if (l.equals(target)) return i;
            if (!l.getLanguage().isEmpty() && l.getLanguage().equals(target.getLanguage()))
            {
                int score = l.getCountry().equals(target.getCountry()) ? 2 : 1;
                if (score > bestScore)
                {
                    best = i;
                    bestScore = score;
                }
            }
        }
        return best;
    }

    @FXML
    private void handleSubmit()
    {
        String given  = givenNameField.getText().trim();
        String family = familyNameField.getText().trim();
        String body   = (given.isEmpty() && family.isEmpty())
            ? source.getResourceBundle().getString("SubmittedText")
            : new MessageFormat(
                    source.getResourceBundle().getString("SubmittedNameFormat"),
                    source.getBundleLocale())
                .format(new Object[]{ given, family });

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(source);
        alert.setTitle(source.getResourceBundle().getString("SubmittedTitle"));
        alert.setHeaderText(null);
        alert.setContentText(body);
        alert.getButtonTypes().setAll(
            new ButtonType(source.getResourceBundle().getString("OkButtonText")));
        alert.showAndWait();
    }

    @FXML
    private void handleClear()
    {
        givenNameField.clear();
        familyNameField.clear();
        messageArea.clear();
    }

    @FXML
    private void handleClose()
    {
        source.hide();
    }
}
