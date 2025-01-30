package com.bvengo.simpleshulkerpreview.gui.panels;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class Panel extends ScrollableWidget {
	protected final MinecraftClient client;
	protected final TextRenderer textRenderer;

	protected final List<Element> children = new ArrayList<>();
	protected final List<Drawable> drawables = new ArrayList<>();

	public static final int X_PADDING = 16;
	protected static final int ELEMENT_HEIGHT = 20;
	protected static final int ELEMENT_GAP = 4;

	protected int elementWidth = 0;
	private int contentsHeight = 0;

	@Nullable Element selectedElement = null;

	public Panel(int x, int y, int width, int height, Text text, MinecraftClient client) {
		super(x, y, width, height, text);
		this.client = client;
		this.textRenderer = client.textRenderer;

		this.elementWidth = getScrollbarX() - getX() - X_PADDING;
	}

	public abstract void init();

	protected <T extends Drawable & Element> void addChild(T child) {
		this.children.add(child);
		this.drawables.add(child);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// Update the scrollbar dragging
		boolean scrollbarDragged = this.checkScrollbarDragged(mouseX, mouseY, button);
		boolean childClicked = false;

		if(!scrollbarDragged) {
			for (Element child : this.children) {
				childClicked = child.mouseClicked(mouseX, mouseY + getScrollY(), button);
				if (childClicked) {
					this.selectedElement = child;
					break;
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button) || childClicked || scrollbarDragged;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean childReleased = false;
		if(this.selectedElement != null) {
			childReleased = this.selectedElement.mouseReleased(mouseX, mouseY + getScrollY(), button);
			this.selectedElement = null;
		}
		return super.mouseReleased(mouseX, mouseY, button) || childReleased;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean childDragged = this.selectedElement != null && this.selectedElement.mouseDragged(mouseX, mouseY + getScrollY(), button, deltaX, deltaY);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || childDragged;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Allow scrolling with arrow keys
		boolean up = keyCode == GLFW.GLFW_KEY_UP;
		boolean down = keyCode == GLFW.GLFW_KEY_DOWN;
		if (up || down) {
			double distance = this.getScrollY();
			this.setScrollY(this.getScrollY() + (double)(up ? -1 : 1) * this.getDeltaYPerScroll());
			if (distance != this.getScrollY()) {
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	protected void setContentsHeightWithPadding(int height) {
		this.contentsHeight = height;
	}

	@Override
	protected int getContentsHeightWithPadding() {
		return contentsHeight;
	}

	@Override
	protected double getDeltaYPerScroll() {
		return 10;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		// Shift the contents of the panel down by the scroll amount
		// Cut off the top and bottom of the contents that are outside the panel using scissor
		context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
		context.getMatrices().push();
		context.getMatrices().translate(0.0, -this.getScrollY(), 0.0);
		// Offset the mouse position by the scroll amount when rendering, so hover conditions work properly
		this.renderContents(context, mouseX, (int)(mouseY + this.getScrollY()), delta);
		context.getMatrices().pop();
		context.disableScissor();

		drawScrollbar(context);
	}

	protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(context, mouseX, mouseY, delta);
		}
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
