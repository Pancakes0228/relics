package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.entities.StellarCatalystProjectileEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
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

public class StellarCatalystItem extends Item implements ICurioItem, IHasTooltip {
    public StellarCatalystItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.stellar_catalyst.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MoonlightWellServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.STELLAR_CATALYST.get(), (LivingEntity) event.getSource().getTrueSource()).isPresent()) {
                    LivingEntity target = event.getEntityLiving();
                    if (event.getAmount() > RelicsConfig.StellarCatalyst.MIN_DAMAGE_AMOUNT.get()
                            && (target.getEntityWorld().isNightTime() || target.getEntityWorld().getDimensionKey() == World.THE_END)
                            && target.getEntityWorld().canSeeSky(target.getPosition())
                            && random.nextFloat() <= RelicsConfig.StellarCatalyst.FALLING_STAR_SUMMON_CHANCE.get()) {
                        StellarCatalystProjectileEntity projectile = new StellarCatalystProjectileEntity((LivingEntity) event.getSource().getTrueSource(),
                                event.getEntityLiving(), event.getAmount() * RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_MULTIPLIER.get().floatValue());
                        projectile.setPosition(target.getPosX(), Math.min(target.getEntityWorld().getHeight(),
                                target.getPosY() + target.getEntityWorld().getRandom().nextInt(10) + 20), target.getPosZ());
                        projectile.getEntityWorld().addEntity(projectile);
                    }
                }
            }
        }
    }
}