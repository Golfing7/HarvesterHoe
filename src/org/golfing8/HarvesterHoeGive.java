package org.golfing8;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;

import net.ess3.api.NoLoanPermittedException;
import net.md_5.bungee.api.ChatColor;

public class HarvesterHoeGive implements CommandExecutor,Listener{

	private Main plugin = Main.getPlugin(Main.class);
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("harvesterhoe")) {
			if(sender.hasPermission("harvesterhoe.admin")) {
				int length = args.length;
				if(length == 0) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8----- &aHarvesterHoes: By &eGolfing8 &8-----"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/harvesterhoe give &e(player) (amount) &b- Gives a player a harvester hoe!"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/harvesterhoe reload &b- Reloads the config."));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If you find any bugs, please messaage me on discord! &5&lGolfing8#0528"));
					return true;
				}
				if(args[0].equalsIgnoreCase("reload")) {
					plugin.reloadConfig();
					plugin.saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Successfully reloaded HarvesterHoes v1.0!");
					return true;
				}
				if(args[0].equalsIgnoreCase("give")) {
					if(length >= 4) {
						sender.sendMessage(ChatColor.RED + "Improper usage! /harvesterhoe give (Player) (Amount)");
						return true;
					}
					for(Player player : Bukkit.getOnlinePlayers()) {
						int amount = Integer.valueOf(args[2]);
						if(!(amount <= 0 || amount >= 65)) {
							ItemStack stack = new ItemStack(Material.GOLD_HOE);
							ItemMeta im = stack.getItemMeta();
							im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Harvester Hoe " + ChatColor.GRAY + "(" + ChatColor.GREEN + "Harvest Mode" + ChatColor.GRAY + ")");
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(ChatColor.translateAlternateColorCodes('&', "&9- &eShift Left-Click"));
							lore.add(ChatColor.WHITE + "To toggle modes!");
							lore.add("");
							lore.add(ChatColor.BLUE + "-" + ChatColor.YELLOW + " Shift Right-Click");
							lore.add(ChatColor.WHITE + "To sell all items!");
							im.spigot().setUnbreakable(true);
							im.setLore(lore);
							stack.setItemMeta(im);
							int z = 0;
							while(z < amount) {
								if(player.getInventory().firstEmpty() == -1) {
									player.getLocation().getWorld().dropItemNaturally(player.getLocation(), stack);
								}else {
									player.getInventory().addItem(stack);
								}
							}
							sender.sendMessage(ChatColor.GREEN + "Successfully given " + ChatColor.YELLOW + amount + ChatColor.GREEN + " HarvesterHoes to " + ChatColor.YELLOW + player.getName());
							player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.YELLOW + amount + ChatColor.GREEN + " HarvesterHoe(s)!");
							return true;
						}else {
							sender.sendMessage(ChatColor.RED + "Please enter a valid amount! 1 > 64!");
							return true;
						}
					
					}
				}
			}else {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
		}
		return true;
	}
	FileConfiguration config = plugin.getConfig();
	@EventHandler
	public void onHarvesterHoe(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(p.getItemInHand().getType() == null)return;
		if(p.getItemInHand().getType().equals(Material.GOLD_HOE)) {
			if(!p.hasPermission("harvesterhoe.use")) {
				p.sendMessage(ChatColor.RED + "You don't have permissions to use HarvesterHoes!");
				return;
			}
			if(p.getItemInHand().getItemMeta()== null)return;
			if(p.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&e&lHarvester Hoe"))) {
				if(p.getItemInHand().getItemMeta().getLore() == null)return;
				if(p.getItemInHand().getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', "&9- &eShift Left-Click"))) {
					Location l = b.getLocation();
					Location l2 = b.getLocation();
					FPlayer fp = FPlayers.getInstance().getByPlayer(p);
					if(FactionsBlockListener.playerCanBuildDestroyBlock(p, b.getLocation(), "break", true)) {
						if(l.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
							e.setCancelled(true);
							int aw = 0;
							int y = 0;
							l2.setY(l2.getY() - 1);
							if(l2.getBlock().getType().equals(Material.DIRT) || l2.getBlock().getType().equals(Material.SAND)) {
								e.setCancelled(true);
								return;
							}
							double price = config.getDouble("harvesterhoe.price.sugarcane");
							Location save = b.getLocation();
							int hg = 0;
							while(aw < 256) {
								l.setY(l.getY() + 1);
								if(!l.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
									break;
									
								}
								aw++;
							}
							while(hg <= 256) {
								l.setY(l.getY() - 1);
								l.getBlock().setType(Material.AIR);
								if(l.getY() == save.getY()) {
									break;
								}
								y++;
							}
							int tott =  y + 1;
							if(p.getItemInHand().getItemMeta().hasEnchants()) {
									try {
											Economy.add(p.getName(), config.getDouble("harvesterhoe.price.sugarcane") * tott);
										
									} catch (com.earth2me.essentials.api.NoLoanPermittedException
											| UserDoesNotExistException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}		
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a+(&l" + price  * tott + "&a)"));
							}else {
								p.getInventory().addItem(new ItemStack(Material.SUGAR_CANE, y + 1));	
							}
							
						}
					}else {
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + "You can only use this where you can build!");
					}
						
					
				}
			}
		}
	}
	@EventHandler
	public void onHarvestToggle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(p.getItemInHand().getType() == null)return;
		if(p.getItemInHand().getItemMeta()== null)return;
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(p.isSneaking()){
				if(p.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&e&lHarvester Hoe"))) {
					if(p.getItemInHand().getItemMeta().getLore() == null)return;
					if(p.getItemInHand().getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', "&9- &eShift Left-Click"))) {
						if(e.getAction() == Action.RIGHT_CLICK_AIR) {
							int a = 0;
							double total = 0;
							while(a <= 35) {
								if(p.getInventory().getItem(a) != null) {
									if(p.getInventory().getItem(a).getType().equals(Material.SUGAR_CANE)) {
										int g = p.getInventory().getItem(a).getAmount();
										p.getInventory().setItem(a, new ItemStack(Material.AIR));
										
										total += g * config.getDouble("harvesterhoe.price.sugarcane");
									}
									
									
									
								}
								if(a == 35) {
									if(total == 0) {
										p.sendMessage(ChatColor.RED + "Nothing to sell!");
										break;
									}
									try {
										Economy.add(p.getName(), total);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a+(&l" + total + "&a)"));
									} catch (NoLoanPermittedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (UserDoesNotExistException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									a++;
								}
								
								
								a++;
								
							}
							return;
						}
						if(!p.getItemInHand().getItemMeta().hasEnchants()) {
							ItemStack newstack = p.getItemInHand();
							ItemMeta im = newstack.getItemMeta();
							im.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
							im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
							im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Harvester Hoe " + ChatColor.GRAY + "(" + ChatColor.GREEN + "Sell Mode" + ChatColor.GRAY + ")");
							newstack.setItemMeta(im);
							p.setItemInHand(newstack);
						}else {
							ItemStack newstack = p.getItemInHand();
							ItemMeta im = newstack.getItemMeta();
							im.removeEnchant(Enchantment.ARROW_DAMAGE);
							im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Harvester Hoe " + ChatColor.GRAY + "(" + ChatColor.GREEN + "Harvest Mode" + ChatColor.GRAY + ")");
							newstack.setItemMeta(im);
							p.setItemInHand(newstack);
						}
						
						
						
					}
				}
			}
		}
		
		
			
	}

}
