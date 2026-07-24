package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.container.ContainerManager;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A preview widget that renders a scaled inventory slot showing exactly how the
 * mod's shulker-box overlay looks with the current config values.
 * <p>
 * For the <b>icon overlay</b>, the widget delegates to the mod's own
 * {@link IconRenderer} (which triggers the wrapDrawItem mixin), so the preview
 * is pixel-perfect relative to real gameplay.
 * <p>
 * For the <b>capacity bar</b>, the widget delegates to the mod's own
 * {@link CapacityBarRenderer} which handles all four fill directions.
 * <p>
 * When {@link #setShowBundle(boolean)} is set to {@code true} (e.g. when the
 * user enables "Override Bundle Icon Position"), the preview switches from a
 * shulker box to a bundle item.
 */
public class PersistentPreviewWidget extends AbstractWidget {
    private final ConfigOptions config;
    private final int renderScale;
    private boolean showBundle = false;
    private Set<Option<?>> bundleOptions = Collections.emptySet();
    private YACLScreen yaclScreen;

    private static final int ITEM_SIZE    = 16;
    private static final int SLOT_PADDING =  1;
    private static final int SLOT_SIZE    = ITEM_SIZE + SLOT_PADDING * 2; // 18

    // Vanilla inventory slot colors (3D bevel style)
    private static final int SLOT_BORDER_DARK   = 0xFF373737;
    private static final int SLOT_BORDER_LIGHT  = 0xFFFFFFFF;
    private static final int SLOT_BORDER_MID    = 0xFF8B8B8B;
    private static final int SLOT_FILL          = 0xFF8B8B8B;
    private static final int TEXT_DIMMED        = 0x80AAAAAA;

    public PersistentPreviewWidget(int x, int y, int width, int height,
                                   ConfigOptions config, int renderScale) {
        super(x, y, width, height, Component.empty());
        this.config      = config;
        this.renderScale = renderScale;
    }

    /** Set the YACLScreen reference for active category and option state lookups. */
    public void setYaclScreen(YACLScreen yaclScreen) {
        this.yaclScreen = yaclScreen;
    }

    /** Set the set of options that trigger bundle preview when hovered or focused. */
    public void setBundleOptions(Set<Option<?>> bundleOptions) {
        this.bundleOptions = bundleOptions;
    }

    /** Switch the preview between shulker box (false) and bundle (true). */
    public void setShowBundle(boolean showBundle) {
        this.showBundle = showBundle;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics,
                                         int mouseX, int mouseY, float delta) {
        int x0 = getX(), y0 = getY(), w = getWidth(), h = getHeight();

        // ── Check hover/focus on options list to toggle showing Bundle vs Shulker ────
        if (this.yaclScreen != null) {
            if (this.yaclScreen.tabNavigationBar != null && this.yaclScreen.tabNavigationBar.getTabManager().getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
                try {
                    java.lang.reflect.Field optionListField = YACLScreen.CategoryTab.class.getDeclaredField("optionList");
                    optionListField.setAccessible(true);
                    dev.isxander.yacl3.gui.WidgetAndType<?> widgetAndType = (dev.isxander.yacl3.gui.WidgetAndType<?>) optionListField.get(categoryTab);
                    if (widgetAndType != null && widgetAndType.getType() instanceof OptionListWidget optionList) {
                        for (OptionListWidget.Entry entry : optionList.children()) {
                            if (entry instanceof OptionListWidget.OptionEntry optionEntry) {
                                if (optionEntry.isMouseOver(mouseX, mouseY) || optionEntry.isFocused()) {
                                    this.showBundle = bundleOptions.contains(optionEntry.option);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore reflection error if layout differs
                }
            }
        }

        // ── Centered slot position ──────────────────────────────────────────
        int slotPx = SLOT_SIZE * renderScale;
        int slotX  = x0 + (w - slotPx) / 2;
        int slotY  = y0 + (h - slotPx) / 2;

        // ── Vanilla 3D beveled slot border ──────────────────────────────────
        // Top-left highlight
        graphics.fill(RenderPipelines.GUI, slotX - 2, slotY - 2,
                slotX + slotPx + 1, slotY - 1, SLOT_BORDER_LIGHT);
        graphics.fill(RenderPipelines.GUI, slotX - 2, slotY - 1,
                slotX - 1, slotY + slotPx + 1, SLOT_BORDER_LIGHT);
        // Bottom-right shadow
        graphics.fill(RenderPipelines.GUI, slotX - 1, slotY + slotPx,
                slotX + slotPx + 2, slotY + slotPx + 1, SLOT_BORDER_DARK);
        graphics.fill(RenderPipelines.GUI, slotX + slotPx, slotY - 1,
                slotX + slotPx + 1, slotY + slotPx + 1, SLOT_BORDER_DARK);
        // Outer shadow corners
        graphics.fill(RenderPipelines.GUI, slotX - 1, slotY + slotPx + 1,
                slotX + slotPx + 2, slotY + slotPx + 2, SLOT_BORDER_DARK);
        graphics.fill(RenderPipelines.GUI, slotX + slotPx + 1, slotY - 1,
                slotX + slotPx + 2, slotY + slotPx + 2, SLOT_BORDER_DARK);
        // Inner border
        graphics.fill(RenderPipelines.GUI, slotX - 1, slotY - 1,
                slotX + slotPx, slotY, SLOT_BORDER_MID);
        graphics.fill(RenderPipelines.GUI, slotX - 1, slotY,
                slotX, slotY + slotPx, SLOT_BORDER_MID);
        // Slot fill
        graphics.fill(RenderPipelines.GUI,
                slotX, slotY, slotX + slotPx, slotY + slotPx, SLOT_FILL);

        // ── Item rendering (requires an active world for Item Component Registry binding) ──
        if (Minecraft.getInstance().level == null) {
            Font font = Minecraft.getInstance().font;
            Component label = Component.translatable("config.simpleshulkerpreview.ingameOnly");
            int labelWidth = font.width(label);
            int labelX = x0 + (w - labelWidth) / 2;
            int labelY = slotY + slotPx + 4;
            graphics.text(font, label, labelX, labelY, TEXT_DIMMED, false);
            return; // slot background and label are visible as an out-of-world placeholder
        }

        int itemScreenX = slotX + SLOT_PADDING * renderScale;
        int itemScreenY = slotY + SLOT_PADDING * renderScale;

        // ── Scale matrix for magnified item rendering ───────────────────────
        var matrix = graphics.pose();
        matrix.pushMatrix();
        matrix.translate(itemScreenX, itemScreenY);
        matrix.scale(renderScale, renderScale);

        // Choose container type based on whether bundle settings are active
        ItemStack containerStack = showBundle ? getPreviewBundle() : getPreviewShulker();
        graphics.fakeItem(containerStack, 0, 0);

        // ── Use the mod's own renderers for pixel-perfect overlays ───────────
        if (!config.disableMod) {
            ContainerManager containerManager = new ContainerManager(containerStack);

            // Icon overlay (delegates to IconRenderer → wrapDrawItem mixin)
            if (config.showPreviewIcon) {
                ItemStack displayStack = containerManager.getDisplayStack();
                if (displayStack != null) {
                    IconRenderer iconRenderer = new IconRenderer(
                            containerManager, displayStack, 0, 0);
                    iconRenderer.renderOptional(graphics);
                }
            }

            // Capacity bar (delegates to CapacityBarRenderer — handles all
            // four directions, shadow, and position from the live config)
            if (config.showCapacity) {
                CapacityBarRenderer barRenderer = new CapacityBarRenderer(
                        containerManager, containerStack, 0, 0);
                barRenderer.renderOptional(graphics);
            }
        }

        matrix.popMatrix();
    }

    /**
     * Creates a shulker box with realistic mixed contents for the preview.
     * <p>
     * The contents exercise different icon selection modes:
     * <ul>
     *   <li><b>FIRST</b>: selects diamond (first item type in list)</li>
     *   <li><b>LAST</b>: selects oak_planks (last item type in list)</li>
     *   <li><b>MOST</b>: selects grass_block (highest total count: 640)</li>
     *   <li><b>LEAST</b>: selects emerald (lowest count: 16)</li>
     *   <li><b>UNIQUE</b>: returns null (multiple distinct item types)</li>
     * </ul>
     * <p>
     * Populated with 1168 items total across 19 slots (out of 1728 max capacity)
     * to render the capacity bar as ~67.6% full.
     */
    // ── Cached instances to avoid GC allocation churn per render frame ──
    private ItemStack cachedShulker;
    private ItemStack cachedBundle;

    private ItemStack getPreviewShulker() {
        if (cachedShulker == null) {
            cachedShulker = createPreviewShulker();
        }
        return cachedShulker;
    }

    private ItemStack getPreviewBundle() {
        if (cachedBundle == null) {
            cachedBundle = createPreviewBundle();
        }
        return cachedBundle;
    }

    private ItemStack createPreviewShulker() {
        Item whiteShulker = BuiltInRegistries.ITEM.getValue(Identifier.parse("white_shulker_box"));
        Item shulkerItem = (whiteShulker != null && !whiteShulker.equals(Items.AIR)) ? whiteShulker : Items.SHULKER_BOX;
        ItemStack shulker = new ItemStack(shulkerItem);

        List<ItemStack> items = new ArrayList<>();
        addStacks(items, Items.DIAMOND,     64, 3);  // 192 Diamonds (3 full stacks) -> FIRST
        addStacks(items, Items.GRASS_BLOCK, 64, 10); // 640 Grass Blocks (10 full stacks) -> MOST
        addStacks(items, Items.EMERALD,     16, 1);  // 16 Emeralds (1 partial stack) -> LEAST
        addStacks(items, Items.OAK_PLANKS,  64, 5);  // 320 Oak Planks (5 full stacks) -> LAST

        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        return shulker;
    }

    private static void addStacks(List<ItemStack> list, Item item, int countPerStack, int stackAmount) {
        for (int i = 0; i < stackAmount; i++) {
            list.add(new ItemStack(item, countPerStack));
        }
    }

    /**
     * Creates a bundle with the same mixed contents as the shulker preview,
     * scaled to 32 items total (50% of the 64-item bundle capacity) so the
     * capacity bar displays half full.
     */
    private ItemStack createPreviewBundle() {
        ItemStack bundle = new ItemStack(Items.BUNDLE);
        List<ItemStack> items = List.of(
                new ItemStack(Items.DIAMOND,      6),
                new ItemStack(Items.GRASS_BLOCK, 14),
                new ItemStack(Items.EMERALD,      2),
                new ItemStack(Items.OAK_PLANKS,  10)
        );
        BundleContents.Mutable mutable = new BundleContents.Mutable(BundleContents.EMPTY);
        for (ItemStack item : items) {
            mutable.tryInsert(item);
        }
        bundle.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
        return bundle;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        // Visual-only preview — no narration required.
    }
}
