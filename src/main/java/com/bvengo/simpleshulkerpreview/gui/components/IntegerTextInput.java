package com.bvengo.simpleshulkerpreview.gui.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.function.Consumer;

public class IntegerTextInput extends TextFieldWidget {
	public IntegerTextInput(TextRenderer textRenderer, int x, int y, int width, int height, Text text, int value, int min, int max, Consumer<Integer> setter) {
		super(textRenderer, x, y, width, height, text);
		this.setText(String.valueOf(value));

		this.setTextPredicate(s -> {
			if(s.isEmpty()) return true;
			try {
				int integer = Integer.parseInt(s);
				if(integer < min || integer > max) {
					this.setEditableColor(Colors.RED);
					return false;
				}
				this.setEditableColor(Colors.WHITE);
				return true;
			} catch (NumberFormatException ignored) {}

			this.setEditableColor(Colors.RED);
			return false;
		});

		this.setChangedListener(s -> {
			if(s.isEmpty()) return;
			try {
				int integer = Integer.parseInt(s);
				setter.accept(integer);  // Predicate handles the rest
			} catch (NumberFormatException e) {
				// Do nothing
			}
		});

		this.setEditable(true);
	}
}
