/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.armouredanimals.mixin;

import com.deflatedpickle.armouredanimals.client.AnimalArmourFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"rawtypes", "UnusedMixin", "unchecked"})
@Mixin(MobEntityRenderer.class)
public abstract class MixinMobEntityRenderer extends LivingEntityRenderer {
  public MixinMobEntityRenderer(
      EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
    super(ctx, model, shadowRadius);
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  public void addArmourFeature(
      EntityRendererFactory.Context context, EntityModel entityModel, float f, CallbackInfo ci) {
    addFeature(new AnimalArmourFeatureRenderer(context, this, entityModel));
  }
}
