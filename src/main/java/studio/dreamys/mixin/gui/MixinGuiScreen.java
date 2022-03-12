package studio.dreamys.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.font.Fonts;
import studio.dreamys.util.ParticleUtils;
import studio.dreamys.util.RenderUtils;
import studio.dreamys.util.shader.shaders.BackgroundShader;

import java.util.Collections;
import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui {
    @Shadow
    public Minecraft mc;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Inject(method = "drawWorldBackground", at = @At("HEAD"))
    private void drawWorldBackground(CallbackInfo callbackInfo) {
        if(mc.thePlayer != null) {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            ParticleUtils.drawParticles(Mouse.getX() * width / mc.displayWidth, height - Mouse.getY() * height / mc.displayHeight - 1);
        }
    }

    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    private void drawClientBackground(CallbackInfo callbackInfo) {
        RenderUtils.drawClientBackground(width, height);
        callbackInfo.cancel();
    }

    @Inject(method = "drawBackground", at = @At("RETURN"))
    private void drawParticles(CallbackInfo callbackInfo) {
        ParticleUtils.drawParticles(Mouse.getX() * width / mc.displayWidth, height - Mouse.getY() * height / mc.displayHeight - 1);
    }

    @Overwrite
    protected void actionPerformed(GuiButton button) {

    }
}