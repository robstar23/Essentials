package org.mcess.essentials.commands;

import static org.mcess.essentials.I18n.tl;
import org.mcess.essentials.Trade;
import org.mcess.essentials.User;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandworld extends EssentialsCommand
{
	public Commandworld()
	{
		super("world");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		World world;

		if (args.length < 1)
		{
			World nether = null;

			final List<World> worlds = server.getWorlds();

			for (World world2 : worlds)
			{
				if (world2.getEnvironment() == World.Environment.NETHER)
				{
					nether = world2;
					break;
				}
			}
			if (nether == null)
			{
				return;
			}
			world = user.getWorld() == nether ? worlds.get(0) : nether;
		}
		else
		{
			world = ess.getWorld(getFinalArg(args, 0));
			if (world == null)
			{
				user.sendMessage(tl("invalidWorld"));
				user.sendMessage(tl("possibleWorlds", server.getWorlds().size() - 1));
				user.sendMessage(tl("typeWorldName"));
				throw new NoChargeException();
			}
		}

		if (ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + world.getName()))
		{
			throw new Exception(tl("noPerm", "essentials.worlds." + world.getName()));
		}

		double factor;
		if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL)
		{
			factor = 8.0;
		}
		else if (user.getWorld().getEnvironment() == World.Environment.NORMAL && world.getEnvironment() == World.Environment.NETHER)
		{
			factor = 1.0 / 8.0;
		}
		else
		{
			factor = 1.0;
		}

		final Location loc = user.getLocation();
		final Location target = new Location(world, loc.getBlockX() * factor + .5, loc.getBlockY(), loc.getBlockZ() * factor + .5);

		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.getTeleport().teleport(target, charge, TeleportCause.COMMAND);
		throw new NoChargeException();
	}
}