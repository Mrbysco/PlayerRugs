package uk.kihira.playerrugs.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import java.util.List;
import java.util.function.Consumer;

public class PlayerRugItem extends BlockItem {

    public PlayerRugItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
        if (resolvableprofile != null) {
            if (!resolvableprofile.isResolved() || !resolvableprofile.properties().containsKey("textures")) {
                PlayerRugBlockEntity.resolve(resolvableprofile).thenAcceptAsync(profile ->
                        stack.set(DataComponents.PROFILE, profile), PlayerRugBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
            }
        }
    }

    @SuppressWarnings("removal")
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        MutableComponent playerComponent = Component.translatable("playerrugs.tooltip", Component.literal("None").withStyle(ChatFormatting.RED));
        if (stack.has(DataComponents.PROFILE)) {
            ResolvableProfile profile = stack.get(DataComponents.PROFILE);
            if (profile != null && profile.name().isPresent()) {
                playerComponent = Component.translatable("playerrugs.tooltip",
                        Component.literal(profile.name().get()).withStyle(ChatFormatting.YELLOW));
            }

        }
        tooltipComponents.add(playerComponent);
    }

}
