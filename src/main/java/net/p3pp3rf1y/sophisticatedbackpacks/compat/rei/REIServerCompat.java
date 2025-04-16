package net.p3pp3rf1y.sophisticatedbackpacks.compat.rei;

import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;

import java.util.function.Supplier;

public class REIServerCompat implements REIServerPlugin {
    @Override
    public double getPriority() {
        return 0D;
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        EntryComparator<ItemStack> colorTag = (context, stack) -> {
			IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
            var builder = DataComponentMap.builder();
            builder.set(ModCoreDataComponents.MAIN_COLOR.get(), wrapper.getMainColor());
            builder.set(ModCoreDataComponents.ACCENT_COLOR.get(), wrapper.getAccentColor());
            return EntryComparator.component().hash(context, new PatchedDataComponentMap(builder.build()));
        };

        registry.register(colorTag, ModItems.BACKPACKS.stream().map(Supplier::get).toArray(BackpackItem[]::new));
    }
}
