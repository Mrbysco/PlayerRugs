package uk.kihira.playerrugs.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;

import java.util.Optional;

public class PlayerRugBlockEntity extends BlockEntity {
    @Nullable
    private ResolvableProfile playerProfile;

    public PlayerRugBlockEntity(BlockPos pos, BlockState state) {
        super(RugRegistry.PLAYER_RUG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput input) {
	    super.loadAdditional(input);

	    Optional<ResolvableProfile> optionalProfile = input.read("profile", ResolvableProfile.CODEC);
	    optionalProfile.ifPresent(this::setPlayerProfile);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
	    super.saveAdditional(output);
        if (this.playerProfile != null) {
	        output.store("profile", ResolvableProfile.CODEC, this.playerProfile);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
	    super.applyImplicitComponents(getter);
        this.setPlayerProfile(getter.get(DataComponents.PROFILE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.PROFILE, this.playerProfile);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
	    super.removeComponentsFromTag(output);
	    output.discard("profile");
    }

	@Override
	public void onDataPacket(Connection net, ValueInput valueInput) {
		super.onDataPacket(net, valueInput);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(PlayerRugs.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(PlayerRugs.LOGGER)) {
			HolderLookup.Provider lookupProvider = this.level != null ? this.level.registryAccess() : VanillaRegistries.createLookup();
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
        try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(PlayerRugs.LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, registries);
            saveCustomOnly(output);
            removeComponentsFromTag(output);

            BlockItem.setBlockEntityData(stack, this.getType(), output);
            stack.applyComponents(this.collectComponents());
        }
    }

    /**
     * GameProfile stuff. It's safer to do it than to call the Skull BlockEntity. (Tends to crash from experience)
     */

    @Nullable
    public ResolvableProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void setPlayerProfile(@Nullable ResolvableProfile profile) {
        this.playerProfile = profile;
    }

	public boolean isStanding() {
		return getBlockState().getValue(PlayerRugBlock.STANDING).booleanValue();
	}
}
