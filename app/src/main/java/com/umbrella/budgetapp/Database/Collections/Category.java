package com.umbrella.budgetapp.Database.Collections;

public class Category {
    public static final String COLLECTION = "category";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String ICON = "icon";

    private String Name;
    private int Icon;
    private int Color;

    public Category() {
        //Empty constructor needed.
    }

    public Category(String name, int icon, int color) {
        Name = name;
        Icon = icon;
        Color = color;
    }

    public String getName() {
        return Name;
    }

    public int getIcon() {
        return Icon;
    }

    public int getColor() {
        return Color;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public void setColor(int color) {
        Color = color;
    }
}
