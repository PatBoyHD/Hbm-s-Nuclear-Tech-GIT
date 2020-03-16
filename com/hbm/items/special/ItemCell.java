package com.hbm.items.special;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.forgefluid.HbmFluidHandlerCell;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.forgefluid.SpecialContainerFillLists.EnumCanister;
import com.hbm.forgefluid.SpecialContainerFillLists.EnumCell;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCell extends ItemRadioactive {

	public ItemCell(String s) {
		super(s);
		this.setMaxDamage(1000);
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if(entityItem.onGround){
			if(hasFluid(entityItem.getItem(), ModForgeFluids.aschrab)){
				if (!entityItem.world.isRemote) {
					entityItem.setDead();
					entityItem.world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 100.0f, entityItem.world.rand.nextFloat() * 0.1F + 0.9F);
					EntityNukeExplosionMK3 entity = new EntityNukeExplosionMK3(entityItem.world);
					entity.posX = entityItem.posX;
					entity.posY = entityItem.posY;
					entity.posZ = entityItem.posZ;
					entity.destructionRange = (int) (MainRegistry.aSchrabRadius*(FluidUtil.getFluidContained(entityItem.getItem()).amount/1000.0F));
					
					entity.speed = 25;
					entity.coefficient = 1.0F;
					entity.waste = false;

					entityItem.world.spawnEntity(entity);
		    		
		    		EntityCloudFleija cloud = new EntityCloudFleija(entityItem.world, (int) (MainRegistry.aSchrabRadius*(FluidUtil.getFluidContained(entityItem.getItem()).amount/1000.0F)));
		    		cloud.posX = entityItem.posX;
		    		cloud.posY = entityItem.posY;
		    		cloud.posZ = entityItem.posZ;
		    		entityItem.world.spawnEntity(cloud);
				}
				return true;
			}
			if(hasFluid(entityItem.getItem(), ModForgeFluids.amat)){
				if (!entityItem.world.isRemote) {
					entityItem.setDead();
					entityItem.world.createExplosion(entityItem, entityItem.posX, entityItem.posY, entityItem.posZ, 10.0F*(FluidUtil.getFluidContained(entityItem.getItem()).amount/1000.0F), true);
				}
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	//TODO balefire bomb
		/*	if(world.getBlockState(pos).getBlock() instanceof BlockCrashedBomb)
		{
			Random rand = new Random();
			int i = rand.nextInt(100);
			if(i == 0)
			{
	            if (!world.isRemote)
	            {
	            	((BlockCrashedBomb)world.getBlockState(pos)).getBlock().explode(world, pos);
	            }
			} else if(i < 90)
			{
	            //if (!world.isRemote)
	            {
	            	player.inventory.consumeInventoryItem(ModItems.cell_empty);

	            	if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.cell_antimatter)))
	            	{
	            		player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.cell_antimatter, 1, 0), false);
	            	}
	            }
			} else {
	            //if (!world.isRemote)
	            {
	            	player.inventory.consumeInventoryItem(ModItems.cell_empty);

	            	if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.cell_anti_schrabidium)))
	            	{
	            		player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.cell_anti_schrabidium, 1, 0), false);
	            	}
	            }
			}
			return true;
		}
		return false;*/
		return EnumActionResult.PASS;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		FluidStack f = FluidUtil.getFluidContained(stack);
		if(f == null){
			return I18n.format("item.cell_empty.name");
		} else {
			return I18n.format(EnumCell.getEnumFromFluid(f.getFluid()).getTranslateKey());
		}
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH){
			for(Fluid f : EnumCell.getFluids()){
				ItemStack stack = new ItemStack(this, 1, 0);
				stack.setTagCompound(new NBTTagCompound());
				if(f != null)
					stack.getTagCompound().setTag(HbmFluidHandlerCell.FLUID_NBT_KEY, new FluidStack(f, 1000).writeToNBT(new NBTTagCompound()));
				items.add(stack);
			}
		}
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new HbmFluidHandlerCell(stack, 1000);
	}
	
	public static boolean isFullCell(ItemStack stack, Fluid fluid){
		if(stack != null){
			if(stack.getItem() instanceof ItemCell && FluidUtil.getFluidContained(stack) != null && FluidUtil.getFluidContained(stack).getFluid() == fluid && FluidUtil.getFluidContained(stack).amount == 1000)
				return true;
		}
		return false;
	}
	
	public static boolean isEmptyCell(ItemStack stack){
		if(stack != null){
			if(stack.getItem() == ModItems.cell && (FluidUtil.getFluidContained(stack) == null || FluidUtil.getFluidContained(stack).amount < 1)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasFluid(ItemStack stack, Fluid f){
		if(stack != null){
			if(stack.getItem() == ModItems.cell && FluidUtil.getFluidContained(stack) != null && FluidUtil.getFluidContained(stack).getFluid() == f)
				return true;
		}
		return false;
	}
	
	public static ItemStack getFullCell(Fluid fluid){
		if(EnumCell.contains(fluid)){
			ItemStack stack = new ItemStack(ModItems.cell, 1, 0);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag(HbmFluidHandlerCell.FLUID_NBT_KEY, new FluidStack(fluid, 1000).writeToNBT(new NBTTagCompound()));
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	
	
	public static class CellRecipe implements IRecipe {

		@Override
		public IRecipe setRegistryName(ResourceLocation name) {
			return null;
		}

		@Override
		public ResourceLocation getRegistryName() {
			return null;
		}

		@Override
		public Class<IRecipe> getRegistryType() {
			return null;
		}

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			return false;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			return null;
		}

		@Override
		public boolean canFit(int width, int height) {
			return false;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return null;
		}
		
	}
	
}
