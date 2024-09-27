import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {

		Color[][] boardColors = board.flatten();

		int score = 0;

		boolean[][] visited = new boolean[boardColors.length][boardColors.length];

		for(int i=0; i < boardColors.length; i++){

			for(int j=0; j < boardColors.length; j++){

				int blobSize = undiscoveredBlobSize(i,j,boardColors,visited);

				if(blobSize > score){

					score = blobSize;

				}

			}

		}

		return score;

	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
				+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {

		if (i < 0 || j < 0 || i >= unitCells.length || j >= unitCells[0].length || visited[i][j]) {

			return 0;

		}

		visited[i][j] = true;

		if (!unitCells[i][j].equals(targetGoal)) {

			return 0;

		}

		int blobSize = 1;

		blobSize += undiscoveredBlobSize(i + 1, j, unitCells, visited);
		blobSize += undiscoveredBlobSize(i - 1, j, unitCells, visited);
		blobSize += undiscoveredBlobSize(i, j + 1, unitCells, visited);
		blobSize += undiscoveredBlobSize(i, j - 1, unitCells, visited);

		return blobSize;

	}

}
