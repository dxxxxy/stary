package studio.dreamys.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface AccessMinecraft {
    @Accessor //running is volatile, cannot use at
    boolean getRunning();
}
