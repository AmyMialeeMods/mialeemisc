package xyz.amymialee.mialeemisc.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class RegistryDumperItem extends Item {
    public RegistryDumperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            ItemStack otherStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
            Registry.ITEM.forEach(item -> {
                if (Objects.equals(Registry.ITEM.getId(item).getNamespace(), Registry.ITEM.getId(otherStack.getItem()).getNamespace())) {
                    Identifier identifier = Registry.ITEM.getId(item);
                    System.out.println("\"" + identifier.getNamespace() + ":" + identifier.getPath() + "\",");
                }
            });
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}