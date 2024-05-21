package com.camcamcamcamcam.renewable_sand;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

@Mod(RenewableSand.MODID)
public class RenewableSand
{
    public static final String MODID = "renewable_sand";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final BlockBehaviour.Properties FALLING_LAYER = BlockBehaviour.Properties.of().mapColor(MapColor.SAND).sound(SoundType.SAND).strength(0.0F);

    public static final DeferredBlock<Block> SAND_LAYER = BLOCKS.register("sand_layer",  () -> new FallingLayerBlock(Blocks.SAND, FALLING_LAYER));
    public static final DeferredBlock<Block> RED_SAND_LAYER = BLOCKS.register("red_sand_layer",  () -> new FallingLayerBlock(Blocks.RED_SAND, FALLING_LAYER));
    public static final DeferredBlock<Block> GRAVEL_LAYER = BLOCKS.register("gravel_layer",  () -> new FallingLayerBlock(Blocks.GRAVEL, FALLING_LAYER));
    public static final DeferredItem<BlockItem> SAND_LAYER_ITEM = ITEMS.registerSimpleBlockItem("sand_layer", SAND_LAYER);
    public static final DeferredItem<BlockItem> RED_SAND_LAYER_ITEM = ITEMS.registerSimpleBlockItem("red_sand_layer", RED_SAND_LAYER);
    public static final DeferredItem<BlockItem> GRAVEL_LAYER_ITEM = ITEMS.registerSimpleBlockItem("gravel_layer", GRAVEL_LAYER);



    public static final Map<Supplier<Block>, Supplier<Block>> SAND_FALLABLES = Util.make(Maps.newHashMap(), (fallables) -> {
        fallables.put(() -> Blocks.SANDSTONE, () -> SAND_LAYER.get());
        fallables.put(() -> Blocks.RED_SANDSTONE, () -> RED_SAND_LAYER.get());
    });

    public static final Map<Supplier<Block>, Supplier<Block>> GRAVEL_FALLABLES = Util.make(Maps.newHashMap(), (fallables) -> {
        fallables.put(() -> Blocks.COBBLESTONE, () -> GRAVEL_LAYER.get());
    });

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public RenewableSand(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(() -> {
            Blocks.BUBBLE_COLUMN.isRandomlyTicking = true;
        });
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(SAND_LAYER_ITEM);
            event.accept(RED_SAND_LAYER_ITEM);
            event.accept(GRAVEL_LAYER_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
