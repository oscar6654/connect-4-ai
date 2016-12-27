import connectK.CKPlayer;
import connectK.BoardModel;
   

import java.awt.Point;
import java.lang.Math;

public class MyAI extends CKPlayer {
	
	private static final int DEFAULT_DEADLINE = 4300;

	public MyAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "LazyAI";
	}

	@Override
	public Point getMove(BoardModel state) {
		
		long start_time = System.nanoTime();
		int FinalBestAlphas[][] = new int[state.getWidth()][state.getHeight()];
		int CurrentBestAlphas[][];
		boolean timeUp = false;
		
		byte oponent=1;
		if(player==1)
			oponent=2;
		for (int depth = 0; ;depth++)
		{
			CurrentBestAlphas = new int[state.getWidth()][state.getHeight()];
			for (int i = 0; i < state.getWidth(); i++)
			{
				for (int j = 0; j < state.getHeight(); j++)
				{
					if(state.getSpace(i, j)==0)
					{
						BoardModel nextBoard = state.placePiece(new Point(i,j), this.player);
						CurrentBestAlphas[i][j] = miniMax(nextBoard, depth, oponent, Integer.MIN_VALUE, Integer.MAX_VALUE, start_time);
						if (CurrentBestAlphas[i][j] == Integer.MAX_VALUE)
							return new Point(i,j);
					}
					if (checkDeadline(start_time)){
						timeUp = true;
						break;
					}
				}
			}
			if (timeUp)
				break;
			FinalBestAlphas = CurrentBestAlphas;
			}
			int i = 0;
			int j = state.getWidth() - 1;
			int X = 0;
			int Y = 0;
			int P;
			int bestAlpha = FinalBestAlphas[0][0];
			while (i <= j)
			{
				int k = 0;
				int m = state.getHeight() - 1;
				
				while (k <= m)
				{
					if (FinalBestAlphas[j][m] >= bestAlpha){
						bestAlpha = FinalBestAlphas[j][m];
						X = j;
						Y = m;
					}
					if (FinalBestAlphas[j][k] >= bestAlpha){
						bestAlpha = FinalBestAlphas[j][k];
						X = j;
						Y = k;
					}
					if (FinalBestAlphas[i][m] >= bestAlpha){
						bestAlpha = FinalBestAlphas[i][m];
						X = i;
						Y = m;
					}
					if (FinalBestAlphas[i][k] >= bestAlpha){
						bestAlpha = FinalBestAlphas[i][k];
						X = i;
						Y = k;
					}
					k++;
					m--;
				}
				j--;
				i++;
			}
			
			/*for (i = 0; i < state.getWidth(); i++)
			{
				for (j = 0; j < state.getHeight(); j++)
				{
					System.out.println(i+" "+j+" "+FinalBestAlphas[i][j]);
				}
			}*/
			
			if(state.getSpace(X, Y)==0)
			//System.out.println(X+" "+Y);
				return new Point(X, Y);
			else
			{
				for (i = 0; i < state.getWidth(); i++)
				{
					for (j = state.getHeight()-1; j >=0 ; j--)
					{
						if(bestAlpha==FinalBestAlphas[i][j])
							if(state.getSpace(i, j)==0)
							return new Point(i, j);
						
					}
				}
				}
				for (i = state.getWidth()-1; i >=0 ; i--)
				{
					for (j = 0; j < state.getHeight(); j++)
					{
							if(state.getSpace(i, j)==0)
							return new Point(i, j);
						
					}
				}

			return null;
		
	}
	
	@Override
	public Point getMove(BoardModel state, int deadline) {	
		return getMove(state);
	}

	public int miniMax(BoardModel state, int depth, byte turn, int alpha, int beta ,long startTime){
		if (checkDeadline(startTime))
			return 0;
		byte oponent=1;
		if(player==1)
			oponent=2;
		if (depth == 0) {
				if (state.winner()==this.player)
					return Integer.MAX_VALUE;
				else if(state.winner()!=-1) 
					return Integer.MIN_VALUE;
			return Heuristic_Evaluation_Functions(state); 
		}
		else
		{
			if (turn==this.player) {
				for (int i = 0; i < state.getWidth(); i++){
					for (int j = 0; j < state.getHeight(); j++){
						if (state.getSpace(i, j)==0)
						{
							BoardModel nextBoard = state.placePiece(new Point(i,j), this.player);
							alpha = Math.max(alpha, miniMax(nextBoard, depth-1,oponent ,alpha, beta,startTime));
							if (beta <= alpha) 
								return alpha;
						}
					}
				}
				return alpha;

			} 
			else 
			{
				for (int i = 0; i < state.getWidth(); i++){
					for (int j = 0; j < state.getHeight(); j++){
						if (state.getSpace(i, j)==0)
						{
						BoardModel nextBoard = state.placePiece(new Point(i,j), oponent);
						beta = Math.min(beta, miniMax(nextBoard, depth-1, this.player,alpha, beta,startTime));
						if (beta <= alpha)
							return beta;
						}
					}
				}
				return beta;
			}
		}

	}
	
	
	
	public boolean checkDeadline(long startTime){
		if (((System.nanoTime() - startTime) / 1000000) + 120 >= DEFAULT_DEADLINE)
			return true;
		else 
			return false;
	}


		
	
	
	public int Heuristic_Evaluation_Functions(BoardModel state)
	{
		
		Point lMove = state.getLastMove();
		if (lMove == null)
			return 0;
		
		int d_x = (int)(lMove.getX());
		int d_y = (int)(lMove.getY());
		byte lMoveOpponent;
		byte lMovePlayer = state.getSpace(lMove);
		if (lMovePlayer == 2)
			lMoveOpponent = 1;
		else
			lMoveOpponent = 2;
		int total=0;
		
		total=winning_possibility_x_axis(state,d_x,d_y,lMovePlayer,lMoveOpponent,total);
		total=winning_possibility_y_axis(state,d_x,d_y,lMovePlayer,lMoveOpponent,total);
		total=winning_possibility_xx_axis(state,d_x,d_y,lMovePlayer,lMoveOpponent,total);
		return winning_possibility_yy_axis(state,d_x,d_y,lMovePlayer,lMoveOpponent,total);
	}
	
		
	public int winning_possibility_yy_axis(BoardModel state,int x,int y,byte LMP,byte LMOP,int total)
	{
		
		
		int i = x - (state.getkLength()-1);
		int j = y - (state.getkLength()-1);
		while (i <= x + (state.getkLength()-1) && j >= y - (state.getkLength()-1)){
				if (i < 0 || i+(state.getkLength()-1) > state.getWidth()-1|| j < 0 || j+(state.getkLength()-1) > state.getHeight()-1){
					i++;
					j++;
					continue;
				}

				int count = 0; 
				int blocks = 0;
				boolean blocked = false;
				int currentCount = 0;
				for (int l = 0; l < state.getkLength(); l++){
					byte playerXY = state.getSpace(i+l, j+l);
					
					if (playerXY == LMOP){
						blocked = true;
						blocks++;
						break;
					}
					else if (playerXY == LMP)
						currentCount++;
					else
						continue; 
				}
				if (!blocked)
					count = currentCount;
				if (count != 0)
					total = total + (int) Math.pow(state.getkLength(), count);
				
				i++;
				j++;
		}
		
		
		return total;
	}
	
	
	public int winning_possibility_xx_axis(BoardModel state,int x,int y,byte LMP,byte LMOP,int total)
	{
		int i=x-(state.getkLength()-1);
		int j=y+(state.getkLength()-1);
		while (i <= (x+state.getkLength()-1) && j >= y-(state.getkLength()-1))
		{
			if (i < 0 || i+(state.getkLength()-1) > (state.getWidth()-1) || j > (state.getHeight()-1) || j-(state.getkLength()-1) < 0)
			{
				i++;
				j--;
				continue;
			}

			int count = 0;
			int blocks = 0;
			boolean blocked = false;
			int currentCount = 0; 
			for (int l = 0; l < state.getkLength(); l++){
				byte playerXY = state.getSpace((int)i+l, (int)j-l);
				
				if (playerXY == LMOP){
					blocked = true;
					blocks++;
					break;
				}
				else if (playerXY == LMP)
					currentCount++;
				else
					continue;
			}
			if (!blocked)
				count = currentCount;
			if (count != 0)
				total = total + (int) Math.pow(state.getkLength(), count);
			i++;
			j--;
		}
		
		return total;
	}
	
		
	public int winning_possibility_y_axis(BoardModel state,int x,int y,byte LMP,byte LMOP,int total)
	{
		for (int i = (y - (state.getkLength()-1)); i < y + state.getkLength(); i++){
			if (i < 0 || i+state.getkLength()-1 > state.getHeight()-1)
				continue;
			
			int count = 0; 
			int blocks = 0;
			boolean blocked = false;
			int currentCount = 0;
			for (int j = 0; j < state.getkLength(); j++){
				byte who = state.getSpace((int)x, (int)i+j);
				if (who == LMOP)
				{
					blocked = true;
					blocks++;
					break;
				}
				else if (who == LMP)
					currentCount++;
				else
					continue;
			}
			if (!blocked)
				count = currentCount;
			if (count != 0)
				total = total + (int) Math.pow(state.getkLength(), count);
		}
		
		return total;
	}

	
	public int winning_possibility_x_axis(BoardModel state,int x,int y,byte LMP,byte LMOP,int total)
	{
		for (int i = x - (state.getkLength()-1); i < x + state.getkLength(); i++)
		{
			if (i < 0 || i+(state.getkLength()-1) > (state.getWidth()-1))
				continue;
			int count = 0;
			int blocks = 0;
			boolean blocked = false;
			int currentCount = 0; 
			for (int j = 0; j < state.getkLength(); j++)
			{
				byte who = state.getSpace((i+j),y);
				if (who == LMOP)
				{
					blocked = true;
					blocks++;
					break;
				}
				else if (who == LMP)
					currentCount++;
				else
					continue;
			}
			if (!blocked)
				count = currentCount;
			if (count != 0)
				total = total + (int) Math.pow(state.getkLength(), count);
		}
		
		return total;
	}

}