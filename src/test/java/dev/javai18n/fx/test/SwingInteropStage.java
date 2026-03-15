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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.FXLogger;
import dev.javai18n.fx.LocalizableStage;
import dev.javai18n.fx.ResourcefulButton;
import dev.javai18n.fx.ResourcefulLabel;
import dev.javai18n.swing.ResourcefulJButton;
import dev.javai18n.swing.ResourcefulJLabel;
import dev.javai18n.swing.SwingModuleRegistrar;

/**
 * Demonstrates Swing ↔ JavaFX interoperability using i18n-fx and i18n-swing together.
 *
 * <p>Two embedding directions are shown:</p>
 * <ol>
 *   <li><b>Swing in JavaFX</b> — A {@link SwingNode} hosts a Swing {@code JPanel} containing a
 *       {@code ResourcefulJLabel} and a {@code ResourcefulJButton} from i18n-swing. Both are
 *       wired to this stage as their locale-event source and update automatically when the
 *       locale changes.</li>
 *   <li><b>JavaFX in Swing</b> — Clicking "Open Swing Frame" opens a Swing {@code JFrame}
 *       containing a {@link JFXPanel} that hosts a {@code ResourcefulLabel} and a
 *       {@code ResourcefulButton} from i18n-fx. The Swing frame's locale selector drives
 *       {@link #setBundleLocale(Locale)} on this stage, so all components — FX and Swing,
 *       embedded and host — update together from a single {@link dev.javai18n.core.Localizable}
 *       source.</li>
 * </ol>
 */
public class SwingInteropStage extends LocalizableStage
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
        SwingModuleRegistrar.ensureRegistered();
    }

    // FX locale selector
    private ChoiceBox<String> localeChoice;
    private volatile Locale[] sortedLocales;
    private boolean updatingLocaleChoice = false;

    // Swing frame state (null when not open)
    private JFrame swingFrame;
    private JComboBox<String> swingLocaleChoice;
    private boolean updatingSwingLocaleChoice = false;

    /**
     * Create and return a fully initialized SwingInteropStage.
     * Must be called on the JavaFX Application Thread.
     */
    public static SwingInteropStage create()
    {
        SwingInteropStage stage = new SwingInteropStage();
        stage.buildUI();
        stage.updateLocaleSpecificValues();
        return stage;
    }

    protected SwingInteropStage() {}

    private void buildUI()
    {
        // Language selector row
        ResourcefulLabel languageLabel = ResourcefulLabel.create(new Resource(this, "languageLabelProps"));
        localeChoice = new ChoiceBox<>();
        localeChoice.getSelectionModel().selectedIndexProperty().addListener(
            (obs, old, idx) ->
            {
                if (!updatingLocaleChoice)
                    setBundleLocale(sortedLocales[idx.intValue()]);
            });
        HBox langRow = new HBox(8, languageLabel, localeChoice);
        langRow.setAlignment(Pos.CENTER_LEFT);

        // "Swing in FX" section
        ResourcefulLabel swingInFxLabel = ResourcefulLabel.create(new Resource(this, "swingInFxSectionProps"));
        swingInFxLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> buildSwingNodeContent(swingNode));

        // "FX in Swing" section
        ResourcefulLabel fxInSwingLabel = ResourcefulLabel.create(new Resource(this, "fxInSwingSectionProps"));
        fxInSwingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        ResourcefulButton openButton = ResourcefulButton.create(new Resource(this, "openSwingFrameButtonProps"));
        openButton.setOnAction(e -> handleOpenSwingFrame());

        ResourcefulButton closeButton = ResourcefulButton.create(new Resource(this, "closeButtonProps"));
        closeButton.setOnAction(e -> hide());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bottomRow = new HBox(spacer, closeButton);

        VBox root = new VBox(12,
            langRow,
            new Separator(Orientation.HORIZONTAL),
            swingInFxLabel,
            swingNode,
            new Separator(Orientation.HORIZONTAL),
            fxInSwingLabel,
            openButton,
            new Separator(Orientation.HORIZONTAL),
            bottomRow);
        root.setPadding(new Insets(16));

        setScene(new Scene(root, 520, 400));
    }

    /** Builds the Swing content inside the SwingNode. Must be called on the EDT. */
    private void buildSwingNodeContent(SwingNode swingNode)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.add(ResourcefulJLabel.create(new Resource(this, "swingLabelProps")));
        panel.add(ResourcefulJButton.create(new Resource(this, "swingButtonProps")));
        swingNode.setContent(panel);
    }

    private void handleOpenSwingFrame()
    {
        if (swingFrame != null)
        {
            SwingUtilities.invokeLater(() -> { if (swingFrame != null) swingFrame.toFront(); });
            return;
        }
        SwingUtilities.invokeLater(this::openSwingFrame);
    }

    /** Creates and shows the Swing JFrame with the embedded JFXPanel. Must be called on the EDT. */
    private void openSwingFrame()
    {
        swingFrame = new JFrame(getResourceBundle().getString("SwingFrameTitle"));
        swingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        swingFrame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e)
            {
                swingFrame = null;
                swingLocaleChoice = null;
            }
        });

        // Language selector row
        swingLocaleChoice = new JComboBox<>();
        populateSwingLocaleChoice(getBundleLocale());
        swingLocaleChoice.addActionListener(e ->
        {
            if (!updatingSwingLocaleChoice)
            {
                int idx = swingLocaleChoice.getSelectedIndex();
                Locale[] locales = sortedLocales;
                if (locales != null && idx >= 0 && idx < locales.length)
                    setBundleLocale(locales[idx]);
            }
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.add(ResourcefulJLabel.create(new Resource(this, "swingLanguageLabelProps")));
        topPanel.add(swingLocaleChoice);

        // Center: Swing label above JFXPanel
        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(480, 60));
        Platform.runLater(() -> buildJFXPanelContent(jfxPanel));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.add(ResourcefulJLabel.create(new Resource(this, "swingFrameLabelProps")), BorderLayout.NORTH);
        centerPanel.add(jfxPanel, BorderLayout.CENTER);

        ResourcefulJButton swingCloseButton = ResourcefulJButton.create(new Resource(this, "swingFrameCloseButtonProps"));
        swingCloseButton.addActionListener(e -> swingFrame.dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        southPanel.add(swingCloseButton);

        swingFrame.add(topPanel, BorderLayout.NORTH);
        swingFrame.add(centerPanel, BorderLayout.CENTER);
        swingFrame.add(southPanel, BorderLayout.SOUTH);
        swingFrame.pack();
        swingFrame.setMinimumSize(new Dimension(440, 200));
        swingFrame.setLocationRelativeTo(null);
        swingFrame.setVisible(true);
    }

    /** Builds the FX content inside the JFXPanel. Must be called on the FX thread. */
    private void buildJFXPanelContent(JFXPanel jfxPanel)
    {
        HBox content = new HBox(8,
            ResourcefulLabel.create(new Resource(this, "embeddedFxLabelProps")),
            ResourcefulButton.create(new Resource(this, "embeddedFxButtonProps")));
        content.setPadding(new Insets(8));
        content.setAlignment(Pos.CENTER_LEFT);
        jfxPanel.setScene(new Scene(content));
    }

    @Override
    protected void updateLocaleSpecificValues()
    {
        super.updateLocaleSpecificValues();
        Locale current = getBundleLocale();
        repopulateLocaleChoice(current);
        if (swingFrame != null)
        {
            SwingUtilities.invokeLater(() ->
            {
                if (swingFrame != null)
                {
                    swingFrame.setTitle(getResourceBundle().getString("SwingFrameTitle"));
                    populateSwingLocaleChoice(current);
                }
            });
        }
    }

    private void repopulateLocaleChoice(Locale current)
    {
        if (localeChoice == null) return;
        List<Locale> sorted = new ArrayList<>(Arrays.asList(getAvailableLocales()));
        Collator collator = Collator.getInstance(current);
        sorted.sort((a, b) -> collator.compare(localeLabel(a, current), localeLabel(b, current)));
        sortedLocales = sorted.toArray(new Locale[0]);

        updatingLocaleChoice = true;
        try
        {
            localeChoice.getItems().clear();
            for (Locale l : sortedLocales)
                localeChoice.getItems().add(localeLabel(l, current));
            localeChoice.getSelectionModel().select(findBestMatchIndex(sortedLocales, current));
        }
        finally
        {
            updatingLocaleChoice = false;
        }
    }

    /** Populates the Swing JComboBox locale selector. Must be called on the EDT. */
    private void populateSwingLocaleChoice(Locale current)
    {
        if (swingLocaleChoice == null || sortedLocales == null) return;
        updatingSwingLocaleChoice = true;
        try
        {
            swingLocaleChoice.removeAllItems();
            for (Locale l : sortedLocales)
                swingLocaleChoice.addItem(localeLabel(l, current));
            swingLocaleChoice.setSelectedIndex(findBestMatchIndex(sortedLocales, current));
        }
        finally
        {
            updatingSwingLocaleChoice = false;
        }
    }

    private static String localeLabel(Locale locale, Locale current)
    {
        if (Locale.ROOT.equals(locale)) return "ROOT";
        return locale.getDisplayName(current) + " [" + locale.getDisplayName(locale) + "]";
    }

    private static int findBestMatchIndex(Locale[] locales, Locale target)
    {
        int best = 0, bestScore = 0;
        for (int i = 0; i < locales.length; i++)
        {
            Locale l = locales[i];
            if (l.equals(target)) return i;
            if (!l.getLanguage().isEmpty() && l.getLanguage().equals(target.getLanguage()))
            {
                int score = l.getCountry().equals(target.getCountry()) ? 2 : 1;
                if (score > bestScore) { best = i; bestScore = score; }
            }
        }
        return best;
    }

    @Override
    public Locale[] getAvailableLocales()
    {
        return FXLogger.FX_LOGGER.getAvailableLocales();
    }
}
