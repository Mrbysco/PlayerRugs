package uk.kihira.playerrugs.common.items;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerRugItem extends BlockItem {

    public PlayerRugItem(Block block, Item.Properties builder) {
        super(block, builder.setISTER(() -> PlayerRugInventoryRenderer::new));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag() && stack.getTag() != null) {
            final GameProfile profile = NBTUtil.readGameProfile(stack.getTag().getCompound("PlayerProfile"));
            tooltip.add(new StringTextComponent("Player: " + (profile != null ? profile.getName() : "None")));
        }
    }
}
