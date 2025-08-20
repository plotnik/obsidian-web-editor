package io.github.plotnik.obsidian_web_editor;

// A simple class to map the incoming JSON body for the PUT request.
public class UpdateRequest {
    private String widget;
    private String value;

    // Getters and setters are required for JSON deserialization
    public String getWidget() {
        return widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}