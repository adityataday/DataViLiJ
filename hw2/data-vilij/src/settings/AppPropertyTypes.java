package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    GUI_RESOURCE_PATH,
    CSS_RESOURCE_PATH,
    CSS_RESOURCE_FILENAME,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    INCORRECT_FILE_EXTENSION_DATA,
    INCORRECT_FILE_EXTENSION_IMAGE,
    LABEL_ALREADY_EXISTS,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,
    LOAD_DATA,
    TO_MANY_LINES,
    TO_MANY_LINES_MSG_1,
    TO_MANY_LINES_MSG_2,
    SAVE_IMAGE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    IMAGE_FILE_EXT,
    DATA_FILE_EXT_DESC,
    IMAGE_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    LEFT_PANE_TITLE,
    LEFT_PANE_TITLEFONT,
    LEFT_PANE_TITLESIZE,
    CHART_TITLE,
    DISPLAY_BUTTON_TEXT,
    CHECKBOX_LABEL
}
