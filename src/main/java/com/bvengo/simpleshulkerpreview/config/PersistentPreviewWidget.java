package com.bvengo.simpleshulkerpreview.config;

import com.bvengo.simpleshulkerpreview.container.ContainerManager;
import com.bvengo.simpleshulkerpreview.positioners.CapacityBarRenderer;
import com.bvengo.simpleshulkerpreview.positioners.IconRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import org.apache.commons.lang3.math.Fraction;
import org.joml.Matrix3x2fStack;

import java.util.List;

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

    private static final int ITEM_SIZE    = 16;
    private static final int SLOT_PADDING =  1;
    private static final int SLOT_SIZE    = ITEM_SIZE + SLOT_PADDING * 2; // 18

    // Vanilla inventory slot colors (3D bevel style)
    private static final int SLOT_BORDER_DARK   = 0xFF373737;
    private static final int SLOT_BORDER_LIGHT  = 0xFFFFFFFF;
    private static final int SLOT_BORDER_MID    = 0xFF8B8B8B;
    private static final int SLOT_FILL          = 0xFF8B8B8B;
    private static final int DIVIDER_COLOR      = 0xFF4A4A4A;

    public PersistentPreviewWidget(int x, int y, int width, int height,
                                   ConfigOptions config, int renderScale) {
        super(x, y, width, height, Component.empty());
        this.config      = config;
        this.renderScale = renderScale;
    }

    /** Switch the preview between shulker box (false) and bundle (true). */
    public void setShowBundle(boolean showBundle) {
        this.showBundle = showBundle;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics,
                                         int mouseX, int mouseY, float delta) {
        int x0 = getX(), y0 = getY(), w = getWidth(), h = getHeight();

        // ── Divider line (section separator, inset from edges) ──────────────
        int dividerY = y0 + 2;
        graphics.fill(RenderPipelines.GUI,
                x0 + 8, dividerY, x0 + w - 8, dividerY + 1, DIVIDER_COLOR);

        // ── Centered slot position ──────────────────────────────────────────
        int slotPx = SLOT_SIZE * renderScale;
        int slotX  = x0 + (w - slotPx) / 2;
        int slotY  = y0 + 6 + (h - 6 - slotPx) / 2;

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

        // ── Item rendering (requires a world for Data Components) ───────────
        if (Minecraft.getInstance().level == null) {
            return; // slot background is still visible as a placeholder
        }

        int itemScreenX = slotX + SLOT_PADDING * renderScale;
        int itemScreenY = slotY + SLOT_PADDING * renderScale;

        // ── Scale matrix for magnified item rendering ───────────────────────
        Matrix3x2fStack matrix = graphics.pose();
        matrix.pushMatrix();
        matrix.translate(itemScreenX, itemScreenY);
        matrix.scale(renderScale, renderScale);

        // Choose container type based on whether bundle settings are active
        ItemStack containerStack = showBundle ? createPreviewBundle() : createPreviewShulker();
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
     *   <li><b>FIRST</b>: selects diamond (first in list)</li>
     *   <li><b>LAST</b>: selects oak_planks (last in list)</li>
     *   <li><b>MOST</b>: selects grass_block (highest total count: 32)</li>
     *   <li><b>LEAST</b>: selects emerald (lowest count: 3)</li>
     *   <li><b>UNIQUE</b>: returns null (multiple distinct items)</li>
     * </ul>
     */
    private ItemStack createPreviewShulker() {
        ItemStack shulker = new ItemStack(Items.SHULKER_BOX);
        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(
                new ItemStack(Items.DIAMOND,     10),
                new ItemStack(Items.GRASS_BLOCK, 32),
                new ItemStack(Items.EMERALD,      3),
                new ItemStack(Items.OAK_PLANKS,  20)
        )));
        return shulker;
    }

    /**
     * Creates a bundle with the same mixed contents as the shulker preview,
     * using BundleContents so the mod's ContainerManager identifies it as
     * a bundle and applies the bundle-specific icon position config.
     */
    private ItemStack createPreviewBundle() {
        ItemStack bundle = new ItemStack(Items.BUNDLE);
        List<ItemStack> items = List.of(
                new ItemStack(Items.DIAMOND,     10),
                new ItemStack(Items.GRASS_BLOCK, 32),
                new ItemStack(Items.EMERALD,      3),
                new ItemStack(Items.OAK_PLANKS,  20)
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
