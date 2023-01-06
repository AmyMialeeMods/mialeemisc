package xyz.amymialee.mialeemisc.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialeemisc.items.ICustomCooldownsItem;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    public void mialeeMisc$cooldownOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null && stack.getItem() instanceof ICustomCooldownsItem item) {
            Item[] array = item.mialeeMisc$getCooldownItems();
            for (int i = 0; i < array.length; i++) {
                float progress = clientPlayerEntity.getItemCooldownManager().getCooldownProgress(array[i], MinecraftClient.getInstance().getTickDelta());
                if (progress > 0.0f) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableTexture();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tessellator tessellator2 = Tessellator.getInstance();
                    BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
                    this.renderGuiQuad(bufferBuilder2, x + MathHelper.ceil(16f / array.length) * i, y + MathHelper.floor(16.0f * (1.0f - progress)), MathHelper.ceil(16f / array.length), MathHelper.ceil(16.0f * progress), 255, 255, 255, 127);
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;getCooldownProgress(Lnet/minecraft/item/Item;F)F"))
    public void mialeeMisc$cooldownStopper(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        if (clientPlayerEntity != null && stack.getItem() instanceof ICustomCooldownsItem item) {
            ci.cancel();
        }
    }
}