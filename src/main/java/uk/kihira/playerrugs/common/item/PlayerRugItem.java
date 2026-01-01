package uk.kihira.playerrugs.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class PlayerRugItem extends BlockItem {

    public PlayerRugItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        MutableComponent playerComponent = Component.translatable("playerrugs.tooltip", Component.literal("None").withStyle(ChatFormatting.RED));
        if (stack.has(DataComponents.PROFILE)) {
            ResolvableProfile profile = stack.get(DataComponents.PROFILE);
            if (profile != null && profile.name().isPresent()) {
                playerComponent = Component.translatable("playerrugs.tooltip",
                        Component.literal(profile.name().get()).withStyle(ChatFormatting.YELLOW));
            }

        }
        tooltipAdder.accept(playerComponent);
    }

}
