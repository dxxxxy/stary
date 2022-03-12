package studio.dreamys.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GuiScrollingList.class, remap = false)
public abstract class MixinGuiScrollingList {
    @Shadow @Final private Minecraft client;
    @Shadow @Final protected int listWidth;
    @Shadow @Final protected int top;
    @Shadow @Final protected int bottom;
    @Shadow @Final protected int right;
    @Shadow @Final protected int left;
    @Shadow @Final protected int slotHeight;
    @Shadow private float initialMouseClickY;
    @Shadow private float scrollFactor;
    @Shadow private float scrollDistance;
    @Shadow protected int selectedIndex;
    @Shadow private long lastClickTime;
    @Shadow private boolean highlightSelected;
    @Shadow private boolean hasHeader;
    @Shadow private int headerHeight;

    @Shadow @Deprecated protected abstract void func_27259_a(boolean hasFooter, int footerHeight);

    @Shadow protected abstract int getSize();

    @Shadow protected abstract void elementClicked(int index, boolean doubleClick);

    @Shadow protected abstract boolean isSelected(int index);

    @Shadow protected abstract int getContentHeight();

    @Shadow protected abstract void drawBackground();

    @Shadow protected abstract void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess);

    @Shadow @Deprecated protected abstract void func_27260_a(int entryRight, int relativeY, Tessellator tess);

    @Shadow protected abstract void drawHeader(int entryRight, int relativeY, Tessellator tess);

    @Shadow @Deprecated protected abstract void func_27255_a(int x, int y);

    @Shadow protected abstract void clickHeader(int x, int y);

    @Shadow @Deprecated protected abstract void func_27257_b(int mouseX, int mouseY);

    @Shadow protected abstract void drawScreen(int mouseX, int mouseY);

    @Shadow protected abstract void applyScrollLimits();

    @Shadow protected abstract void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2);

    @Overwrite //rewriting the whole method
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawBackground();

        boolean isHovering = mouseX >= left && mouseX <= left + listWidth &&
                mouseY >= top && mouseY <= bottom;
        int listLength     = getSize();
        int scrollBarWidth = 6;
        int scrollBarRight = left + listWidth;
        int scrollBarLeft  = scrollBarRight - scrollBarWidth;
        int entryLeft      = left;
        int entryRight     = scrollBarLeft - 1;
        int viewHeight     = bottom - top;
        int border         = 4;

        if (Mouse.isButtonDown(0))
        {
            if (initialMouseClickY == -1.0F)
            {
                if (isHovering)
                {
                    int mouseListY = mouseY - top - headerHeight + (int) scrollDistance - border;
                    int slotIndex = mouseListY / slotHeight;

                    if (mouseX >= entryLeft && mouseX <= entryRight && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength)
                    {
                        elementClicked(slotIndex, slotIndex == selectedIndex && System.currentTimeMillis() - lastClickTime < 250L);
                        selectedIndex = slotIndex;
                        lastClickTime = System.currentTimeMillis();
                    }
                    else if (mouseX >= entryLeft && mouseX <= entryRight && mouseListY < 0)
                    {
                        clickHeader(mouseX - entryLeft, mouseY - top + (int) scrollDistance - border);
                    }

                    if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight)
                    {
                        scrollFactor = -1.0F;
                        int scrollHeight = getContentHeight() - viewHeight - border;
                        if (scrollHeight < 1) scrollHeight = 1;

                        int var13 = (int)((float)(viewHeight * viewHeight) / (float) getContentHeight());

                        if (var13 < 32) var13 = 32;
                        if (var13 > viewHeight - border*2)
                            var13 = viewHeight - border*2;

                        scrollFactor /= (float)(viewHeight - var13) / (float)scrollHeight;
                    }
                    else
                    {
                        scrollFactor = 1.0F;
                    }

                    initialMouseClickY = mouseY;
                }
                else
                {
                    initialMouseClickY = -2.0F;
                }
            }
            else if (initialMouseClickY >= 0.0F)
            {
                scrollDistance -= ((float)mouseY - initialMouseClickY) * scrollFactor;
                initialMouseClickY = (float)mouseY;
            }
        }
        else
        {
            while (isHovering && Mouse.next())
            {
                int scroll = Mouse.getEventDWheel();
                if (scroll != 0)
                {
                    if      (scroll > 0) scroll = -1;
                    else if (scroll < 0) scroll =  1;

                    scrollDistance += (float)(scroll * slotHeight / 2);
                }
            }

            initialMouseClickY = -1.0F;
        }

        applyScrollLimits();

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldr = tess.getWorldRenderer();

        ScaledResolution res = new ScaledResolution(client);
        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(left      * scaleW), (int)(client.displayHeight - (bottom * scaleH)),
                (int)(listWidth * scaleW), (int)(viewHeight * scaleH));

        if (client.theWorld != null)
        {
            drawGradientRect(left, top, right, bottom, 0xC0101010, 0xD0101010);
        }
//        else // Draw dark dirt background
//        {
//            GlStateManager.disableLighting();
//            GlStateManager.disableFog();
//            client.renderEngine.bindTexture(Gui.optionsBackground);
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            float scale = 32.0F;
//            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
//            worldr.pos(left,  bottom, 0.0D).tex(left  / scale, (bottom + (int) scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
//            worldr.pos(right, bottom, 0.0D).tex(right / scale, (bottom + (int) scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
//            worldr.pos(right, top,    0.0D).tex(right / scale, (top    + (int) scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
//            worldr.pos(left,  top,    0.0D).tex(left  / scale, (top    + (int) scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
//            tess.draw();
//        }

        int baseY = top + border - (int) scrollDistance;

        if (hasHeader) {
            drawHeader(entryRight, baseY, tess);
        }

        for (int slotIdx = 0; slotIdx < listLength; ++slotIdx)
        {
            int slotTop = baseY + slotIdx * slotHeight + headerHeight;
            int slotBuffer = slotHeight - border;

            if (slotTop <= bottom && slotTop + slotBuffer >= top)
            {
                if (highlightSelected && isSelected(slotIdx))
                {
                    int min = left;
                    int max = entryRight;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                    worldr.pos(min,     slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(max,     slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(max,     slotTop              - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(min,     slotTop              - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(min + 1, slotTop + slotBuffer + 1, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(max - 1, slotTop + slotBuffer + 1, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(max - 1, slotTop              - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(min + 1, slotTop              - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    tess.draw();
                    GlStateManager.enableTexture2D();
                }

                drawSlot(slotIdx, entryRight, slotTop, slotBuffer, tess);
            }
        }

        GlStateManager.disableDepth();

        int extraHeight = getContentHeight() - viewHeight - border;
        if (extraHeight > 0)
        {
            int height = viewHeight * viewHeight / getContentHeight();

            if (height < 32) height = 32;

            if (height > viewHeight - border*2)
                height = viewHeight - border*2;

            int barTop = (int) scrollDistance * (viewHeight - height) / extraHeight + top;
            if (barTop < top)
            {
                barTop = top;
            }

            GlStateManager.disableTexture2D();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,  bottom, 0.0D).tex(0.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarRight, bottom, 0.0D).tex(1.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarRight, top,    0.0D).tex(1.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,  top,    0.0D).tex(0.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.draw();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,  barTop + height, 0.0D).tex(0.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarRight, barTop + height, 0.0D).tex(1.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarRight, barTop,          0.0D).tex(1.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,  barTop,          0.0D).tex(0.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.draw();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,      barTop + height - 1, 0.0D).tex(0.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarRight - 1, barTop,              0.0D).tex(1.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,      barTop,              0.0D).tex(0.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.draw();
        }

        drawScreen(mouseX, mouseY);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
