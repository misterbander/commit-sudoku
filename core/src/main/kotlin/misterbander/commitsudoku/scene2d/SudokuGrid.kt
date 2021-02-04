package misterbander.commitsudoku.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.style.get
import misterbander.commitsudoku.CommitSudoku

class SudokuGrid(private val game: CommitSudoku) : Actor()
{
	val cells = Array(9) { j -> Array(9) { i -> Cell(i, j) } }
	
	init
	{
		x = 468F - gridSize/2
		y = 300F - gridSize/2
		width = gridSize
		height = gridSize
	}
	
	private val cellSize: Float
		get() = 64F
	
	private val gridSize: Float
		get() = 9*cellSize
	
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val shapeDrawer = game.shapeDrawer
		val lineColor: Color = game.skin["secondarycolor"]
		shapeDrawer.rectangle(x, y, gridSize, gridSize, lineColor, 3F)
		for (i in 1 until 9)
		{
			// Draw vertical lines
			shapeDrawer.line(x + i*cellSize, y, x + i*cellSize, y + 9*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
			// Draw horizontal lines
			shapeDrawer.line(x, y + i*cellSize, x + 9*cellSize, y + i*cellSize, if (i%3 == 0) 3F else 1F, true, lineColor, lineColor)
		}
		
		cells.forEach { it.forEach { cell -> cell.draw(batch) } }
	}
	
	inner class Cell(private val i: Int, private val j: Int)
	{
		var value = (1..9).random()
		var color = Color.CLEAR
		var isGiven = false
		var isCorrect = false
		val cornerMarks = Array(9) { false }
		val centerMarks = Array(9) { false }
		var hasCornerTextDecoration = false
		
		fun draw(batch: Batch)
		{
			game.segoeui2.apply {
				color = game.skin["primarycolor"]
				draw(batch, value.toString(), x + i*cellSize, y + j*cellSize)
			}
		}
	}
}