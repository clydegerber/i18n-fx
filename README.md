# i18n-fx

JavaFX bindings for the [i18n-core](https://github.com/clydegerber/i18n) internationalization framework.

This library provides locale-aware JavaFX components that automatically update
their text, graphics, styles, tooltips, and accessibility attributes
when the application locale changes.

## Requirements

- Java 21 or later
- [i18n-core](https://github.com/clydegerber/i18n) 1.4.0 or later (pulled in transitively)

## Installation

### Maven

```xml
<dependency>
    <groupId>dev.javai18n</groupId>
    <artifactId>i18n-fx</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Module Declaration

```java
module my.module
{
    requires dev.javai18n.fx;
}
```

## Components

### Localizable top-level containers

These classes serve as the locale-event source for all Resourceful components they own.
They implement `Localizable` and dispatch locale events on the JavaFX Application Thread
when `setBundleLocale()` is called.

| Class | Extends | Notes |
|---|---|---|
| `LocalizableStage` | `Stage` | All top-level windows; bundle key `"stageProperties"` uses `StagePropertyBundle`. Covers standard windows, modal dialogs (via `initModality()`), and undecorated windows (via `initStyle()`). |
| `LocalizablePopup` | `Popup` | Floating unowned window; `updateLocaleSpecificValues()` is a no-op in the base class — subclass it to add locale-specific popup content. |

### Resourceful components

All Resourceful components implement `Resourceful` and `LocaleEventListener`.
Each receives an `updateLocaleSpecificValues()` call on the JavaFX Application Thread
whenever its source fires a locale change.

The base set of localized properties for every Resourceful `Node` subclass is:
**name, style (CSS), accessible text, accessible help, accessible role description**
(from `NodePropertyBundle`). Additional properties are noted in the table below.

**Buttons and toggles**

| Class | Extends | Additional Localized Properties |
|---|---|---|
| `ResourcefulButton` | `Button` | text, graphic, tooltip |
| `ResourcefulCheckBox` | `CheckBox` | text, graphic, tooltip |
| `ResourcefulRadioButton` | `RadioButton` | text, graphic, tooltip |
| `ResourcefulToggleButton` | `ToggleButton` | text, graphic, tooltip |
| `ResourcefulHyperlink` | `Hyperlink` | text, graphic, tooltip |

**Labels**

| Class | Extends | Additional Localized Properties |
|---|---|---|
| `ResourcefulLabel` | `Label` | text, graphic, tooltip |

**Text input**

| Class | Extends | Additional Localized Properties |
|---|---|---|
| `ResourcefulTextField` | `TextField` | prompt text, tooltip |
| `ResourcefulPasswordField` | `PasswordField` | prompt text, tooltip |
| `ResourcefulTextArea` | `TextArea` | prompt text, tooltip |

**Data and selection**

| Class | Extends | Additional Localized Properties |
|---|---|---|
| `ResourcefulComboBox` | `ComboBox` | prompt text, item values (string array), tooltip |
| `ResourcefulChoiceBox` | `ChoiceBox` | item values (string array) |
| `ResourcefulListView` | `ListView` | tooltip |
| `ResourcefulTableView` | `TableView` | tooltip |
| `ResourcefulTreeView` | `TreeView` | tooltip |
| `ResourcefulTreeTableView` | `TreeTableView` | tooltip |
| `ResourcefulSpinner` | `Spinner` | tooltip |
| `ResourcefulSlider` | `Slider` | tooltip |
| `ResourcefulProgressBar` | `ProgressBar` | tooltip |
| `ResourcefulProgressIndicator` | `ProgressIndicator` | tooltip |
| `ResourcefulScrollBar` | `ScrollBar` | tooltip |

**Panes and containers**

| Class | Extends | Additional Localized Properties |
|---|---|---|
| `ResourcefulScrollPane` | `ScrollPane` | tooltip |
| `ResourcefulSplitPane` | `SplitPane` | tooltip |
| `ResourcefulTabPane` | `TabPane` | tooltip |
| `ResourcefulTitledPane` | `TitledPane` | title text, graphic, tooltip |
| `ResourcefulAccordion` | `Accordion` | tooltip |
| `ResourcefulMenuBar` | `MenuBar` | tooltip |
| `ResourcefulToolBar` | `ToolBar` | tooltip |
| `ResourcefulSeparator` | `Separator` | tooltip |
| `ResourcefulPagination` | `Pagination` | tooltip |
| `ResourcefulHTMLEditor` | `HTMLEditor` | tooltip |

**Non-Node components** (MenuItem, Tab, TableColumn, Tooltip, ContextMenu do not extend Node)

| Class | Extends | Localized Properties |
|---|---|---|
| `ResourcefulMenuItem` | `MenuItem` | text, graphic |
| `ResourcefulMenu` | `Menu` | text, graphic |
| `ResourcefulCheckMenuItem` | `CheckMenuItem` | text, graphic |
| `ResourcefulRadioMenuItem` | `RadioMenuItem` | text, graphic |
| `ResourcefulTab` | `Tab` | title text, graphic, tooltip |
| `ResourcefulTableColumn` | `TableColumn` | header text, graphic |
| `ResourcefulTooltip` | `Tooltip` | text |

## Omitted Components

`ColorPicker` and `DatePicker` are intentionally absent from this library.

Both `ColorPickerSkin` and `DatePickerSkin` retrieve locale-sensitive text (the
"Custom Color…" dialog label and the calendar month/day-name headers, respectively)
by calling `Locale.getDefault()` internally. JavaFX provides no API to inject an
explicit locale into these skins.

The only way to force the skins to display text in a different locale is to call
`Locale.setDefault()`, which is a JVM-wide operation. Doing so from inside a library
would be an undocumented global side effect that could silently break any other
locale-sensitive code in the application — including `ResourceBundle` lookups,
`DateFormat`, `NumberFormat`, and third-party components. This library must not
call `Locale.setDefault()`.

Wrapping `ColorPicker` or `DatePicker` as Resourceful components would therefore
produce only partial localization: the tooltip and prompt text would follow the
bundle locale, but the skin's own text would continue to use the JVM default locale.
This inconsistency would be misleading and potentially confusing to users.

## Property Bundles

Property bundles are typed `AttributeCollection` implementations that carry the localized
values read from a JSON or XML resource file. No code generation or compilation step is needed
to add new bundle entries.

**Font** is stored as a CSS style string via the `Style` key (e.g.
`"-fx-font: bold 14pt Arial"`) and applied via `Node.setStyle()`, making locale-specific
fonts — such as CJK font families — straightforward to configure.

**Graphic** is stored as a path or URL string in the bundle and resolved at
`setAttribute()` time by `FXImageResourceLoader`. The getter returns a
`javafx.scene.image.Image`; the component wraps it in `new ImageView(image)`
before calling `setGraphic()`.

| Bundle | Extends | Fields |
|---|---|---|
| `NodePropertyBundle` | — | name, style (CSS), accessible text, accessible help, accessible role description |
| `ControlPropertyBundle` | `NodePropertyBundle` | + tooltip text |
| `LabeledPropertyBundle` | `ControlPropertyBundle` | + text, graphic |
| `ButtonBasePropertyBundle` | `LabeledPropertyBundle` | (type marker — no additional keys) |
| `TextInputPropertyBundle` | `ControlPropertyBundle` | + prompt text |
| `ComboBoxPropertyBundle` | `ControlPropertyBundle` | + prompt text, values (string array) |
| `ChoiceBoxPropertyBundle` | `NodePropertyBundle` | + values (string array) |
| `TitledPanePropertyBundle` | `ControlPropertyBundle` | + title text, graphic |
| `StagePropertyBundle` | `NodePropertyBundle` | + title, icon images (list) |
| `MenuItemPropertyBundle` | — | text, graphic |
| `TabPropertyBundle` | — | title text, graphic, tooltip text |
| `TableColumnPropertyBundle` | — | header text, graphic |
| `TooltipPropertyBundle` | — | text |

## Quick Start

### 1. Create a LocalizableStage subclass

```java
public class MyStage extends LocalizableStage
{
    static
    {
        MyModuleRegistrar.ensureRegistered();
    }

    @Override
    public Locale[] getAvailableLocales()
    {
        return new Locale[]{ Locale.ROOT, Locale.FRANCE };
    }
}
```

### 2. Define a resource bundle

**MyStageBundle.json:**

```json
{
    "okButton":
    {
        "type": "dev.javai18n.fx.ButtonBasePropertyBundle",
        "Text": "OK"
    }
}
```

**MyStageBundle_fr.json:**

```json
{
    "okButton":
    {
        "type": "dev.javai18n.fx.ButtonBasePropertyBundle",
        "Text": "D'accord"
    }
}
```

### 3. Create a Resourceful component

```java
MyStage myStage = MyStage.create();
Resource okResource = new Resource(myStage, "okButton");
ResourcefulButton okButton = ResourcefulButton.create(okResource);
```

The button displays the correct text and other properties for the current locale.
When `myStage.setBundleLocale(locale)` is called, every attached Resourceful component
updates itself on the JavaFX Application Thread.

## Sample Application

A complete file explorer example is provided in the test sources to demonstrate the library end to end.

**Main class:** `dev.javai18n.fx.test.TestFXApp`
**Stage class:** `dev.javai18n.fx.test.DemoStage`

Launch it via the `run-demo` Maven profile or the convenience script:

```bash
mvn test-compile -Prun-demo
```

```bash
./run-demo.sh
```

The application starts with the system locale and opens at the user's home directory.

### What it demonstrates

- **Locale switching** — Preferences > Locale menu lists the locales supported by the application
  logger by default, or all JVM locales when "All JVM Locales" is selected. Choosing a locale
  updates every menu item, toolbar button, tab label, table column header, tooltip, and status
  bar message simultaneously on the JavaFX Application Thread.
- **File system navigation** — a directory tree (`ResourcefulTreeView`) on the left and a file
  table (`ResourcefulTableView` with `ResourcefulTableColumn` headers) on the right, connected
  by Back, Forward, Up, and Home toolbar buttons (`ResourcefulButton`) and a path field
  (`ResourcefulTextField`).
- **Sortable file table** — sort by name, size, type, or date via View > Sort By radio menu
  items (`ResourcefulRadioMenuItem`); show or hide hidden files and extensions via
  View check menu items (`ResourcefulCheckMenuItem`).
- **Context menu** — right-clicking the file table opens a `ContextMenu` populated with
  localized `ResourcefulMenuItem` items.
- **Preview and Properties tabs** — preview a selected text file in a scroll pane
  (`ResourcefulScrollPane`) containing a text area (`ResourcefulTextArea`); a titled pane
  (`ResourcefulTitledPane`) shows file metadata with localized labels (`ResourcefulLabel`).
- **Widgets tab** — exercises the remaining component types: `ResourcefulPasswordField`,
  `ResourcefulTextField`, `ResourcefulSpinner`, `ResourcefulSlider` (with locale-formatted
  tick labels), `ResourcefulChoiceBox`, `ResourcefulComboBox`, `ResourcefulListView` (day-of-week
  names from `DateFormatSymbols`), `ResourcefulProgressBar`, `ResourcefulProgressIndicator`,
  `ResourcefulToggleButton`, and `ResourcefulPagination`.
- **FXML dialog** — File > Open FXML Dialog opens a contact form loaded from FXML
  (`FXMLContactStage`), demonstrating the FXML path where components are instantiated by
  `FXMLLoader` and initialized via controller injection.
- **Swing interoperability** — Preferences > Swing Interop Demo opens a window
  (`SwingInteropStage`) embedding a Swing panel inside a `SwingNode` and a JavaFX scene
  inside a `JFXPanel`, demonstrating cross-toolkit locale propagation.

All localized strings live in `DemoStageBundle.json` and its locale variants
(`DemoStageBundle_en.json`, `DemoStageBundle_fr.json`, etc.).

## Building

```bash
mvn clean package
```

To build with sources JAR, javadoc JAR, and GPG signing for release:

```bash
mvn -Prelease clean package
```

## Testing

To execute unit tests on the classpath (default):

```bash
mvn clean test
```

To execute unit tests under JPMS:

```bash
mvn clean test -Ptest-modulepath
```

### Default Locale and Surefire

`LocalizationDelegate` initializes its locale field from `Locale.getDefault()` at construction time.
Passing `-Duser.language=fr` to Maven on the command line does **not** change `Locale.getDefault()` in the forked Surefire JVM: Surefire propagates Maven `-D` system properties via `System.setProperty()` inside the already-running JVM, after the default locale has been initialized by the JVM startup sequence.

To ensure tests pass regardless of the OS default locale, every `create()` factory method in test-only `Localizable` classes (`TestComponentSource`, `AppStage`) calls `setBundleLocale(Locale.ROOT)` immediately after construction. Tests that call `ResourceBundle.getBundle()` and assert English string values pass `Locale.ROOT` explicitly rather than using the no-locale overload.

When writing new tests that assert string values from a bundle, always pin the locale explicitly (e.g. `setBundleLocale(Locale.ROOT)`) rather than relying on `Locale.getDefault()`.

## License

This project is licensed under the
[Apache License, Version 2.0](LICENSE).
