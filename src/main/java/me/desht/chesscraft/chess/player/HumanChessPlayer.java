package me.desht.chesscraft.chess.player;

import me.desht.chesscraft.ChessCraft;
import me.desht.chesscraft.Messages;
import me.desht.chesscraft.chess.ChessGame;
import me.desht.chesscraft.chess.TimeControl;
import me.desht.chesscraft.exceptions.ChessException;
import me.desht.chesscraft.expector.ExpectDrawResponse;
import me.desht.chesscraft.expector.ExpectSwapResponse;
import me.desht.chesscraft.expector.ExpectYesNoResponse;
import me.desht.chesscraft.util.ChessUtils;
import me.desht.dhutils.MiscUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HumanChessPlayer extends ChessPlayer {

	private Player player;

	public HumanChessPlayer(String name, ChessGame game, int colour) {
		super(name, game, colour);
	}

	private Player getBukkitPlayer() {
		if (player == null) {
			player = Bukkit.getPlayer(getName());
		} else {
			if (!player.isOnline())
				player = null;
		}
		
		return player;
	}
	
	@Override
	public String getDisplayName() {
		return ChatColor.GOLD + getName() + ChatColor.RESET;
	}
	
	@Override
	public void promptForFirstMove() {
		alert(Messages.getString("Game.started", ChessUtils.getDisplayColour(getColour()), ChessUtils.getWandDescription()));
	}

	@Override
	public void promptForNextMove() {
		Player p = getBukkitPlayer();
		if (p == null)
			return;
		
		alert(Messages.getString("Game.playerPlayedMove",
		                         ChessUtils.getDisplayColour(getOtherColour()),
		                         getGame().getPosition().getLastMove().getSAN()));
		
		if (getGame().getPosition().isCheck()) {
			playEffect("check");
			alert(Messages.getString("Game.check"));
		}
	}

	@Override
	public void alert(String message) {
		Player p = getBukkitPlayer();
		if (p == null) return;
		MiscUtil.alertMessage(p, Messages.getString("Game.alertPrefix", getGame().getName()) + message);
	}

	@Override
	public void statusMessage(String message) {
		Player p = getBukkitPlayer();
		if (p == null) return;
		MiscUtil.statusMessage(p, message);
	}

	@Override
	public void replayMoves() {
		// nothing to do here
	}

	@Override
	public void cleanup() {
		player = null;
	}

	@Override
	public void validateAffordability(String error) {
		if (error == null) error = "Game.cantAffordToJoin";
		double stake = getGame().getStake();
		if (ChessCraft.economy != null && !ChessCraft.economy.has(getName(), stake)) {
			throw new ChessException(Messages.getString(error, ChessUtils.formatStakeStr(stake)));
		}
	}

	@Override
	public void validateInvited(String error) {
		String invited = getGame().getInvited();
		if (!invited.equals(ChessGame.OPEN_INVITATION) && !invited.equalsIgnoreCase(getName())) { 
			throw new ChessException(Messages.getString(error));
		}
	}

	@Override
	public boolean isHuman() {
		return true;
	}

	@Override
	public void withdrawFunds(double amount) {
		ChessCraft.economy.withdrawPlayer(getName(), amount);
		alert(Messages.getString("Game.paidStake", ChessUtils.formatStakeStr(amount)));
	}

	@Override
	public void depositFunds(double amount) {
		ChessCraft.economy.depositPlayer(getName(), amount);
	}

	@Override
	public void summonToGame() {
		Player p = getBukkitPlayer();
		if (p != null) {
			getGame().getView().summonPlayer(p);
		} else {
			// TODO: player's gone offline...
		}
	}

	@Override
	public void cancelOffers() {
		Player p = getBukkitPlayer();
		
		if (p != null) {
			// making a move after a draw/swap/undo offer has been made is equivalent to declining the offer
			ExpectYesNoResponse.handleYesNoResponse(p, false);
		}
	}

	@Override
	public double getPayoutMultiplier() {
		return 2.0;
	}

	@Override
	public void drawOffered() {
		String offerer = getGame().getOtherPlayerName(getName());
		
		ChessCraft.getInstance().responseHandler.expect(getName(), new ExpectDrawResponse(getGame(), offerer));
		
		alert(Messages.getString("ChessCommandExecutor.drawOfferedOther", offerer));
		alert(Messages.getString("ChessCommandExecutor.typeYesOrNo"));
	}

	@Override
	public void swapOffered() {
		String offerer = getGame().getOtherPlayerName(getName());
		
		ChessCraft.getInstance().responseHandler.expect(getName(), new ExpectSwapResponse(getGame(), offerer));
		
		alert(Messages.getString("ChessCommandExecutor.swapOfferedOther", offerer));
		alert(Messages.getString("ChessCommandExecutor.typeYesOrNo"));
	}

	@Override
	public void undoLastMove() {
		// do nothing here
	}

	@Override
	public void checkPendingAction() {
		// do nothing here
	}

	@Override
	public void playEffect(String effect) {
		Player p = getBukkitPlayer();
		if (p != null) {
			ChessCraft.getInstance().getFX().playEffect(p.getLocation(), effect);
		}
	}

	@Override
	public void notifyTimeControl(TimeControl timeControl) {
		if (timeControl.isNewPhase()) {
			alert(Messages.getString("Game.newTimeControlPhase", timeControl.phaseString()));
		} else if (getGame().getPosition().getPlyNumber() <= 2) {
			alert(Messages.getString("ChessCommandExecutor.gameDetail.timeControlType", getGame().getTimeControl(getColour()).getSpec()));
		}
	}

}
