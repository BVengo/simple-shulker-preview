package com.bvengo.simpleshulkerpreview.config;

/**
 * Which slot of the container should be displayed
 * FIRST - the first item available in the container
 * LAST - the last item available in the container
 * UNIQUE - only display if there is one item type in the container
 * MOST - displays which item there is the most of in the container
 */
public enum IconDisplayOption {
    FIRST,
    LAST,
    UNIQUE,
    MOST,
    LEAST;
}
