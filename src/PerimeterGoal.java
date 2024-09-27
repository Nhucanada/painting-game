import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {

		Color[][] flatBoard = board.flatten();
		int max = flatBoard.length - 1;
		int score = 0;

		for (int i=0; i <= max; i++) {

			for (int j=0; j <= max; j++) {

				if (i == 0 || i == max || j == 0 || j == max) {

					if (flatBoard[i][j] == this.targetGoal) {

						score++;

					}

				}

				if ((i==0 && j==0)|| (i==max && j==max) || (i==0 && j==max) || (i==max && j==0) ) {

					if (flatBoard[i][j] == this.targetGoal) {

						score++;

					}

				}

			}

		}

		return score;

	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal)
				+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
