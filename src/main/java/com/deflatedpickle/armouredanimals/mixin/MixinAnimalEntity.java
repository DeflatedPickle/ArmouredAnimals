/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.armouredanimals.mixin;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(AnimalEntity.class)
public abstract class MixinAnimalEntity extends MobEntity {
  protected MixinAnimalEntity(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
    var stack = player.getStackInHand(hand);
    var equipped = getEquippedStack(EquipmentSlot.HEAD);

    if (stack.getItem() == equipped.getItem()) return super.interactAt(player, hitPos, hand);

    if (equipped.isEmpty() && !stack.isEmpty()) {
      this.equipStack(EquipmentSlot.HEAD, stack.copy());
      if (!world.isClient()) {
        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
      }
      stack.setCount(0);
      return ActionResult.success(world.isClient());
    } else if (player.isSneaking()) {
      // this.dropStack(equipped);
      player.giveItemStack(equipped);
      this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);

      return ActionResult.success(world.isClient());
    }

    return super.interactAt(player, hitPos, hand);
  }

  @Override
  protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
      this.dropStack(this.getEquippedStack(equipmentSlot));
      this.equipStack(equipmentSlot, ItemStack.EMPTY);
    }
  }

  @Override
  protected void damageArmor(DamageSource source, float amount) {
    if (amount <= 0.0f) {
      return;
    }
    if ((amount /= 4.0f) < 1.0f) {
      amount = 1.0f;
    }

    ItemStack itemStack =
        ((List<ItemStack>) getArmorItems()).get(EquipmentSlot.HEAD.getEntitySlotId());
    if (source.isFire() && itemStack.getItem().isFireproof()
        || !(itemStack.getItem() instanceof ArmorItem)) return;
    itemStack.damage(
        (int) amount, this, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.HEAD));
  }
}
