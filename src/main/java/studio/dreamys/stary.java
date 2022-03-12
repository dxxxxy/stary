package studio.dreamys;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import studio.dreamys.font.Fonts;

@Mod(modid = stary.MODID, version = stary.VERSION, name = stary.NAME)
public class stary {
    public static final String MODID = "stary";
    public static final String VERSION = "1.0";
    public static final String NAME = "stary";
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Fonts.loadFonts();
    }
}
