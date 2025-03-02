package uk.kihira.playerrugs.common.blockentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import uk.kihira.playerrugs.common.RugRegistry;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class PlayerRugBlockEntity extends BlockEntity {
    @Nullable
    private static GameProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;

    private GameProfile playerProfile;
    private boolean isSlim = false;

    public PlayerRugBlockEntity(BlockPos pos, BlockState state) {
        super(RugRegistry.PLAYER_RUG_BLOCK_ENTITY.get(), pos, state);
    }

    public static void setup(GameProfileCache gameProfileCache, MinecraftSessionService service, Executor executor) {
        profileCache = gameProfileCache;
        sessionService = service;
        mainThreadExecutor = executor;
    }

    public static void setup(Services services, Executor executor) {
        setup(services.profileCache(), services.sessionService(), executor);
    }

    public static void clear() {
        profileCache = null;
        sessionService = null;
        mainThreadExecutor = null;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("PlayerProfile", 10)) {
            this.setPlayerProfile(NbtUtils.readGameProfile(compound.getCompound("PlayerProfile")));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.playerProfile != null) {
            CompoundTag tag = new CompoundTag();
            NbtUtils.writeGameProfile(tag, this.playerProfile);
            compound.put("PlayerProfile", tag);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag compoundNBT = pkt.getTag();
        handleUpdateTag(compoundNBT);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public CompoundTag getPersistentData() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().offset(-1, -1, -1), getBlockPos().offset(1, 1, 1));
    }

    /**
     * GameProfile stuff. It's safer to do it than to call the Skull BlockEntity. (Tends to crash from experience)
     */

    @Nullable
    public GameProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public boolean isSlim() {
        return this.isSlim;
    }

    public void setPlayerProfile(@Nullable GameProfile profile) {
        synchronized (this) {
            this.playerProfile = profile;
            if (this.level != null && this.level.isClientSide && this.playerProfile != null && this.playerProfile.isComplete()) {
                Minecraft.getInstance().getSkinManager().registerSkins(this.playerProfile, (textureType, textureLocation, profileTexture) -> {
                    if (textureType.equals(MinecraftProfileTexture.Type.SKIN)) {
                        String metadata = profileTexture.getMetadata("model");
                        this.isSlim = metadata != null && metadata.equals("slim");
                    }
                }, true);
            }
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        updateGameProfile(this.playerProfile, (profile) -> {
            this.playerProfile = profile;
            this.setChanged();
        });
    }

    public static void updateGameProfile(@Nullable GameProfile profile, Consumer<GameProfile> profileConsumer) {
        if (profile != null && !StringUtil.isNullOrEmpty(profile.getName()) && (!profile.isComplete() || !profile.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
            profileCache.getAsync(profile.getName(), (gameProfile) -> {
                Util.backgroundExecutor().execute(() -> {
                    Util.ifElse(gameProfile, (gameProfile1) -> {
                        Property property = Iterables.getFirst(gameProfile1.getProperties().get("textures"), (Property) null);
                        if (property == null) {
                            gameProfile1 = sessionService.fillProfileProperties(gameProfile1, true);
                        }

                        GameProfile gameprofile = gameProfile1;
                        mainThreadExecutor.execute(() -> {
                            profileCache.add(gameprofile);
                            profileConsumer.accept(gameprofile);
                        });
                    }, () -> {
                        mainThreadExecutor.execute(() -> {
                            profileConsumer.accept(profile);
                        });
                    });
                });
            });
        } else {
            profileConsumer.accept(profile);
        }
    }
}
