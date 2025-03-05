package uk.kihira.playerrugs.common.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerRugItem extends BlockItem {

    public PlayerRugItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag tag) {
        super.verifyTagAfterLoad(tag);
        //TagType 8 is a string, 10 is a compound
        if (tag.contains("PlayerProfile", 8) && !Util.isBlank(tag.getString("PlayerProfile"))) {
            GameProfile gameprofile = new GameProfile((UUID) null, tag.getString("PlayerProfile"));
            SkullBlockEntity.updateGameprofile(gameprofile, (profile) -> {
                tag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
            });
        } else if (tag.contains("PlayerProfile", 10)) {
            GameProfile gameprofile = NbtUtils.readGameProfile(tag.getCompound("PlayerProfile"));
            SkullBlockEntity.updateGameprofile(gameprofile, (profile) -> {
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
        CompoundTag tag = stack.getTag();
        MutableComponent playerComponent = Component.translatable("playerrugs.tooltip", Component.literal("None").withStyle(ChatFormatting.RED));
        if (tag != null) {
            if (tag.contains("PlayerProfile", 8)) {
                playerComponent = Component.translatable("playerrugs.tooltip",
                        Component.literal(tag.getString("PlayerProfile")).withStyle(ChatFormatting.YELLOW));
            } else {
                CompoundTag profile = tag.getCompound("PlayerProfile");
                String name = "None";
                if (profile.contains("Name", 8)) {
                    name = profile.getString("Name");
                }
                playerComponent = Component.translatable("playerrugs.tooltip",
                        Component.literal(name).withStyle(ChatFormatting.YELLOW));
            }
        }
        tooltip.add(playerComponent);
    }
}
