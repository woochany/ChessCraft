package me.desht.chesscraft.expector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.ChessGame;
import me.desht.dhutils.MiscUtil;

public class ExpectUndoResponse extends ExpectYesNoResponse {

	public ExpectUndoResponse(ChessGame game, String offerer) {
		super(game, offerer);
	}
	
	@Override
	public void doResponse(final String offeree) {
		deferTask(Bukkit.getPlayer(offerer), new Runnable() {

			@Override
			public void run() {
				if (accepted) {
					game.alert(offerer, Messages.getString("ExpectYesNoOffer.undoOfferAccepted", getPlayerName())); //$NON-NLS-1$
					game.undoMove(offerer);
				} else {
					game.alert(offerer, Messages.getString("ExpectYesNoOffer.undoOfferDeclined", getPlayerName())); //$NON-NLS-1$
					Player player = Bukkit.getPlayer(offeree);
					if (player != null) {
						MiscUtil.statusMessage(player, Messages.getString("ExpectYesNoOffer.youDeclinedUndoOffer")); //$NON-NLS-1$
					}
				}
			}
		});
	}
}
