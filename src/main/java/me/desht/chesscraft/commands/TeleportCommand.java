package me.desht.chesscraft.commands;

import me.desht.chesscraft.ChessCraft;
import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.BoardViewManager;
import me.desht.chesscraft.chess.ChessGame;
import me.desht.chesscraft.chess.ChessGameManager;
import me.desht.chesscraft.exceptions.ChessException;
import me.desht.dhutils.PermissionUtils;
import me.desht.dhutils.commands.AbstractCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class TeleportCommand extends AbstractCommand {

	public TeleportCommand() {
		super("chess tp", 0, 2);
		setPermissionNode("chesscraft.commands.teleport");
		setUsage(new String[] {
				"/chess tp [<game-name>]",
				"/chess tp -b <board-name>"
		});
		setOptions("b");
	}

	@Override
	public boolean execute(Plugin plugin, CommandSender sender, String[] args) throws ChessException {
		notFromConsole(sender);
		
		if (!ChessCraft.getInstance().getConfig().getBoolean("teleporting")) {
			throw new ChessException(Messages.getString("ChessCommandExecutor.noTeleporting"));
		}
		
		Player player = (Player)sender;
		
		switch (args.length) {
		case 0:
			BoardViewManager.getManager().teleportOut(player);
			break;
		case 1:
			if (getBooleanOption("b")) {
				PermissionUtils.requirePerms(sender, "chesscraft.commands.teleport.board");
				BoardViewManager.getManager().getBoardView(args[0]).summonPlayer(player);
			} else {
				ChessGame game = ChessGameManager.getManager().getGame(args[0], true);
				game.getView().summonPlayer(player);
			}
			break;
		}
		return true;
	}

}
