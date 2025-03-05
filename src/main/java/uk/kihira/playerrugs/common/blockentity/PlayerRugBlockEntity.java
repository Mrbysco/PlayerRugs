package uk.kihira.playerrugs.common.blockentity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.RugRegistry;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class PlayerRugBlockEntity extends BlockEntity {
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCacheByName;
    @Nullable
    private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> profileCacheById;
    public static final Executor CHECKED_MAIN_THREAD_EXECUTOR = runnable -> {
        Executor executor = mainThreadExecutor;
        if (executor != null) {
            executor.execute(runnable);
        }
    };

    @Nullable
    private ResolvableProfile playerProfile;

    public PlayerRugBlockEntity(BlockPos pos, BlockState state) {
        super(RugRegistry.PLAYER_RUG_BLOCK_ENTITY.get(), pos, state);
    }

    public static void setup(final Services services, Executor p_mainThreadExecutor) {
        mainThreadExecutor = p_mainThreadExecutor;
        final BooleanSupplier booleansupplier = () -> profileCacheById == null;
        profileCacheByName = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10L))
                .maximumSize(256L)
                .build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
                    public CompletableFuture<Optional<GameProfile>> load(String username) {
                        return PlayerRugBlockEntity.fetchProfileByName(username, services);
                    }
                });
        profileCacheById = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10L))
                .maximumSize(256L)
                .build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>() {
                    public CompletableFuture<Optional<GameProfile>> load(UUID id) {
                        return PlayerRugBlockEntity.fetchProfileById(id, services, booleansupplier);
                    }
                });
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String name, Services services) {
        return services.profileCache()
                .getAsync(name)
                .thenCompose(
                        optionalProfile -> {
                            LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheById;
                            return loadingcache != null && !optionalProfile.isEmpty()
                                    ? loadingcache.getUnchecked(optionalProfile.get().getId())
                                    .thenApply(profile -> profile.or(() -> optionalProfile))
                                    : CompletableFuture.completedFuture(Optional.empty());
                        }
                );
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileById(UUID id, Services services, BooleanSupplier cacheUninitialized) {
        return CompletableFuture.supplyAsync(() -> {
            if (cacheUninitialized.getAsBoolean()) {
                return Optional.empty();
            } else {
                ProfileResult profileresult = services.sessionService().fetchProfile(id, true);
                return Optional.ofNullable(profileresult).map(ProfileResult::profile);
            }
        }, Util.backgroundExecutor());
    }

    public static void clear() {
        mainThreadExecutor = null;
        profileCacheByName = null;
        profileCacheById = null;
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compound, lookupProvider);

        if (compound.contains("profile")) {
            ResolvableProfile.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("profile"))
                    .resultOrPartial(error -> PlayerRugs.LOGGER.error("Failed to load profile from player rug: {}", error))
                    .ifPresent(this::setPlayerProfile);
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compound, lookupProvider);
        if (this.playerProfile != null) {
            compound.put("profile", ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, this.playerProfile).getOrThrow());
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.setPlayerProfile(input.get(DataComponents.PROFILE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.PROFILE, this.playerProfile);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("profile");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag compoundNBT = pkt.getTag();
        handleUpdateTag(compoundNBT, lookupProvider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt, lookupProvider);
        return nbt;
    }

    @Override
    public CompoundTag getPersistentData() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt, level != null ? level.registryAccess() : VanillaRegistries.createLookup());
        return nbt;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return new AABB(getBlockPos().offset(-1, -1, -1), getBlockPos().offset(1, 1, 1));
//    }

    /**
     * GameProfile stuff. It's safer to do it than to call the Skull BlockEntity. (Tends to crash from experience)
     */

    @Nullable
    public ResolvableProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void setPlayerProfile(@Nullable ResolvableProfile profile) {
        synchronized (this) {
            this.playerProfile = profile;
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        if (this.playerProfile != null && !this.playerProfile.isResolved()) {
            resolve(this.playerProfile).thenAcceptAsync(profile -> {
                this.playerProfile = profile;
                this.setChanged();
            }, CHECKED_MAIN_THREAD_EXECUTOR);
        } else {
            this.setChanged();
        }
    }

    public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String profileName) {
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheByName;
        return loadingcache != null && StringUtil.isValidPlayerName(profileName)
                ? loadingcache.getUnchecked(profileName)
                : CompletableFuture.completedFuture(Optional.empty());
    }

    public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(UUID profileUuid) {
        LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCacheById;
        return loadingcache != null ? loadingcache.getUnchecked(profileUuid) : CompletableFuture.completedFuture(Optional.empty());
    }

    public static CompletableFuture<ResolvableProfile> resolve(ResolvableProfile resolvableProfile) {
        if (resolvableProfile.isResolved()) {
            return CompletableFuture.completedFuture(resolvableProfile);
        } else {
            return resolvableProfile.id().isPresent() ? fetchGameProfile(resolvableProfile.id().get()).thenApply(profile -> {
                GameProfile gameprofile = profile.orElseGet(() -> new GameProfile(resolvableProfile.id().get(), resolvableProfile.name().orElse("")));
                return new ResolvableProfile(gameprofile);
            }) : fetchGameProfile(resolvableProfile.name().orElseThrow()).thenApply(profile -> {
                GameProfile gameprofile = profile.orElseGet(() -> new GameProfile(Util.NIL_UUID, resolvableProfile.name().get()));
                return new ResolvableProfile(gameprofile);
            });
        }
    }
}
