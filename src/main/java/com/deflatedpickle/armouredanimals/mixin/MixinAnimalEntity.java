/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.armouredanimals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin", "rawtypes"})
@Mixin(AnimalEntity.class)
public abstract class MixinAnimalEntity extends MobEntity {
  protected MixinAnimalEntity(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
    if (player.world.isClient) return ActionResult.PASS;

    var stack = player.getStackInHand(hand);
    var slot = LivingEntity.getPreferredEquipmentSlot(stack);
    var equipped = getEquippedStack(slot);

    if (slot != EquipmentSlot.HEAD) return super.interactAt(player, hitPos, hand);
    if (stack.getItem() == equipped.getItem()) return super.interactAt(player, hitPos, hand);

    if (equipped.isEmpty() && !stack.isEmpty()) {
      this.equipStack(slot, stack.copy());
      if (!world.isClient()) {
        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
      }
      stack.setCount(0);
      return ActionResult.success(world.isClient());
    }

    return super.interactAt(player, hitPos, hand);
  }
}
