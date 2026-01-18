/*
 * Pickle Pirate Flag Mod for Hytale
 * UI for renaming placed flags
 */
package pickleflag;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;

/**
 * Interactive UI page for renaming a Pickle Pirate Flag.
 *
 * Opens a text input dialog allowing the player to change the flag's
 * display name, which appears on the map marker.
 */
public class FlagRenameUI extends InteractiveCustomUIPage<FlagRenameUI.RenameData> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // The flag being renamed
    private final FlagManager.FlagData flagData;

    /**
     * Create a new rename UI for the given flag.
     *
     * @param playerRef Reference to the player opening the UI
     * @param flagData  The flag to rename
     */
    public FlagRenameUI(@Nonnull PlayerRef playerRef, FlagManager.FlagData flagData) {
        super(playerRef, CustomPageLifetime.CanDismiss, RenameData.CODEC);
        this.flagData = flagData;
    }

    /**
     * Build the UI layout and event bindings.
     */
    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder uiCommandBuilder,
                      @Nonnull UIEventBuilder uiEventBuilder,
                      @Nonnull Store<EntityStore> store) {

        // Load the UI template
        uiCommandBuilder.append("Pages/FlagRename.ui");

        // Set the current flag name in the text field
        uiCommandBuilder.set("#FlagName.Value", this.flagData.name());

        // Bind Confirm button click - reads the text field value when clicked
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SetButton",
            EventData.of("@ConfirmName", "#FlagName.Value"),
            false
        );

        // Cancel button and Back button will trigger onDismiss - no binding needed
    }

    /**
     * Handle data events from the UI.
     */
    @Override
    public void handleDataEvent(Ref<EntityStore> ref,
                                Store<EntityStore> store,
                                RenameData data) {
        super.handleDataEvent(ref, store, data);

        // Confirm button was clicked - save the name and close
        if (data.confirmName != null) {
            String newName = data.confirmName.trim();

            if (!newName.isBlank()) {
                // Limit name length
                if (newName.length() > 32) {
                    newName = newName.substring(0, 32);
                }

                LOGGER.atInfo().log("Confirming rename of flag " + this.flagData.id() +
                    " from '" + this.flagData.name() + "' to '" + newName + "'");

                // Update the flag name in the manager
                PickleFlagPlugin.get().getFlagManager().renameFlag(this.flagData.id(), newName);
            }

            // Close the UI
            this.close();
        }
    }

    /**
     * Data class for UI event data (Confirm button sends the text field value).
     */
    public static class RenameData {

        public static final BuilderCodec<RenameData> CODEC = BuilderCodec
            .builder(RenameData.class, RenameData::new)
            .addField(
                new KeyedCodec<>("@ConfirmName", Codec.STRING, false),  // false = optional
                (data, name) -> data.confirmName = name,
                data -> data.confirmName
            )
            .build();

        private String confirmName;
    }
}
