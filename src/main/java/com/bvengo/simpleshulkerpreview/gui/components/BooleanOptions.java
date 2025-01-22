package com.bvengo.simpleshulkerpreview.gui.components;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.client.option.SimpleOption.BOOLEAN;
import static net.minecraft.client.option.SimpleOption.BOOLEAN_TEXT_GETTER;

public class BooleanOptions {
	public SimpleOption<Boolean> option;
	public ClickableWidget widget;

	public BooleanOptions(MinecraftClient client, String key, int x, int y, int width, boolean defaultValue, Consumer<Boolean> setter) {
		String translationKey = SimpleShulkerPreviewMod.MOD_ID + ".options." + key;

		this.option = new SimpleOption<>(
				translationKey,
				value -> Tooltip.of(Text.translatable(translationKey + ".tooltip")),
				BOOLEAN_TEXT_GETTER,
				BOOLEAN,
				defaultValue,
				setter
		);

		this.widget = this.option.createWidget(client.options, x, y, width);
	}
}
