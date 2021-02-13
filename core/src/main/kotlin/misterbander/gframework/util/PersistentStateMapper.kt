package misterbander.gframework.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxMap
import ktx.collections.set
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


/**
 * Maps string keys to serializable objects that can be saved to file storage. Useful for saving game state.
 * @author Mister_Bander
 */
class PersistentStateMapper(filePath: String)
{
	private val stateFile: FileHandle = Gdx.files.local(filePath)
	val stateMap: GdxMap<String, Serializable> = GdxMap()
	
	inline operator fun <reified T : Serializable> get(key: String): T?
	{
		return stateMap[key] as T?
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
			val size = inputStream.readInt()
			for (i in 0 until size)
			{
				val key = inputStream.readUTF()
				val value = inputStream.readObject() as Serializable
				stateMap[key] = value
			}
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
		outputStream.writeInt(stateMap.size)
		stateMap.forEach { entry: ObjectMap.Entry<String, Serializable> ->
			outputStream.writeUTF(entry.key)
			outputStream.writeObject(entry.value)
		}
		outputStream.close()
	}
}
