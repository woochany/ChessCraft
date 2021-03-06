package me.desht.chesscraft.commands;

import java.util.ArrayList;
import java.util.List;

import me.desht.chesscraft.ChessCraft;
import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.ai.AIFactory;
import me.desht.chesscraft.chess.ai.AIFactory.AIDefinition;
import me.desht.dhutils.MessagePager;
import me.desht.dhutils.commands.AbstractCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ListAICommand extends AbstractCommand {

	public ListAICommand() {
		super("chess l a", 0, 1);
		setPermissionNode("chesscraft.commands.list.ai");
		setUsage("/chess list ai");
	}

	@Override
	public boolean execute(Plugin plugin, CommandSender sender, String[] args) {

		MessagePager pager = MessagePager.getPager(sender).clear();

		if (args.length == 0) {
			List<AIDefinition> aiDefs = AIFactory.instance.listAIDefinitions(true);
			List<String> lines = new ArrayList<String>(aiDefs.size());
			for (AIDefinition aiDef : aiDefs) {
				if (!aiDef.isEnabled())
					continue;
				StringBuilder sb = new StringBuilder(Messages.getString("ChessCommandExecutor.AIList",
				                                                        aiDef.getName(), aiDef.getImplClassName(),
				                                                        aiDef.getComment()));
				if (ChessCraft.economy != null) {
					sb.append(", " + Messages.getString("ChessCommandExecutor.AIpayout", (int) (aiDef.getPayoutMultiplier() * 100))); //$NON-NLS-1$
				}

				lines.add(MessagePager.BULLET +  sb.toString());
			}
			pager.add(lines);
		} else {
			AIDefinition aiDef = AIFactory.instance.getAIDefinition(args[0], true);
			pager.add(aiDef.getDetails());
		}

		pager.showPage();
		return true;
	}

}
