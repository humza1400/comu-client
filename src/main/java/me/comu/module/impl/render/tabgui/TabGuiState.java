package me.comu.module.impl.render.tabgui;

import me.comu.module.Category;
import me.comu.property.Property;
import me.comu.property.properties.ListProperty;

import java.util.ArrayList;
import java.util.List;

public class TabGuiState {
    public boolean visible = true;
    public boolean mainMenu = true;
    public boolean showProperties = false;
    public boolean inPropertyFocus = false;
    public boolean insideListProperty = false;

    public int selectedTab = 0;
    public int selectedModule = 0;
    public int selectedProperty = 0;
    public int selectedListProperty = 0;

    public float categoryTransition = 0f;
    public float targetCategoryTransition = 0f;
    public float moduleTransition = 0f;
    public float targetModuleTransition = 0f;
    public float propertyTransition = 0f;
    public float targetPropertyTransition = 0f;
    public float listPropertyTransition = 0f;
    public float targetListPropertyTransition = 0f;


    public List<Category> categories = Category.getToggleCategories();
    public ListProperty currentList = null;
    public List<Property<?>> currentListProperties = List.of();
    public List<ListProperty> parentListStack = new ArrayList<>();

    public Category getCurrentCategory() {
        return categories.get(selectedTab);
    }
}
