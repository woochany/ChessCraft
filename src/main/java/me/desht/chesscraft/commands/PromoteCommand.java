package me.desht.chesscraft.commands;

import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.ChessGame;
import me.desht.chesscraft.chess.ChessGameManager;
import me.desht.chesscraft.exceptions.ChessException;
import me.desht.chesscraft.util.ChessUtils;
import me.desht.dhutils.MiscUtil;
import me.desht.dhutils.commands.AbstractCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import chesspresso.Chess;

public class PromoteCommand extends AbstractCommand {

	public PromoteCommand() {
		super("chess pr", 1, 1);
		setPermissionNode("chesscraft.commands.promote");
		setUsage("/chess promote");
	}

	@Override
	public boolean execute(Plugin plugin, CommandSender player, String[] args) throws ChessException {
		notFromConsole(player);

		ChessGame game = ChessGameManager.getManager().getCurrentGame(player.getName(), true);
		game.ensurePlayerInGame(player.getName());
		
		int piece = Chess.charToPiece(Character.toUpperCase(args[0].charAt(0)));
		int colour = game.getPlayerColour(player.getName());
		game.getPlayer(colour).setPromotionPiece(piece);
		MiscUtil.statusMessage(player, Messages.getString("ChessCommandExecutor.promotionPieceSet", //$NON-NLS-1$
		                                                    game.getName(),ChessUtils.pieceToStr(piece).toUpperCase()));
		game.getView().getControlPanel().repaintControls();

		return true;
	}

}

