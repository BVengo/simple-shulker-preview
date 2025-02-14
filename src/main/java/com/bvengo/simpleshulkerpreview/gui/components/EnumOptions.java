package com.bvengo.simpleshulkerpreview.gui.components;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.util.List;
import java.util.function.Consumer;

public class EnumOptions<T extends Enum<T> & TranslatableOption> {
	public SimpleOption<T> option;
	public ClickableWidget widget;

	public EnumOptions(Class<T> clazz, MinecraftClient client, String key, int x, int y, int width, T defaultValue, Consumer<T> setter) {
		String translationKey = SimpleShulkerPreviewMod.MOD_ID + ".options." + key;

		List<T> values = List.of(clazz.getEnumConstants());
		Codec<T> enumCodec = Codec.INT.xmap(
				values::get,
				Enum::ordinal
		);

		this.option = new SimpleOption<T>(
				translationKey,
				value -> Tooltip.of(Text.translatable(value.getTranslationKey() + ".tooltip")),
				(optionText, value) -> Text.of(value.toString()),
				new SimpleOption.PotentialValuesBasedCallbacks<>(values, enumCodec),
				defaultValue,
				setter
		);

		this.widget = this.option.createWidget(client.options, x, y, width);
	}
}
