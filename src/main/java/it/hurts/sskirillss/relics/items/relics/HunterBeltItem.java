package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class HunterBeltItem extends Item implements ICurioItem, IHasTooltip {

    public HunterBeltItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.hunter_belt.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return RelicsConfig.HunterBelt.ADDITIONAL_LOOTING.get();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class HunterBeltEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingHurtEvent event) {
            if (event.getSource().getTrueSource() instanceof PlayerEntity
                    && event.getEntityLiving() instanceof AnimalEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HUNTER_BELT.get(), player).isPresent()) {
                    event.setAmount(event.getAmount() * RelicsConfig.HunterBelt.PLAYER_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
            if (event.getSource().getTrueSource() instanceof TameableEntity) {
                TameableEntity pet = (TameableEntity) event.getSource().getTrueSource();
                if (pet.getOwner() != null && pet.getOwner() instanceof PlayerEntity
                        && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HUNTER_BELT.get(), pet.getOwner()).isPresent()) {
                    event.setAmount(event.getAmount() * RelicsConfig.HunterBelt.PET_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
        }
    }
}