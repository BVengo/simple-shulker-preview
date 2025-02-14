package com.bvengo.simpleshulkerpreview.container;

import net.minecraft.item.ItemStack;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerManagerCache {
	private static final Map<ItemStack, CacheEntry> cache = new ConcurrentHashMap<>();

	private static class CacheEntry {
		final ContainerManager containerManager;
		volatile Instant lastAccessed;

		CacheEntry(ContainerManager containerManager) {
			this.containerManager = containerManager;
			this.lastAccessed = Instant.now();
		}

		void updateAccessTime() {
			this.lastAccessed = Instant.now();
		}
	}

	public static ContainerManager getContainerManager(ItemStack stack) {
		CacheEntry entry = cache.computeIfAbsent(stack, s -> new CacheEntry(new ContainerManager(s)));
		entry.updateAccessTime();
		cleanUp();
		System.out.println("Cache size: " + cache.size());
		return entry.containerManager;
	}

	public static void cleanUp() {
		Instant now = Instant.now();
		Iterator<Map.Entry<ItemStack, CacheEntry>> iterator = cache.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<ItemStack, CacheEntry> entry = iterator.next();
			if (entry.getValue().lastAccessed.plusSeconds(1).isBefore(now)) {
				iterator.remove();
			}
		}
	}
}