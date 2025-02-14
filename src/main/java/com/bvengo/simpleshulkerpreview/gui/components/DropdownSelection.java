package com.bvengo.simpleshulkerpreview.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.util.function.Consumer;

public class DropdownSelection<T extends Enum<T> & TranslatableOption> {
	final Class<T> clazz;
	T selected;

	MinecraftClient client;

	public DisplayWidget selectedDisplay;
	public DropdownSelectionListWidget dropdown;

	public int x, y, width, height, dropdownHeight;

	Consumer<T> changedListener;

	public DropdownSelection(Class<T> clazz, T selected, MinecraftClient client, int x, int y, int width, int height, int dropdownHeight, Consumer<T> changedListener) {
		this.clazz = clazz;
		this.selected = selected;
		this.client = client;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dropdownHeight = dropdownHeight;

		this.changedListener = changedListener;
	}

	public void init() {
		this.selectedDisplay = new DisplayWidget(x + width / 2, y, width, height, Text.translatable(selected.getTranslationKey()), client.textRenderer);
		this.selectedDisplay.active = true;

		this.dropdown = new DropdownSelectionListWidget(client);

		dropdown.visible = false;
		dropdown.setFocused(false);
	}

	public void setSelected(T selected) {
		this.selected = selected;
		this.selectedDisplay.setMessage(Text.translatable(selected.getTranslationKey()));

		changedListener.accept(selected);
	}

	public T getSelected() {
		return selected;
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		selectedDisplay.render(context, mouseX, mouseY, delta);
		if (dropdown.visible) {
			dropdown.render(context, mouseX, mouseY, delta);
		}
	}

	public class DisplayWidget extends TextWidget {
		public DisplayWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
			super(x, y, width, height, message, textRenderer);
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
			dropdown.visible = !dropdown.visible;
			dropdown.setFocused(dropdown.visible);
		}
	}

	private class DropdownSelectionListWidget extends AlwaysSelectedEntryListWidget<DropdownSelectionListWidget.DropdownEntry> {
		public DropdownSelectionListWidget(final MinecraftClient client) {
//			super(client, DropdownSelection.this.width, DropdownSelection.this.dropdownHeight, x, y + DropdownSelection.this.height);
			super(client, 10, 10, 20, 15);

			for(T value : DropdownSelection.this.clazz.getEnumConstants()) {
				Text text = Text.translatable(value.getTranslationKey());
				DropdownSelectionListWidget.DropdownEntry dropdownEntry = new DropdownSelectionListWidget.DropdownEntry(text);
				this.addEntry(dropdownEntry);
				if (value == DropdownSelection.this.selected) {
					this.setSelected(dropdownEntry);
					DropdownSelection.this.setSelected(value);
				}
			}
		}

		public int getRowWidth() {
			return DropdownSelection.this.width;
		}

		public class DropdownEntry extends AlwaysSelectedEntryListWidget.Entry<DropdownEntry> {
			private final Text displayName;

			public DropdownEntry(final Text displayName) {
				this.displayName = displayName;
			}

			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				TextRenderer textRenderer = DropdownSelection.this.client.textRenderer;
				context.drawCenteredTextWithShadow(textRenderer, displayName, x, y + entryHeight / 2, -1);
			}

			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if (KeyCodes.isToggle(keyCode)) {
					this.onPressed();
					return true;
				} else {
					return super.keyPressed(keyCode, scanCode, modifiers);
				}
			}

			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				this.onPressed();
				return super.mouseClicked(mouseX, mouseY, button);
			}

			private void onPressed() {
				DropdownSelectionListWidget.this.setSelected(this);
			}

			public Text getNarration() {
				return this.displayName;
			}
		}
	}
}
