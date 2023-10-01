package com.bvengo.simpleshulkerpreview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bvengo.simpleshulkerpreview.config.ConfigOptions;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import red.jackf.chesttracker_adapted.memory.MemoryDatabase;
import red.jackf.chesttracker_adapted.memory.MemoryUtils;

/**
 * Simple Shulker Preview Mod
 */
@Environment(EnvType.CLIENT)
public class SimpleShulkerPreviewMod implements ClientModInitializer {
	public static final String MOD_ID = "simpleshulkerpreview";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info(LOGGER.getName() + " loading...");

		AutoConfig.register(ConfigOptions.class, GsonConfigSerializer::new);

		// #region ChestTracker.onInitializeClient();
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
		// #endregion

		LOGGER.info(LOGGER.getName() + " loaded.");
	}
}
