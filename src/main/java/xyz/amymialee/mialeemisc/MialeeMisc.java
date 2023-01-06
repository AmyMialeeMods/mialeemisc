package xyz.amymialee.mialeemisc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialeemisc.itemgroup.MialeeItemGroup;
import xyz.amymialee.mialeemisc.items.IClickConsumingItem;
import xyz.amymialee.mialeemisc.util.MialeeMath;

public class MialeeMisc implements ModInitializer {
    public static final String MOD_ID = "mialeemisc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier clickConsume = id("click_consume");
    public static final Identifier targetPacket = id("target");
    public static final TagKey<Item> DAMAGE_IMMUNE = TagKey.of(Registry.ITEM_KEY, id("damage_immune"));

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(clickConsume, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            minecraftServer.execute(() -> {
                if (serverPlayer.getMainHandStack().getItem() instanceof IClickConsumingItem item) {
                    item.mialeeMisc$doAttack(serverPlayer);
                }
            });
        });

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.info("Loaded in development environment.");
            MialeeItemGroup.create(id(MOD_ID, "mialee_group"))
                    .setIcon((i) -> {
                        Item item = Registry.ITEM.get(MialeeMath.clampLoop(i / 2 + 1, 1, Registry.ITEM.size()));
                        return new ItemStack(item);
                    })
                    .setItems((itemStacks, itemGroup) -> {
                        for(Item item : Registry.ITEM) {
                            if (item == Items.AIR) continue;
                            itemStacks.add(item.getDefaultStack());
                        }
                    });
        }
    }

    public static ItemStack enchantStack(ItemStack stack, Enchantment enchantment, int level) {
        stack.addEnchantment(enchantment, level);
        return stack;
    }

    public static <T> Registry<T> createRegistry(Identifier id, Class<T> clazz) {
        return FabricRegistryBuilder.createSimple(clazz, id).buildAndRegister();
    }

    public static Identifier id(String ... path) {
        return namedId(MOD_ID, path);
    }

    public static Identifier namedId(String namespace, String ... path) {
        return new Identifier(namespace, String.join(".", path));
    }
}