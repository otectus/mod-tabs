package vodmordia.modtabs.config;

import com.google.gson.annotations.SerializedName;

/**
 * Data model for custom tab definitions loaded from JSON files
 */
public class CustomTabDefinition {

    @SerializedName("tabId")
    public String tabId;

    @SerializedName("enabled")
    public boolean enabled = true;

    @SerializedName("icon")
    public IconDefinition icon;

    @SerializedName("tooltip")
    public String tooltip;

    @SerializedName("order")
    public int order = 100;

    @SerializedName("action")
    public ActionDefinition action;

    @SerializedName("requiredMods")
    public String[] requiredMods = new String[0];

    public static class IconDefinition {
        @SerializedName("item")
        public String item;

        @SerializedName("fallbackItem")
        public String fallbackItem = "minecraft:book";

        @SerializedName("scale")
        public float scale = 1.0f;

        @SerializedName("patchouliBook")
        public String patchouliBook;
    }

    public static class ActionDefinition {
        @SerializedName("type")
        public String type = "use_item";

        @SerializedName("item")
        public String item;

        @SerializedName("screenClass")
        public String screenClass;

        @SerializedName("command")
        public String command;

        @SerializedName("keybind")
        public String keybind;

        @SerializedName("bookId")
        public String bookId;
    }

    /**
     * Validates the tab definition for required fields
     */
    public boolean isValid() {
        if (tabId == null || tabId.trim().isEmpty()) {
            return false;
        }

        if (icon == null || icon.item == null || icon.item.trim().isEmpty()) {
            return false;
        }

        if (action == null || action.type == null || action.type.trim().isEmpty()) {
            return false;
        }

        // Validate action has required parameters based on type
        switch (action.type) {
            case "use_item":
                return action.item != null && !action.item.trim().isEmpty();
            case "open_screen":
                return action.screenClass != null && !action.screenClass.trim().isEmpty();
            case "run_command":
                return action.command != null && !action.command.trim().isEmpty();
            case "keybind":
                return action.keybind != null && !action.keybind.trim().isEmpty();
            case "open_patchouli_book":
                return action.bookId != null && !action.bookId.trim().isEmpty();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "CustomTabDefinition{" +
                "tabId='" + tabId + '\'' +
                ", enabled=" + enabled +
                ", tooltip='" + tooltip + '\'' +
                ", order=" + order +
                '}';
    }
}