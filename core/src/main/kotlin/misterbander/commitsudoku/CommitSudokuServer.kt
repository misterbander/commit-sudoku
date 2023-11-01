package misterbander.commitsudoku

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.log.info
import misterbander.gframework.util.PersistentStateMapper
import java.io.ObjectInputStream
import kotlin.concurrent.thread

class CommitSudokuServer(private val screen: CommitSudokuScreen)
{
	private lateinit var serverSocket: ServerSocket
	@Volatile private var shouldCloseServer = false
		set(value)
		{
			field = value
			if (value)
				serverSocket.dispose()
		}

	fun start(mapper: PersistentStateMapper)
	{
		shouldCloseServer = false
		thread(isDaemon = true) {
			val hints = ServerSocketHints()
			hints.acceptTimeout = 0
			serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, 11530, hints)
			info("CommitSudokuScreen    | INFO") { "Running server..." }
			while (true)
			{
				try
				{
					val socket = serverSocket.accept(null)
					info("CommitSudokuScreen    | INFO") { "Accepting connection from ${socket.remoteAddress}" }
					val objectInputStream = ObjectInputStream(socket.inputStream)
					mapper.read(objectInputStream)
					objectInputStream.close()
					socket.dispose()
					screen.reset()
					screen.readState(mapper)
				}
				catch (e: GdxRuntimeException)
				{
					if (shouldCloseServer)
						break
					else
						e.printStackTrace()
				}
			}
		}
	}

	fun stop()
	{
		shouldCloseServer = true
	}
}
