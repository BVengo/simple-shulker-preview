package com.bvengo.simpleshulkerpreview.compat;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Render compatibility layer for GUI sprite rendering.
 * Handles the Blaze3D → RenderPearl pipeline migration for 26.3.
 */
public final class RenderCompat {

    private static final Method BLIT_SPRITE;
    private static final Object GUI_TEXTURED_PIPELINE;
    private static boolean loggedRenderError = false;

    static {
        Method method = null;
        Object pipeline = null;

        try {
            // Detect which pipeline is being used
            Class<?> pipelineClass;
            try {
                // 26.3+: RenderPearl
                pipelineClass = Class.forName("com.mojang.renderpearl.api.pipeline.RenderPipeline");
            } catch (ClassNotFoundException e) {
                // <=26.2: Blaze3D
                pipelineClass = Class.forName("com.mojang.blaze3d.pipeline.RenderPipeline");
            }

            // Grab the static RenderPipelines.GUI_TEXTURED field
            Class<?> pipelinesClass = Class.forName("net.minecraft.client.renderer.RenderPipelines");
            Field guiTextField = pipelinesClass.getField("GUI_TEXTURED");
            pipeline = guiTextField.get(null);

            // Find blitSprite(pipeline, Identifier, int, int, int, int)
            for (Method m : GuiGraphicsExtractor.class.getMethods()) {
                if (m.getName().equals("blitSprite") && m.getParameterCount() == 6) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params[0].isAssignableFrom(pipelineClass)
                            && params[1] == Identifier.class
                            && params[2] == int.class
                            && params[3] == int.class
                            && params[4] == int.class
                            && params[5] == int.class) {
                        method = m;
                        break;
                    }
                }
            }

            if (method == null) {
                SimpleShulkerPreviewMod.LOGGER.warn(
                        "[RenderCompat] Couldn't find blitSprite method; sprite rendering disabled");
            }
        } catch (Exception e) {
            SimpleShulkerPreviewMod.LOGGER.warn(
                    "[RenderCompat] Init failed; sprite rendering disabled", e);
        }

        BLIT_SPRITE = method;
        GUI_TEXTURED_PIPELINE = pipeline;
    }

    private RenderCompat() {}

    /**
     * Renders a GUI sprite using the right runtime pipeline.
     * Will no-op if the compat layer failed to initialize or if arguments are invalid.
     */
    public static void blitSprite(GuiGraphicsExtractor graphics, Identifier sprite,
                                  int x, int y, int width, int height) {
        if (BLIT_SPRITE == null || graphics == null || sprite == null
                || width <= 0 || height <= 0) {
            return;
        }

        try {
            BLIT_SPRITE.invoke(graphics, GUI_TEXTURED_PIPELINE, sprite, x, y, width, height);
        } catch (Exception e) {
            if (!loggedRenderError) {
                SimpleShulkerPreviewMod.LOGGER.warn(
                        "[RenderCompat] blitSprite call failed; future failures hidden", e);
                loggedRenderError = true;
            }
        }
    }
}
