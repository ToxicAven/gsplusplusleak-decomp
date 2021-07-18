// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.manager.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiChat;
import com.gamesense.client.command.CommandManager;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.RenderUtil;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.VersionChecker;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import java.util.Iterator;
import java.util.function.Predicate;
import com.gamesense.api.event.events.BlockChangeEvent;
import net.minecraft.network.play.server.SPacketBlockChange;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.client.GameSense;
import com.gamesense.api.util.player.NameUtil;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.client.manager.Manager;

public enum ClientEventManager implements Manager
{
    INSTANCE;
    
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    private ClientEventManager() {
        SPacketPlayerListItem packet;
        final Iterator<SPacketPlayerListItem.AddPlayerData> iterator;
        SPacketPlayerListItem.AddPlayerData playerData;
        String name;
        final Iterator<SPacketPlayerListItem.AddPlayerData> iterator2;
        SPacketPlayerListItem.AddPlayerData playerData2;
        String name2;
        SPacketBlockChange packet2;
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                packet = (SPacketPlayerListItem)event.getPacket();
                if (packet.func_179768_b() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    packet.func_179767_a().iterator();
                    while (iterator.hasNext()) {
                        playerData = iterator.next();
                        if (playerData.func_179962_a().getId() != this.getMinecraft().field_71449_j.func_148256_e().getId()) {
                            new Thread(() -> {
                                name = NameUtil.resolveName(playerData.func_179962_a().getId().toString());
                                if (name != null && this.getPlayer() != null && this.getPlayer().field_70173_aa >= 1000) {
                                    GameSense.EVENT_BUS.post(new PlayerJoinEvent(name));
                                }
                                return;
                            }).start();
                        }
                    }
                }
                if (packet.func_179768_b() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    packet.func_179767_a().iterator();
                    while (iterator2.hasNext()) {
                        playerData2 = iterator2.next();
                        if (playerData2.func_179962_a().getId() != this.getMinecraft().field_71449_j.func_148256_e().getId()) {
                            new Thread(() -> {
                                name2 = NameUtil.resolveName(playerData2.func_179962_a().getId().toString());
                                if (name2 != null && this.getPlayer() != null && this.getPlayer().field_70173_aa >= 1000) {
                                    GameSense.EVENT_BUS.post(new PlayerLeaveEvent(name2));
                                }
                            }).start();
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof SPacketBlockChange) {
                packet2 = (SPacketBlockChange)event.getPacket();
                GameSense.EVENT_BUS.post(new BlockChangeEvent(packet2.func_179827_b(), packet2.func_180728_a().func_177230_c()));
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }
    
    @SubscribeEvent
    public void onRenderScreen(final RenderGameOverlayEvent.Text event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onRenderBlockOverlay(final RenderBlockOverlayEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(final LivingEntityUseItemEvent.Finish event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onInputUpdate(final InputUpdateEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onPlayerPush(final PlayerSPPushOutOfBlocksEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFogDensity(final EntityViewRenderEvent.FogDensity event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onFov(final EntityViewRenderEvent.FOVModifier event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent
    public void onEntityJoin(final EntityJoinWorldEvent event) {
        if (event.getEntity() != null && event.getEntity().equals((Object)this.getPlayer()) && !VersionChecker.joinMessage.equalsIgnoreCase("None")) {
            MessageBus.sendClientPrefixMessage(VersionChecker.joinMessage);
        }
    }
    
    @SubscribeEvent
    public void onUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (this.getMinecraft().field_71439_g == null || this.getMinecraft().field_71441_e == null) {
            return;
        }
        if (event.getEntity().func_130014_f_().field_72995_K && event.getEntityLiving() == this.getPlayer()) {
            for (final Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) {
                    continue;
                }
                module.onUpdate();
            }
            GameSense.EVENT_BUS.post(event);
        }
    }
    
    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (this.getMinecraft().field_71439_g == null || this.getMinecraft().field_71441_e == null) {
            return;
        }
        this.getProfiler().func_76320_a("gamesense");
        this.getProfiler().func_76320_a("setup");
        RenderUtil.prepare();
        final RenderEvent event2 = new RenderEvent(event.getPartialTicks());
        this.getProfiler().func_76319_b();
        for (final Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            this.getProfiler().func_76320_a(module.getName());
            module.onWorldRender(event2);
            this.getProfiler().func_76319_b();
        }
        this.getProfiler().func_76320_a("release");
        RenderUtil.release();
        this.getProfiler().func_76319_b();
        this.getProfiler().func_76319_b();
    }
    
    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        if (this.getMinecraft().field_71439_g == null || this.getMinecraft().field_71441_e == null) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            for (final Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) {
                    continue;
                }
                module.onRender();
            }
            GameSense.INSTANCE.gameSenseGUI.render();
        }
        GameSense.EVENT_BUS.post(event);
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState() || Keyboard.getEventKey() == 0) {
            return;
        }
        final EntityPlayerSP player = this.getPlayer();
        if (player != null && !player.func_70093_af()) {
            final String prefix = CommandManager.getCommandPrefix();
            final char typedChar = Keyboard.getEventCharacter();
            if (prefix.length() == 1 && prefix.charAt(0) == typedChar) {
                this.getMinecraft().func_147108_a((GuiScreen)new GuiChat(prefix));
            }
        }
        final int key = Keyboard.getEventKey();
        if (key != 0) {
            for (final Module module : ModuleManager.getModules()) {
                if (module.getBind() != key) {
                    continue;
                }
                module.toggle();
            }
        }
        GameSense.INSTANCE.gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
    }
    
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            GameSense.EVENT_BUS.post(event);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(final ClientChatEvent event) {
        if (event.getMessage().startsWith(CommandManager.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                this.getMinecraft().field_71456_v.func_146158_b().func_146239_a(event.getMessage());
                CommandManager.callCommand(event.getMessage().substring(1));
            }
            catch (Exception e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + e.getMessage(), true);
            }
        }
    }
}
