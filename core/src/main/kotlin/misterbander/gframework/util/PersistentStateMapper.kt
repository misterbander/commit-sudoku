package misterbander.gframework.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.collections.set


/**
 * Maps string keys to serializable objects that can be saved to file storage. Useful for saving game state.
 * @author Mister_Bander
 */
class PersistentStateMapper(filePath: String)
{
	private val stateFile: FileHandle = Gdx.files.local(filePath)
	val stateMap: HashMap<String, Serializable> = HashMap()
	
	inline operator fun <reified T : Serializable> get(key: String): T?
	{
		return stateMap[key] as? T?
	}
	
	operator fun set(key: String, value: Serializable)
	{
		stateMap[key] = value
	}
	
	/**
	 * Reads state from file storage.
	 * @return True if state is successfully read, false if file does not exist.
	 */
	@Suppress("UNCHECKED_CAST")
	fun read(): Boolean
	{
		if (stateFile.exists())
		{
			val inputStream = ObjectInputStream(stateFile.read())
			stateMap.putAll(inputStream.readObject() as HashMap<String, Serializable>)
			inputStream.close()
			return true
		}
		return false
	}
	
	/**
	 * Writes state to file storage.
	 */
	fun write()
	{
		val outputStream = ObjectOutputStream(stateFile.write(false))
		outputStream.writeObject(stateMap)
		outputStream.close()
	}
}
