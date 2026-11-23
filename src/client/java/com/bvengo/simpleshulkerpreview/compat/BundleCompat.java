package com.bvengo.simpleshulkerpreview.compat;

import com.bvengo.simpleshulkerpreview.SimpleShulkerPreviewMod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

/**
 * Data component compatibility layer for Bundles.
 * Handles BundleContents changes between 26.2 & 26.3.
 */
public final class BundleCompat {

    // itemCopies in 26.3, itemCopyStream in 26.2
    private static final Method GET_ITEMS;

    private static final BundleCreator BUNDLE_CREATOR;

    // Track errors so we only alert once
    private static boolean loggedGetError = false;
    private static boolean loggedCreateError = false;

    static {
        Method getItems = null;
        BundleCreator creator = null;

        try {
            try {
                getItems = BundleContents.class.getMethod("itemCopies");
            } catch (NoSuchMethodException e) {
                getItems = BundleContents.class.getMethod("itemCopyStream");
            }

            // 26.3: copyWithContents(Stream) on BundleContents directly
            try {
                Method copyWith = BundleContents.class.getMethod("copyWithContents", Stream.class);
                creator = items -> {
                    return (BundleContents) copyWith.invoke(BundleContents.EMPTY, items.stream());
                };
            } catch (NoSuchMethodException e) {
                // <=26.2
                Constructor<?> ctor = BundleContents.Mutable.class.getConstructor(BundleContents.class);
                Method insert = BundleContents.Mutable.class.getMethod("tryInsert", ItemStack.class);
                Method toImmutable = BundleContents.Mutable.class.getMethod("toImmutable");

                creator = items -> {
                    Object mutable = ctor.newInstance(BundleContents.EMPTY);
                    for (ItemStack item : items) {
                        insert.invoke(mutable, item.copy());
                    }
                    return (BundleContents) toImmutable.invoke(mutable);
                };
            }
        } catch (Exception e) {
            SimpleShulkerPreviewMod.LOGGER.warn(
                    "[BundleCompat] Init failed; bundle features may be limited", e);
        }

        GET_ITEMS = getItems;
        BUNDLE_CREATOR = creator;
    }

    private BundleCompat() {}

    /**
     * Creates a BundleContents from a list of item stacks.
     * Falls back to {@link BundleContents#EMPTY} if there's
     * an issue with the compat layer.
     */
    public static BundleContents createBundleContents(List<ItemStack> items) {
        if (BUNDLE_CREATOR == null) return BundleContents.EMPTY;

        try {
            return BUNDLE_CREATOR.create(items);
        } catch (Exception e) {
            if (!loggedCreateError) {
                SimpleShulkerPreviewMod.LOGGER.warn(
                        "[BundleCompat] createBundleContents failed; future failures hidden", e);
                loggedCreateError = true;
            }
            return BundleContents.EMPTY;
        }
    }

    /**
     * Returns the items inside a bundle as a stream of item stack copies.
     * Falls back to an empty stream if there's an issue with the compat layer.
     */
    @SuppressWarnings("unchecked")
    public static Stream<ItemStack> getBundleItemCopies(BundleContents bundle) {
        if (GET_ITEMS == null || bundle == null) return Stream.empty();

        try {
            return (Stream<ItemStack>) GET_ITEMS.invoke(bundle);
        } catch (Exception e) {
            if (!loggedGetError) {
                SimpleShulkerPreviewMod.LOGGER.warn(
                        "[BundleCompat] getBundleItemCopies failed; future failures hidden", e);
                loggedGetError = true;
            }
            return Stream.empty();
        }
    }

    @FunctionalInterface
    private interface BundleCreator {
        BundleContents create(List<ItemStack> items) throws Exception;
    }
}
