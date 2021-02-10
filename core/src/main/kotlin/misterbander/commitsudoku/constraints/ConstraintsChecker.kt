package misterbander.commitsudoku.constraints

import ktx.actors.txt
import misterbander.commitsudoku.scene2d.SudokuGrid

class ConstraintsChecker(private val grid: SudokuGrid)
{
	// Preset constraints
	private val sudokuConstraint = SudokuConstraint(grid)
	
	fun check()
	{
		println("Checking constraints")
		grid.cells.forEach { it.forEach { cell -> cell.isCorrect = true } }
		var correctFlag = true
		if (correctFlag && sudokuConstraint.check())
		{
			grid.screen.isFinished = true
			grid.screen.modeLabel.txt = "Completed!"
			grid.screen.timer.isRunning = false
		}
	}
}
