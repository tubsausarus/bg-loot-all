package tubs.bglootall;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;


/**
 * A button with scaled text that disables itself if the container is empty.
 * Uses modern 1.21.4 rendering (drawGuiTexture) and avoids stretching.
 */
public class CustomTextSizeButton extends PressableWidget {
    static {
        Identifier.ofVanilla("textures/gui/widgets.png");
    }

    private final float scale;
    private final Runnable onPress;
    private final HandledScreen<?> screen;
    private final boolean isContainer;

    public CustomTextSizeButton(int x, int y, int width, int height,
                           Text message, Runnable onPress,
                           HandledScreen<?> screen, float scale, boolean isContainer) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.scale = scale;
        this.screen = screen;
        this.isContainer = isContainer;
    }

    @Override
    public void onPress() {
        if (this.active) {
            onPress.run();
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // update active state
        if(this.isContainer){
            this.active = !isContainerEmpty();
        } else{
            this.active = !isInventoryEmpty();
        }


        // colors depending on state
        int fillColor;
        int borderColor;
        if (!this.active) {
            fillColor = 0xFF555555;   // dark gray
            borderColor = 0xFF2B2B2B; // darker border
        } else if (this.isHovered()) {
            fillColor = 0xFFAAAAFF;   // light blue hover
            borderColor = 0xFF5555AA; // blue border
        } else {
            fillColor = 0xFF777777;   // normal gray
            borderColor = 0xFF333333; // border
        }

        // draw filled rectangle
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), fillColor);

        // draw border (top, bottom, left, right)
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, borderColor); // top
        context.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor); // bottom
        context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), borderColor); // left
        context.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), borderColor); // right

        // scaled text
        var matrices = context.getMatrices();
        matrices.push();
        matrices.scale(scale, scale, 1.0f);

        var tr = MinecraftClient.getInstance().textRenderer;
        int textWidth = tr.getWidth(getMessage());
        int textX = (int) ((this.getX() + (this.getWidth() / 2f) - (textWidth * scale / 2f)) / scale);
        int textY = (int) ((this.getY() + (this.getHeight() / 2f) - (tr.fontHeight * scale / 2f)) / scale);

        int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        context.drawText(tr, getMessage(), textX, textY, textColor, true);

        matrices.pop();
    }



    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        if (isContainerEmpty()) {
            builder.put(NarrationPart.TITLE, Text.literal("Loot All Button, chest is empty"));
        } else {
            builder.put(NarrationPart.TITLE, Text.literal("Loot All Button, chest has loot"));
        }
    }

    private boolean isContainerEmpty() {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return true;

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory != client.player.getInventory() && slot.hasStack()) {
                return false;
            }
        }
        return true;
    }

    private boolean isInventoryEmpty() {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return true;

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory == client.player.getInventory() && slot.hasStack()) {
                return false;
            }
        }
        return true;
    }
}

