package com.bvengo.simpleshulkerpreview.config;

/**
 * Whether to use the custom name to determine the icon
 * PREFER - prefer the custom name if valid, otherwise use the slot option
 * ALWAYS - always use the custom name, otherwise don't display
 * NEVER - never use the custom name
 */
public enum CustomNameOption {
    ALWAYS,
    PREFER,
    NEVER;
}
