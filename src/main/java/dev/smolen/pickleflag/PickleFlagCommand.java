/*
 * Pickle Pirate Flag Mod for Hytale
 * Commands for managing placed flags
 */
package dev.smolen.pickleflag;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Command collection for managing Pickle Pirate Flags.
 *
 * Commands:
 * - /pickleflag manage <id> - Opens rename UI for the specified flag
 * - /pickleflag list - Lists all discovered flags (future)
 */
public class PickleFlagCommand extends AbstractCommandCollection {

    public PickleFlagCommand() {
        super("pickleflag", "Commands to use and manage Pickle Pirate Flags");
        this.addSubCommand(new ManageCommand());
    }

    /**
     * Manage command - opens the rename UI for a flag.
     * Triggered from the map marker context menu.
     */
    private static class ManageCommand extends AbstractCommand {

        @Nonnull
        private final RequiredArg<String> idArg = this.withRequiredArg(
            "id",
            "The flag ID to manage",
            (ArgumentType) ArgTypes.STRING
        );

        public ManageCommand() {
            super("manage", "Open the flag management UI");
        }

        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            String id = this.idArg.get(context);

            // Find the flag
            FlagManager.FlagData flag = PickleFlagPlugin.get().getFlagManager().getFlag(id);
            if (flag == null) {
                context.sendMessage(Message.raw("No flag with id " + id + " found."));
                return CompletableFuture.completedFuture(null);
            }

            // Get the player who sent the command
            CommandSender sender = context.sender();
            if (sender instanceof Player player) {
                Ref ref = player.getReference();
                if (ref != null && ref.isValid()) {
                    Store store = ref.getStore();
                    World world = ((EntityStore) store.getExternalData()).getWorld();

                    return CompletableFuture.runAsync(() -> {
                        PlayerRef playerRefComponent = (PlayerRef) store.getComponent(
                            ref, PlayerRef.getComponentType());

                        if (playerRefComponent != null) {
                            // Open the rename UI
                            player.getPageManager().openCustomPage(
                                ref,
                                store,
                                (CustomUIPage) new FlagRenameUI(playerRefComponent, flag)
                            );
                        }
                    }, (Executor) world);
                }
            }

            return CompletableFuture.completedFuture(null);
        }
    }
}
