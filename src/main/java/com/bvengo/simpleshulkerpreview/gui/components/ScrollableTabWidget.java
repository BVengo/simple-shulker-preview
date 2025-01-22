package com.bvengo.simpleshulkerpreview.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollableTabWidget extends ScrollableWidget implements ParentElement {

	List<Element> children;

	public ScrollableTabWidget(int x, int y, int width, int height, Text text) {
		super(x, y, width, height, text);
		children = new ArrayList<>();
	}

	public void addChild(Element child) {
		children.add(child);
	}

	@Override
	public List<Element> children() {
		return children;
	}

	@Override
	public boolean isDragging() {
		return false;
	}

	@Override
	public void setDragging(boolean dragging) {

	}

	@Nullable
	@Override
	public Element getFocused() {
		return null;
	}

	@Override
	public void setFocused(@Nullable Element focused) {

	}

	@Override
	protected int getContentsHeightWithPadding() {
		return 0;
	}

	@Override
	protected double getDeltaYPerScroll() {
		return 0;
	}

	@Override
	public int getMaxScrollY() {
		// Temporary for testing purposes
		return Math.max(1, this.getContentsHeightWithPadding() - this.height);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		drawScrollbar(context);
		children.stream().filter(child -> child instanceof Drawable).forEach(child -> ((Drawable) child).render(context, mouseX, mouseY, delta));
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}

