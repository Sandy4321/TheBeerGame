package test.tests;

import edu.utfpr.ct.datamodel.AbstractNode;
import edu.utfpr.ct.datamodel.Function;
import edu.utfpr.ct.datamodel.Game;
import edu.utfpr.ct.datamodel.Node;
import edu.utfpr.ct.datamodel.TravellingTime;
import edu.utfpr.ct.gamecontroller.Engine;
import edu.utfpr.ct.logmanager.Logger;
import java.util.Date;
import java.util.Random;
import edu.utfpr.ct.interfaces.ILogger;

public class LoggerTest
{
	private Game reference;
	private Game retrieved;

	public void test()
	{
		int qty;
		ILogger logger = Logger.getLogger();
		Comparator comparator = new Comparator();
		Engine engine;

		reference = new Game();
		reference.timestamp = new Date().getTime();
		reference.name = "GameTest";
		reference.password = "PasswordTest";
		reference.missingUnitCost = 1.0;
		reference.stockUnitCost = 2.0;
		reference.sellingUnitProfit = 3.0;
		reference.realDuration = 36;
		reference.informedDuration = 50;
		reference.deliveryDelay = 2;
		reference.unitiesOnTravel = 16;
		reference.initialStock = 16;
		reference.informedChainSupply = true;

		engine = new Engine();
		engine.setGame(reference, Function.RETAILER);
		engine.buildGame();
		logger.logGameStart(reference);
		fillPLayerMoves(logger);

		for(Game game : logger.getGames())
			if(game.timestamp == reference.timestamp)
				 retrieved = game;

		comparator.compareAll(reference, retrieved);

		logger.purgeGame(retrieved.gameID);
		qty = logger.getGames().length;
		System.out.println("Qtd de jogos depois do purge: " + qty);
	}

	private void fillPLayerMoves(ILogger logger)
	{
		Node node;
		Random random = new Random();

		for(AbstractNode abstractNode : reference.supplyChain)
		{
			if(abstractNode instanceof TravellingTime)
				continue;

			node = (Node) abstractNode;
			node.playerMove.clear();

			for(int i = 0; i < reference.realDuration; i++)
			{
				node.playerMove.add(Math.abs(random.nextInt()));
				logger.logPlayerMove(reference.gameID, node);
			}
		}
	}
}
