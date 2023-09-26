package red.jackf.chesttracker_adapted;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import red.jackf.chesttracker_adapted.memory.MemoryDatabase;
import red.jackf.chesttracker_adapted.memory.MemoryUtils;

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

    public static void onInitializeClient() {

        // Save if someone just decides to X out of craft
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            MemoryDatabase database = MemoryDatabase.getCurrent();
            if (database != null)
                database.save();
        });

        // Checking for memories that are still alive
        ClientTickEvents.END_WORLD_TICK.register(MemoryUtils::checkValidCycle);

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if (world.isClient) {
                Block hit = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
                if (MemoryUtils.isValidInventoryHolder(hit, world, blockHitResult.getBlockPos())) {
                    MemoryUtils.setLatestPos(blockHitResult.getBlockPos());
                    MemoryUtils.setWasEnderchest(hit == Blocks.ENDER_CHEST);
                } else {
                    MemoryUtils.setLatestPos(null);
                    MemoryUtils.setWasEnderchest(false);
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) {
                MemoryUtils.setLatestPos(null);
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) {
                MemoryUtils.setLatestPos(null);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }
}
