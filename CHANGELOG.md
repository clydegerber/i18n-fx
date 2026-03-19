# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.0.1] - 2026-03-19

### Changed

- Demo app: Sort By Size now sorts files and directories together by numeric
  size rather than grouping directories before files
- Demo app: Sort By Type added to the View > Sort By menu (sorts by file
  extension; was previously only available by clicking the Type column header)
- Demo app: column header clicks and View menu sort selections now use the
  same sort logic and keep each other's state in sync; the column sort
  indicator arrow always reflects the active sort

## [1.0] - 2026-03-15

### Added

- `LocalizableStage` — locale-event source extending `Stage`; fires locale events on the JavaFX Application Thread
- `LocalizablePopup` — locale-event source extending `Popup`; `updateLocaleSpecificValues()` is a no-op in the base class — subclasses override it to apply locale-specific content to nodes placed inside the popup
- `ResourcefulButton` — localizes button text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulCheckBox` — localizes check box text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulRadioButton` — localizes radio button text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulToggleButton` — localizes toggle button text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulHyperlink` — localizes hyperlink text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulLabel` — localizes label text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulTextField` — localizes text field prompt text, tooltip, style, and accessibility attributes
- `ResourcefulPasswordField` — localizes password field prompt text, tooltip, style, and accessibility attributes
- `ResourcefulTextArea` — localizes text area prompt text, tooltip, style, and accessibility attributes
- `ResourcefulComboBox` — localizes combo box prompt text, item values, tooltip, style, and accessibility attributes
- `ResourcefulChoiceBox` — localizes choice box item values, tooltip, style, and accessibility attributes
- `ResourcefulListView` — localizes list view tooltip, style, and accessibility attributes
- `ResourcefulTableView` — localizes table view tooltip, style, and accessibility attributes
- `ResourcefulTreeView` — localizes tree view tooltip, style, and accessibility attributes
- `ResourcefulTreeTableView` — localizes tree table view tooltip, style, and accessibility attributes
- `ResourcefulSpinner` — localizes spinner tooltip, style, and accessibility attributes
- `ResourcefulSlider` — localizes slider tooltip, style, and accessibility attributes
- `ResourcefulProgressBar` — localizes progress bar tooltip, style, and accessibility attributes
- `ResourcefulProgressIndicator` — localizes progress indicator tooltip, style, and accessibility attributes
- `ResourcefulScrollBar` — localizes scroll bar tooltip, style, and accessibility attributes
- `ResourcefulScrollPane` — localizes scroll pane tooltip, style, and accessibility attributes
- `ResourcefulSplitPane` — localizes split pane tooltip, style, and accessibility attributes
- `ResourcefulTabPane` — localizes tab pane tooltip, style, and accessibility attributes
- `ResourcefulTitledPane` — localizes titled pane title text, graphic, tooltip, style, and accessibility attributes
- `ResourcefulAccordion` — localizes accordion tooltip, style, and accessibility attributes
- `ResourcefulMenuBar` — localizes menu bar tooltip, style, and accessibility attributes
- `ResourcefulToolBar` — localizes tool bar tooltip, style, and accessibility attributes
- `ResourcefulSeparator` — localizes separator tooltip, style, and accessibility attributes
- `ResourcefulPagination` — localizes pagination tooltip, style, and accessibility attributes
- `ResourcefulHTMLEditor` — localizes HTML editor tooltip, style, and accessibility attributes
- `ResourcefulMenuItem` — localizes menu item text and graphic
- `ResourcefulMenu` — localizes menu text and graphic
- `ResourcefulCheckMenuItem` — localizes check menu item text and graphic
- `ResourcefulRadioMenuItem` — localizes radio menu item text and graphic
- `ResourcefulTab` — localizes tab title text, graphic, and tooltip
- `ResourcefulTableColumn` — localizes table column header text and graphic
- `ResourcefulTooltip` — localizes tooltip text
- `NodePropertyBundle` — base `AttributeCollection` for name, style, and accessibility properties
- `ControlPropertyBundle` — extends `NodePropertyBundle` with tooltip text
- `LabeledPropertyBundle` — extends `ControlPropertyBundle` with text and graphic
- `ButtonBasePropertyBundle` — extends `LabeledPropertyBundle` (type marker)
- `TextInputPropertyBundle` — extends `ControlPropertyBundle` with prompt text
- `ComboBoxPropertyBundle` — extends `ControlPropertyBundle` with prompt text and item values
- `ChoiceBoxPropertyBundle` — extends `NodePropertyBundle` with item values
- `TitledPanePropertyBundle` — extends `ControlPropertyBundle` with title text and graphic
- `StagePropertyBundle` — extends `NodePropertyBundle` with title and icon images
- `MenuItemPropertyBundle` — `AttributeCollection` for menu item text and graphic
- `TabPropertyBundle` — `AttributeCollection` for tab title text, graphic, and tooltip
- `TableColumnPropertyBundle` — `AttributeCollection` for table column header text and graphic
- `TooltipPropertyBundle` — `AttributeCollection` for tooltip text
- Classpath and module-path test profiles
- Release profile with javadoc and source JAR generation

### Changed

- Updated `i18n-swing` dependency from 1.2.1 to 1.2.2 (test scope)
- Demo app: `AppStage.getAvailableLocales()` now delegates to
  `FXLogger.FX_LOGGER.getAvailableLocales()` instead of maintaining a
  separate hard-coded locale array

### Fixed

- Test factory methods (`TestComponentSource.create()`, `AppStage.create()`)
  now call `setBundleLocale(Locale.ROOT)` immediately after construction so
  that `testInitialProperties` assertions pass regardless of the JVM default
  locale
- `TestDemoStage.testDemoStageJsonBundle()` now passes `Locale.ROOT` explicitly
  to `ResourceBundle.getBundle()` before asserting English string values
