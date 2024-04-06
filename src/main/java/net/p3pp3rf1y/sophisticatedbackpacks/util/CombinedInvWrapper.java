package net.p3pp3rf1y.sophisticatedbackpacks.util;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CombinedInvWrapper<S extends SlottedStackStorage> extends CombinedSlottedStorage<ItemVariant, S> implements SlottedStackStorage {
    protected final int[] baseIndex;
    protected final int slotCount;

    public CombinedInvWrapper(List<S> inventories) {
        super(inventories);

        this.baseIndex = new int[inventories.size()];
        int index = 0;
        for (int i = 0; i < inventories.size(); i++) {
            index += inventories.get(i).getSlotCount();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    @SafeVarargs
    public CombinedInvWrapper(S... inventories) {
        this(List.of(inventories));
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected Optional<SlottedStackStorage> getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= parts.size())
        {
            return Optional.empty();
        }
        return Optional.of(parts.get(index));
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return;
        }
        slot = getSlotFromIndex(slot, index);
        handler.get().setStackInSlot(slot, stack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return ItemStack.EMPTY;
        }
        slot = getSlotFromIndex(slot, index);
        return handler.get().getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return 0;
        }
        int localSlot = getSlotFromIndex(slot, index);
        return handler.get().getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, ItemVariant resource, int count) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return false;
        }
        int localSlot = getSlotFromIndex(slot, index);
        return handler.get().isItemValid(localSlot, resource, count);
    }

    @Override
    public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return 0;
        }
        slot = getSlotFromIndex(slot, index);
        return handler.get().insertSlot(slot, resource, maxAmount, ctx);
    }

    @Override
    public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
        int index = getIndexForSlot(slot);
        Optional<SlottedStackStorage> handler = getHandlerFromIndex(index);
        if (handler.isEmpty()) {
            return 0;
        }
        slot = getSlotFromIndex(slot, index);
        return handler.get().extractSlot(slot, resource, maxAmount, ctx);
    }
}
