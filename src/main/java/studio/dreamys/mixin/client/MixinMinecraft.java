package studio.dreamys.mixin.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.dreamys.util.RenderUtils;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    private long lastFrame = getTime();

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(CallbackInfo callbackInfo) {
        long currentTime = getTime();
        int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtils.deltaTime = deltaTime;
    }

    @Overwrite //by default, main menu limit is 30
    public int getLimitFramerate() {
        return Minecraft.getMinecraft().gameSettings.limitFramerate;
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
}
