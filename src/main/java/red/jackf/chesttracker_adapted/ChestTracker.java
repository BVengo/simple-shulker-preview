package red.jackf.chesttracker_adapted;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChestTracker {
    public static final Logger LOGGER = LogManager.getLogger("ChestTracker Adaptation (Simple Shulker Preview)");
    public static final String MODID = "chesttracker_adapted";

    // public static final ChestTrackerConfig CONFIG =
    // AutoConfig.register(ChestTrackerConfig.class,
    // JanksonConfigSerializer::new).getConfig();
    public static final ConfigOptions.ChestTrackerAdaptedOptions CONFIG = AutoConfig
            .getConfigHolder(ConfigOptions.class).getConfig().databaseOptions;

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    public static void sendDebugMessage(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player != null)
            player.sendMessage(Text.literal("[ChestTracker] ").formatted(Formatting.YELLOW).append(text));
    }

    public static int getSquareSearchRange() {
        int blockValue = sliderValueToRange(ChestTracker.CONFIG.searchRange);
        if (blockValue == Integer.MAX_VALUE)
            return blockValue;
        return blockValue * blockValue;
    }

    public static int sliderValueToRange(int sliderValue) {
        if (sliderValue <= 16) {
            return 15 + sliderValue;
        } else if (sliderValue <= 32) {
            return 30 + ((sliderValue - 16) * 2);
        } else if (sliderValue <= 48) {
            return 60 + ((sliderValue - 32) * 4);
        } else if (sliderValue <= 64) {
            return 120 + ((sliderValue - 48) * 8);
        } else if (sliderValue <= 80) {
            return 240 + ((sliderValue - 64) * 16);
        } else if (sliderValue <= 97) {
            return 480 + ((sliderValue - 80) * 32);
        }
        return Integer.MAX_VALUE;
    }
}
