package uk.kihira.playerrugs.common.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class PlayerRugItem extends BlockItem {

    public PlayerRugItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag tag) {
        super.verifyTagAfterLoad(tag);
        if (tag.contains("PlayerProfile")) {
            GameProfile gameprofile = NbtUtils.readGameProfile(tag.getCompound("PlayerProfile"));
            PlayerRugBlockEntity.updateGameProfile(gameprofile, (profile) -> {
                tag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
            });
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new PlayerRugInventoryRenderer(new BlockEntityRendererProvider.Context(
                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getBlockRenderer(),
                        Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels(),
                        Minecraft.getInstance().font
                ));
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.hasTag() && stack.getTag() != null) {
            final GameProfile profile = NbtUtils.readGameProfile(stack.getTag().getCompound("PlayerProfile"));
            tooltip.add(Component.literal("Player: " + (profile != null ? profile.getName() : "None")));
        }
    }
}
