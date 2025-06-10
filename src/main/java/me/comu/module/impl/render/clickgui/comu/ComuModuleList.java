package me.comu.module.impl.render.clickgui.comu;


import me.comu.Comu;
import me.comu.module.Category;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;
import me.comu.module.impl.render.clickgui.comu.properties.EnumDropdown;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ComuModuleList {

    private final List<ComuModule> moduleList = new ArrayList<>();
    private final List<EnumDropdown> expandedDropdowns = new ArrayList<>();

    private Category selectedCategory = Category.COMBAT;

    private int baseX, baseY;
    private int maxWidth;
    private int scrollOffset = 0;

    private int boxWidth = 100, boxHeight = 30, spacing = 10;
    private static final int MAX_VISIBLE_HEIGHT = 292;

    private final Map<Category, Integer> categoryScrollMap = new HashMap<>();

    public void setBounds(int x, int y, int maxWidth) {
        this.baseX = x;
        this.baseY = y;
        this.maxWidth = maxWidth;
        if (moduleList.isEmpty()) {
            rebuild();
        }
    }

    public void setCategory(Category category) {
        if (category != selectedCategory) {
            categoryScrollMap.put(selectedCategory, scrollOffset);
            selectedCategory = category;
            scrollOffset = categoryScrollMap.getOrDefault(category, 0);
            rebuild();
        }
    }

    public void setFilteredModulesBySearch(String query) {
        moduleList.clear();

        int columnCount = 3;
        int initialOffsetY = 5;
        int[] columnHeights = new int[columnCount];
        Arrays.fill(columnHeights, initialOffsetY);

        String lowerQuery = query.toLowerCase();

        List<me.comu.module.Module> allModules = Comu.getInstance().getModuleManager().getRegistry();
        List<me.comu.module.Module> matchingModules = allModules.stream().filter(mod -> mod.getName().toLowerCase().contains(lowerQuery) || mod.getDisplayName().toLowerCase().contains(lowerQuery) || (mod.getAliases() != null && mod.getAliases().stream().anyMatch(alias -> alias.toLowerCase().contains(lowerQuery)))).toList();

        for (me.comu.module.Module module : matchingModules) {
            int bestColumn = 0;
            for (int i = 1; i < columnCount; i++) {
                if (columnHeights[i] < columnHeights[bestColumn]) {
                    bestColumn = i;
                }
            }

            int posX = bestColumn * (boxWidth + spacing);
            int posY = columnHeights[bestColumn];

            ComuModule comuModule = new ComuModule(null, module);
            comuModule.setPosition(posX, posY);
            comuModule.setWidth(boxWidth);
            comuModule.rebuildButtons();

            moduleList.add(comuModule);
            columnHeights[bestColumn] += comuModule.getHeight() + spacing;
        }
    }


    public void rebuild() {
        moduleList.clear();

        int columnCount = 3;
        int initialOffsetY = 5;
        int[] columnHeights = new int[columnCount];
        Arrays.fill(columnHeights, initialOffsetY);

        List<me.comu.module.Module> modules = Comu.getInstance().getModuleManager().getModules();
        modules.sort(Comparator.comparing(Module::getName));

        for (me.comu.module.Module module : modules) {
            boolean shouldAdd;
            if (selectedCategory == null) {
                shouldAdd = module instanceof ToggleableModule && ((ToggleableModule) module).isEnabled();
            } else {
                shouldAdd = module instanceof ToggleableModule && module.getCategory() == selectedCategory;
            }
            if (!shouldAdd) continue;

            int bestColumn = 0;
            for (int i = 1; i < columnCount; i++) {
                if (columnHeights[i] < columnHeights[bestColumn]) {
                    bestColumn = i;
                }
            }

            int posX = bestColumn * (boxWidth + spacing);
            int posY = columnHeights[bestColumn];

            ComuModule comuModule = new ComuModule(null, module);
            comuModule.setPosition(posX, posY);
            comuModule.setWidth(boxWidth);
            comuModule.rebuildButtons();

            moduleList.add(comuModule);
            columnHeights[bestColumn] += comuModule.getHeight() + spacing;
        }
    }

    public void draw(DrawContext context, int rawMouseX, int rawMouseY) {
        int guiMouseX = rawMouseX;
        int guiMouseY = rawMouseY;

        int totalContentHeight = getTotalContentHeight();
        int actualVisibleHeight = Math.min(MAX_VISIBLE_HEIGHT, totalContentHeight);

        int scissorX = baseX;
        int scissorY = baseY;
        int scissorW = maxWidth;
        int scissorH = actualVisibleHeight;

        context.enableScissor(scissorX, scissorY, scissorX + scissorW, scissorY + scissorH);

        expandedDropdowns.clear();

        for (ComuModule module : moduleList) {
            module.collectExpandedDropdowns(expandedDropdowns);
        }

        boolean hoverDisabled = expandedDropdowns.stream().anyMatch(dropdown -> dropdown.isHovered(guiMouseX, guiMouseY));

        for (ComuModule module : moduleList) {
            int drawX = baseX + module.getX();
            int drawY = baseY + module.getY() - scrollOffset;

            module.setHoverDisabled(hoverDisabled);
            module.drawAt(context, drawX, drawY, scrollOffset, baseY, baseY + actualVisibleHeight, guiMouseX, guiMouseY, expandedDropdowns);
            module.handleMouseDrag(rawMouseX, rawMouseY, drawX, drawY);
        }

        for (EnumDropdown dropdown : expandedDropdowns) {
            dropdown.drawOnTop(context, guiMouseX, guiMouseY);
        }

        context.disableScissor();

        if (totalContentHeight > MAX_VISIBLE_HEIGHT) {
            int maxScroll = totalContentHeight - MAX_VISIBLE_HEIGHT;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

            int thumbHeight = Math.max((int) ((float) MAX_VISIBLE_HEIGHT * MAX_VISIBLE_HEIGHT / totalContentHeight), 20);

            float scrollRatio = (float) scrollOffset / maxScroll;
            int scrollRange = MAX_VISIBLE_HEIGHT - thumbHeight;
            int thumbY = baseY + (int) (scrollRange * scrollRatio);

            int thumbX = baseX + maxWidth + 5;
            context.fill(thumbX, thumbY, thumbX + 1, thumbY + thumbHeight, 0x80FFFFFF);
        }
    }

    private int getTotalContentHeight() {
        int maxY = 0;
        for (ComuModule module : moduleList) {
            int bottomY = module.getY() + module.getHeight();
            if (bottomY > maxY) {
                maxY = bottomY;
            }
        }
        return maxY + 25;
    }

    public void handleScroll(int delta) {
        scrollOffset += delta * 60;
        int maxScroll = Math.max(0, getTotalContentHeight() - MAX_VISIBLE_HEIGHT);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        categoryScrollMap.put(selectedCategory, scrollOffset);
    }

    public boolean mouseClicked(double guiMouseX, double guiMouseY, int button) {
        for (EnumDropdown dropdown : expandedDropdowns) {
            if (dropdown.wasClickConsumed(guiMouseX, guiMouseY, button)) {
                dropdown.mouseClicked(guiMouseX, guiMouseY, button);
                return true;
            }
        }

        for (ComuModule module : moduleList) {
            int drawX = baseX + module.getX();
            int drawY = baseY + module.getY() - scrollOffset;
            if (module.mouseClicked(guiMouseX, guiMouseY, drawX, drawY, button)) {
                return true;
            }
        }

        return false;
    }

    public List<ComuModule> getModules() {
        return moduleList;
    }
}
