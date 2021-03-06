package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SpaceDissectorItem extends Item implements IHasTooltip {
    public static final String TAG_IS_THROWN = "thrown";
    public static final String TAG_UUID = "uuid";
    public static final String TAG_UPDATE_TIME = "time";

    public SpaceDissectorItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (handIn == Hand.MAIN_HAND && !playerIn.getCooldownTracker().hasCooldown(stack.getItem())) {
            if (!NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
                SpaceDissectorEntity entity = new SpaceDissectorEntity(worldIn, playerIn);
                entity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 1.0F,
                        RelicsConfig.SpaceDissector.MOVEMENT_SPEED.get().floatValue(), 0.0F);
                NBTUtils.setBoolean(stack, TAG_IS_THROWN, true);
                NBTUtils.setString(stack, TAG_UUID, entity.getUniqueID().toString());
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                entity.stack = playerIn.getHeldItemMainhand();
                entity.setOwner(playerIn);
                worldIn.addEntity(entity);
            } else {
                if (!NBTUtils.getString(stack, TAG_UUID, "").equals("")) {
                    UUID uuid = UUID.fromString(NBTUtils.getString(stack, TAG_UUID, ""));
                    if (worldIn instanceof ServerWorld) {
                        if (((ServerWorld) worldIn).getEntityByUuid(uuid) != null) {
                            if (((ServerWorld) worldIn).getEntityByUuid(uuid) instanceof SpaceDissectorEntity) {
                                SpaceDissectorEntity dissector = (SpaceDissectorEntity) ((ServerWorld) worldIn).getEntityByUuid(uuid);
                                if (!playerIn.isSneaking()) {
                                    if (!dissector.getDataManager().get(SpaceDissectorEntity.IS_RETURNING)) {
                                        dissector.getDataManager().set(SpaceDissectorEntity.IS_RETURNING, true);
                                    }
                                } else {
                                    if (playerIn.getPositionVec().distanceTo(dissector.getPositionVec()) > RelicsConfig.SpaceDissector.DISTANCE_FOR_TELEPORT.get()) {
                                        playerIn.setPositionAndUpdate(dissector.getPosX(), dissector.getPosY(), dissector.getPosZ());
                                        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                                                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                        dissector.remove();
                                        playerIn.getCooldownTracker().setCooldown(stack.getItem(), RelicsConfig.SpaceDissector.COOLDOWN_AFTER_TELEPORT.get() * 20);
                                        NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                                    }
                                }
                            }
                        } else {
                            playerIn.getCooldownTracker().setCooldown(stack.getItem(), RelicsConfig.SpaceDissector.COOLDOWN_AFTER_TELEPORT.get() * 20);
                            NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                        }
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.ticksExisted % 20 == 0 && NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) + 1);
            if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > RelicsConfig.SpaceDissector.MAX_THROWN_TIME.get()) {
                NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_2"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_3"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}