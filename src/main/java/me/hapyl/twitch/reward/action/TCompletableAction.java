package me.hapyl.twitch.reward.action;

import com.google.common.collect.Maps;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.Reward;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.PlayerHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public abstract class TCompletableAction extends TAction {

    public final Map<Player, Reward> awaitingCompletion;

    public TCompletableAction(String name) {
        super(name);
        this.awaitingCompletion = Maps.newHashMap();
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        return false;
    }

    @Override
    public void onSuccess(@NonNull Player player, @NonNull TwitchUser user, @NonNull Reward reward) {
        super.onSuccess(player, user, reward);

        awaitingCompletion.put(player, reward);
    }

    public final void completeAction(@NonNull Player player) {
        final Reward reward = awaitingCompletion.remove(player);

        if (reward == null) {
            return;
        }

        boolean anyDroppedOnGround = false;

        for (ItemStack item : reward.itemRewards()) {
            final boolean method = addItemOrDrop(player, item);

            if (!method) {
                anyDroppedOnGround = true;
            }
        }

        player.sendMessage(formatItemDrops(reward.itemRewards(), anyDroppedOnGround));
    }

    private Component formatItemDrops(List<ItemStack> itemList, boolean anyDroppedOnGround) {
        Component component = Component.text(Message.PREFIX).append(Component.text("Награда: ", NamedTextColor.GRAY));

        for (int i = 0; i < itemList.size(); i++) {
            if (i != 0) {
                component = component.append(Component.text(", ", NamedTextColor.GRAY));
            }

            final ItemStack item = itemList.get(i);
            component = component
                    .append(Component.text(item.getAmount(), NamedTextColor.DARK_GREEN))
                    .append(Component.text("x", NamedTextColor.GRAY))
                    .append(Component.translatable(item.translationKey(), NamedTextColor.GREEN));
        }

        if (anyDroppedOnGround) {
            component = component.append(Component.text(" (Некоторые выпали на землю!)", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        }

        return component;
    }

    private boolean addItemOrDrop(Player player, ItemStack item) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() != -1) {
            inventory.addItem(item);
            return true;
        }
        else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            return false;
        }
    }

}
