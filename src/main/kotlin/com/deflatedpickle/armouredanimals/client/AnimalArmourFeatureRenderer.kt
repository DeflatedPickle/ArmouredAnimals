/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SameParameterValue", "MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")

package com.deflatedpickle.armouredanimals.client

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.model.AnimalModel
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.DyeableArmorItem
import net.minecraft.util.Identifier

class AnimalArmourFeatureRenderer<T : LivingEntity, M : EntityModel<T>>(
    context: EntityRendererFactory.Context,
    entityRenderer: LivingEntityRenderer<T, M>,
    val model: M,
) : FeatureRenderer<T, M>(entityRenderer) {
    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        entity: T,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        renderArmor(
            matrices,
            vertexConsumers,
            entity,
            EquipmentSlot.HEAD,
            light,
            model,
        )
    }

    private fun renderArmor(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        entity: T,
        armorSlot: EquipmentSlot,
        light: Int,
        model: M
    ) {
        val itemStack = (entity as LivingEntity).getEquippedStack(armorSlot)
        if (itemStack.item !is ArmorItem) {
            return
        }
        val armorItem = itemStack.item as ArmorItem
        if (armorItem.slotType != armorSlot) {
            return
        }
        // (this.contextModel as BipedEntityModel<*>).setAttributes(model)
        val bl: Boolean = this.usesSecondLayer(armorSlot)
        val bl2 = itemStack.hasGlint()
        if (armorItem is DyeableArmorItem) {
            val i = armorItem.getColor(itemStack)
            val f = (i shr 16 and 0xFF).toFloat() / 255.0f
            val g = (i shr 8 and 0xFF).toFloat() / 255.0f
            val h = (i and 0xFF).toFloat() / 255.0f
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, null)
            this.renderArmorParts(
                matrices,
                vertexConsumers,
                light,
                armorItem,
                bl2,
                model,
                bl,
                1.0f,
                1.0f,
                1.0f,
                "overlay"
            )
        } else {
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0f, 1.0f, 1.0f, null)
        }
    }

    private fun renderArmorParts(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        item: ArmorItem,
        usesSecondLayer: Boolean,
        model: M,
        legs: Boolean,
        red: Float,
        green: Float,
        blue: Float,
        overlay: String?
    ) {
        val vertexConsumer = ItemRenderer.getArmorGlintConsumer(
            vertexConsumers,
            RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, legs, overlay)),
            false,
            usesSecondLayer
        )
        (model as AnimalModel<*>).render(
            matrices,
            vertexConsumer,
            light,
            OverlayTexture.DEFAULT_UV,
            red,
            green,
            blue,
            1.0f
        )
    }

    private fun usesSecondLayer(slot: EquipmentSlot): Boolean {
        return slot == EquipmentSlot.LEGS
    }

    private fun getArmorTexture(item: ArmorItem, legs: Boolean, overlay: String?): Identifier? {
        val string =
            "textures/models/armor/" + item.material.name + "_layer_" + (if (legs) 2 else 1) + (if (overlay == null) "" else "_$overlay") + ".png"
        return ArmorFeatureRenderer.ARMOR_TEXTURE_CACHE.computeIfAbsent(string) { id: String? -> Identifier(id) }
    }
}
