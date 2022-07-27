# Simple Shulker Preview

Simple Shulker Preview is a small fabric mod that displays an icon indicating the contents of a shulker. This icon appears on top of the shulker icon in inventory slots.


![Example image](src/main/resources/assets/simpleshulkerpreview/example.png)

For other download pages, please see [CurseForge](https://www.curseforge.com/minecraft/mc-mods/simple-shulker-preview) or [Modrinth](https://modrinth.com/mod/simple-shulker-preview).

## Configs
| **Option**          | **Description**                                                               | **Default** |
|---------------------|-------------------------------------------------------------------------------|-------------|
| `Disable Mod`       | Disables the mod so that overlay icons are not displayed.                     | _False_     |
| `Unique Items Only` | Only displays icons on shulker boxes holding one item type.                   | _False_     |
| `Display Item`      | Sets which item in the shulker should be displayed. Options are First or Last | _First_     |

This mod comes with the [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) built in to 
implement configs (there should be no need to download it yourself). I would recommend installing [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)
alongside it if you actually want to use them though, because I didn't want to create yet another config mapping for 
such a simple mod.

Please keep in mind that, although Simple Shulker Preview may be available for certain 
versions, that doesn't always mean that ModMenu will be too. If you are desperate to edit the configs, 
they are stored in configs/simpleshulkerpreview.json.

## Compiling
1. Clone the repository and open it in a terminal or IDE
2. Run `./gradlew build`
3. Find the built jar file in build/libs/

## Installing
1. Make sure fabric is installed and setup. Download and installation instructions can be found at https://fabricmc.net/
2. Compile or download the mod jar file for Simple Shulker Preview
3. Move the jar file into your minecraft mods folder (generated the first time you run the fabric version of minecraft)
