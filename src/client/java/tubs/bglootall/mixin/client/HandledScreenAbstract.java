package tubs.bglootall.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenAbstract {
    @Inject(method = "drawForeground", at = @At("HEAD"))
    private void captureTitleText(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        HandledScreen<?> self = (HandledScreen<?>)(Object)this;
        Text title = self.getTitle();
        System.out.println("[LootAll] Foreground title: " + title.getString());
    }
}

