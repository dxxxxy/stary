package studio.dreamys.mixin.client;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MinecraftError;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import studio.dreamys.util.RenderUtils;

@Mixin(LoadingScreenRenderer.class)
public abstract class MixinLoadingScreenRenderer {

    @Shadow private String message;
    @Shadow private Minecraft mc;
    @Shadow private String currentlyDisplayedText;
    @Shadow private long systemTime;
    @Shadow private boolean loadingSuccess;
    @Shadow private Framebuffer framebuffer;

    @Overwrite //rewriting the whole method
    public void setLoadingProgress(int progress) {
        {
            if (!((AccessMinecraft) mc).getRunning()) {
                if (!loadingSuccess) {
                    throw new MinecraftError();
                }
            } else {
                long i = Minecraft.getSystemTime();

                if (i - systemTime >= 100L) {
                    systemTime = i;
                    ScaledResolution scaledresolution = new ScaledResolution(mc);
                    int j = scaledresolution.getScaleFactor();
                    int k = scaledresolution.getScaledWidth();
                    int l = scaledresolution.getScaledHeight();

                    if (OpenGlHelper.isFramebufferEnabled()) {
                        framebuffer.framebufferClear();
                    } else {
                        GlStateManager.clear(256);
                    }

                    framebuffer.bindFramebuffer(false);
                    GlStateManager.matrixMode(5889);
                    GlStateManager.loadIdentity();
                    GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                    GlStateManager.matrixMode(5888);
                    GlStateManager.loadIdentity();
                    GlStateManager.translate(0.0F, 0.0F, -200.0F);

                    if (!OpenGlHelper.isFramebufferEnabled()) {
                        GlStateManager.clear(16640);
                    }

                    RenderUtils.drawClientBackground(mc.displayWidth, mc.displayHeight);
//                try
//                {
//                    if (!net.minecraftforge.fml.client.FMLClientHandler.instance().handleLoadingScreen(scaledresolution)) //FML Don't render while FML's pre-screen is rendering
//                    {
//                        Tessellator tessellator = Tessellator.getInstance();
//                        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
//                        mc.getTextureManager().bindTexture(Gui.optionsBackground);
//                        float f = 32.0F;
//                        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
//                        worldrenderer.pos(0.0D, (double)l, 0.0D).tex(0.0D, (double)((float)l / f)).color(64, 64, 64, 255).endVertex();
//                        worldrenderer.pos((double)k, (double)l, 0.0D).tex((double)((float)k / f), (double)((float)l / f)).color(64, 64, 64, 255).endVertex();
//                        worldrenderer.pos((double)k, 0.0D, 0.0D).tex((double)((float)k / f), 0.0D).color(64, 64, 64, 255).endVertex();
//                        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
//                        tessellator.draw();
//
//                        if (progress >= 0)
//                        {
//                            int i1 = 100;
//                            int j1 = 2;
//                            int k1 = k / 2 - i1 / 2;
//                            int l1 = l / 2 + 16;
//                            GlStateManager.disableTexture2D();
//                            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
//                            worldrenderer.pos((double)k1, (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
//                            worldrenderer.pos((double)k1, (double)(l1 + j1), 0.0D).color(128, 128, 128, 255).endVertex();
//                            worldrenderer.pos((double)(k1 + i1), (double)(l1 + j1), 0.0D).color(128, 128, 128, 255).endVertex();
//                            worldrenderer.pos((double)(k1 + i1), (double)l1, 0.0D).color(128, 128, 128, 255).endVertex();
//                            worldrenderer.pos((double)k1, (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
//                            worldrenderer.pos((double)k1, (double)(l1 + j1), 0.0D).color(128, 255, 128, 255).endVertex();
//                            worldrenderer.pos((double)(k1 + progress), (double)(l1 + j1), 0.0D).color(128, 255, 128, 255).endVertex();
//                            worldrenderer.pos((double)(k1 + progress), (double)l1, 0.0D).color(128, 255, 128, 255).endVertex();
//                            tessellator.draw();
//                            GlStateManager.enableTexture2D();
//                        }

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    mc.fontRendererObj.drawStringWithShadow(currentlyDisplayedText, (float) ((k - mc.fontRendererObj.getStringWidth(currentlyDisplayedText)) / 2), (float) (l / 2 - 4 - 16), 16777215);
                    mc.fontRendererObj.drawStringWithShadow(message, (float) ((k - mc.fontRendererObj.getStringWidth(message)) / 2), (float) (l / 2 - 4 + 8), 16777215);
//                    }
//                }
//                catch (java.io.IOException e)
//                {
//                    com.google.common.base.Throwables.propagate(e);
//                } //FML End
                    framebuffer.unbindFramebuffer();

                    if (OpenGlHelper.isFramebufferEnabled()) {
                        framebuffer.framebufferRender(k * j, l * j);
                    }

                    mc.updateDisplay();

                    try {
                        Thread.yield();
                    } catch (Exception ignored) {

                    }
                }
            }
        }
    }
}
