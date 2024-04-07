package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client.accessor;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VertexFormat.class)
public interface VertexFormatAccessor {
	@Accessor("offsets")
	IntList getOffsets();
}
