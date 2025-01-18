package com.bvengo.simpleshulkerpreview.gui.components;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.client.option.GameOptions.getGenericValueText;

public class IntegerSlider {
	public SimpleOption<Integer> option;
	public ClickableWidget widget;


	public IntegerSlider(MinecraftClient client, String key, int min, int max, int x, int y, int width, int defaultValue, Consumer<Integer> setter) {
		this.option = new SimpleOption<>(
				SimpleShulkerPreviewMod.MOD_ID + ".options." + key,
				SimpleOption.emptyTooltip(),
				(optionText, value) -> getGenericValueText(optionText, Text.of(value.toString())),
				new SimpleOption.ValidatingIntSliderCallbacks(min, max),
				Codec.intRange(min, max),
				defaultValue,
				setter
		);

		this.widget = this.option.createWidget(client.options, x, y, width);
	}
}
