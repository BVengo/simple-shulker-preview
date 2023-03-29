# Simple Shulker Preview

Simple Shulker Preview is a small fabric mod that displays an icon indicating the contents of a shulker. This icon appears on top of the shulker icon in inventory slots.


![Example image](src/main/resources/assets/simpleshulkerpreview/example.png)

For other download pages, please see [CurseForge](https://www.curseforge.com/minecraft/mc-mods/simple-shulker-preview) or [Modrinth](https://modrinth.com/mod/simple-shulker-preview).

## Configs
| **Option**          | **Description**                                                               | **Default** |
|---------------------|-------------------------------------------------------------------------------|-------------|
| `Display Item`      | Sets which item in the shulker should be displayed.                           | _First_     |
| `Group Enchantments`| Group enchantments to treat enchanted / un-enchanted items separately         | _False_     |
| `X Offset`          | The horizontal offset of the icon, from left to right.                        | 12.0        |
| `Y Offset`          | The vertical offset of the icon, from top to bottom.                          | 12.0        |
| `Z Offset`          | In/out offset. Use when other mods cover / are covered by this.               | 100.0       |
| `Scale`             | Size of the icon.                                                             | 10.0        |
| `Disable Mod`       | Disables the mod so that overlay icons are not displayed.                     | _False_     |

**Display Item Options:**
- First - the first item in the shulker box
- Last - the last item in the shulker box
- Unique - only displays an icon if there is exactly one type of item in the shulker box. Enchantments / names / durabilities are ignored.
- Most - the item type with the most items. This is based on stack size, not stack count. e.g. 64 stone (stacked) and 2 diamond swords (unstacked) will show the stone.
- Least - the item type with the least items. This is based on stack size, not stack count. e.g. 64 stone (stacked) and 2 diamond swords (unstacked) will show the swords.

## Mod Compatibility Configs
Settings for compatibility with other mod functionalities. All default false.
| **Option**                   | **Description**                                                                  |
|------------------------------|----------------------------------------------------------------------------------|
| `Support Custom Heads`       | Displays the textures of custom player heads and treats them as individual items |
| `Support Recursive Shulkers` | If shulkers are stored in shulkers, counts the items in them too. |
| `Support Stacked Shulkers`   | Displays the preview even for shulkers that are stacked. Adds additional x, y, z offsets, and scale to prevent overlapping with the shulker count.                          |

This mod comes with the [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) built in to 
implement configs (there should be no need to download it yourself). I would recommend installing [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu) alongside it if you actually want to use them though, because I didn't want to create yet another config mapping for such a simple mod.

Please keep in mind that, although Simple Shulker Preview may be available for certain versions, that doesn't always mean that ModMenu will be too. If you are desperate to edit the configs, they are stored in configs/simpleshulkerpreview.json.

## Contributing
Contributions (especially when updating) are welcome! I'm quite busy and so can't always update the mod within a reasonable timeframe of the latest minecraft updates.
