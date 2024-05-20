# Changelog

### General
- Added fabric version of the logo and used it in `fabric.mod.json`.

### Improvements
- Changed the way Litematica support works by adding the trinket slots as a container in the `getInventoryItemCounts` function.
- Moved Storage Wrappers to `SophisticatedCore` for better compatibility between sophisticated mods. *(Reverted later)*

### Fixes
- Fixed an issue where the Tool Swapper caused item loss when the backpack was full.
- Fixed incompatibility with repurposed structures.
- Fixed server incompatibility with reworked Litematica compatibility.
- Reworked Litematica compatibility due to reverting the Storage Wrapper move commits.
- Completely reworked Litematica compatibility.
- Disabled `isSameThread` requirement as it stopped the StorageWrapper from getting the right data.
- Attempted to fix a `ConcurrentModificationException`.