/*
 * Mohist - MohistMC
 * Copyright (C) 2018-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.mohistmc.commands;

import com.mohistmc.api.ItemAPI;
import com.mohistmc.api.PlayerAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ItemsCommand extends Command {

    private final List<String> params = Arrays.asList("info", "name", "save");

    public ItemsCommand(String name) {
        super(name);
        this.description = "Mohist related commands";
        this.usageMessage = "/items [info|name|save]";
        this.setPermission("mohist.command.items");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        if (args.length == 1 && (sender.isOp() || testPermission(sender))) {
            for (String param : params) {
                if (param.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(param);
                }
            }
        }

        return list;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You have nothing on main hand.");
            return false;
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "info" -> {
                ItemsCommand.info(player);
            }
            case "name" -> {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /items name <string>");
                    return false;
                }
                ItemAPI.name(player.getInventory().getItemInMainHand(), args[1]);
                sender.sendMessage(ChatColor.GREEN + "Item name set complete.");
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }
        }

        return false;
    }

    public static void info(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        // item name and i18n name
        player.sendMessage(ChatColor.GRAY + "Name - " + ChatColor.GREEN + itemStack.getType());
        player.sendMessage(ChatColor.GRAY + "Name(Translate) - " + ChatColor.GREEN + nmsItem.getDisplayName());
        player.sendMessage(ChatColor.GRAY + "ForgeItem - " + itemStack.getType().isForgeBlock);
        player.sendMessage(ChatColor.GRAY + "ForgeBlock - " + itemStack.getType().isForgeItem);
        player.sendMessage(ChatColor.GRAY + "NBT(CraftBukkit) - " + ItemAPI.getNBTAsString(itemStack));
        player.sendMessage(ChatColor.GRAY + "NBT(Vanilla) - " + ItemAPI.getNbtAsString(PlayerAPI.getNMSPlayer(player).getMainHandItem().getTag())); // Use vanilla method
        player.sendMessage(ChatColor.GRAY + "NBT(Forge) - " + ItemAPI.getNbtAsString(nmsItem.getForgeCaps()));
    }
}