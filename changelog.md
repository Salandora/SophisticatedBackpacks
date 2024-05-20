# Changelog

### General
- Added fabric version of the logo and used it in `fabric.mod.json`.

### Improvements
- Moved Litematica support to `SophisticatedCore`.
- Moved Storage Wrappers to `SophisticatedCore` for better compatibility between sophisticated mods. *(Reverted later)*

### Fixes
- Fixed typo.
- Fixed rendering of experience.
- Fixed chests item tag.
- Fixed server incompatibility with reworked Litematica compatibility.
- Reworked Litematica compatibility due to reverting the Storage Wrapper move commits.
- Completely reworked Litematica compatibility.
- Placing backpacks in creative mode no longer removes items or limits counts to 64 due to wrong handling in the `copyTo` function. Fixes [Salandora/SophisticatedBackpacks#7](https://github.com/Salandora/SophisticatedBackpacks/issues/7).
- Battery upgrade can now be charged or discharged correctly.
- `insertIntoStorage` now returns the inserted amount, not the remaining. This aligns with Fabric's approach to handling insertion/extraction.
- Properly implemented `getSlot` in `ControllerBlockEntityBase`.
- Selecting a recipe with a non-empty crafting grid no longer causes an infinite loop.
- Checked for `null` in `soundEvent`, preventing errors from broken records in a jukebox upgrade.