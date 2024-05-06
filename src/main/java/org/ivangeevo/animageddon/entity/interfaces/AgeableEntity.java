package org.ivangeevo.animageddon.entity.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public interface AgeableEntity {

    AgeableEntity createChild(AgeableEntity var1);

    boolean interact(PlayerEntity par1EntityPlayer);

    boolean entityAgeableInteract(PlayerEntity player);

    void entityInit();

    int getGrowingAge();

    void setGrowingAge(int par1);

    void writeEntityToNBT(NbtCompound nbt);

    void readEntityFromNBT(NbtCompound nbt);

    void onLivingUpdate();

    boolean isChild();

    void setSize(float fWidth, float fHeight);

    void adjustSizeForAge(boolean bIsChild);

    boolean canChildGrow();

    boolean canLoveJuiceRegenerate();

    int getTicksForChildToGrow();

    World getWorld();

    double getPosX();

    double getPosY();

    double getPosZ();

    Box getBoundingBox();

    void setLocationAndAngles(double x, double y, double z, float yaw, float pitch);

    void spawnEntityInWorld(AgeableEntity entity);

    boolean hasDisplayName();


    void func_94058_c(String displayName);

    void setItemStack(ItemStack stack);

    void decrementStackSize();

    int getItemDamage();

    void setTicksForChildToGrow(int ticks);

    boolean isRemote();

    boolean getBooleanFlag();

    void setBooleanFlag(boolean flag);
}
