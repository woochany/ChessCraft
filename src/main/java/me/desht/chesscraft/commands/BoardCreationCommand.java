package me.desht.chesscraft.commands;

import me.desht.chesscraft.ChessCraft;
import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.BoardStyle;
import me.desht.chesscraft.exceptions.ChessException;
import me.desht.chesscraft.expector.ExpectBoardCreation;
import me.desht.dhutils.MiscUtil;
import me.desht.dhutils.commands.AbstractCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class BoardCreationCommand extends AbstractCommand {

	public BoardCreationCommand() {
		super("chess b c", 1);
		addAlias("chess c b");	// backwards compat
		setPermissionNode("chesscraft.commands.create.board");
		setUsage("/chess board create <board-name> [-style <style-name>] [-pstyle <style-name>]");
		setOptions(new String[] { "style:s", "pstyle:s" });
	}

	@Override
	public boolean execute(Plugin plugin, CommandSender sender, String[] args) throws ChessException {

		notFromConsole(sender);
		
		if (args.length == 0 || args[0].startsWith("-")) {
			showUsage(sender);
			return true;
		}

		String name = args[0];
		
		String boardStyleName = getStringOption("style"); //$NON-NLS-1$
		String pieceStyleName = getStringOption("pstyle"); //$NON-NLS-1$
		
		// this will throw an exception if the styles are in any way invalid or incompatible
		BoardStyle.verifyCompatibility(boardStyleName, pieceStyleName);

		MiscUtil.statusMessage(sender, Messages.getString("ChessCommandExecutor.boardCreationPrompt", name)); //$NON-NLS-1$
		ChessCraft.getInstance().responseHandler.expect(sender.getName(), new ExpectBoardCreation(name, boardStyleName, pieceStyleName));
		
		return true;
	}

}
