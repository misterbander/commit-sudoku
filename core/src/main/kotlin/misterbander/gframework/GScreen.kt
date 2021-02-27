package misterbander.gframework

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.collections.GdxSet
import ktx.collections.plusAssign
import misterbander.gframework.scene2d.AccessibleInputWindow
import misterbander.gframework.scene2d.GContactListener
import misterbander.gframework.scene2d.GObject


/**
 * `GScreen`s are extensions of [KtxScreen]s. `GScreen`s have a main camera, a viewport, and a `Scene2D` [Stage] already
 * defined for you. All you need to do is to override the `show()` method and place your initialization code in there.
 * This could include setting up Tiled maps, creating [GObject]s and/or build your UI.
 *
 * The camera and the viewport can be overridden to use your own camera and/or viewport.
 *
 * `GScreen`s provide convenient methods to spawn `GObject`s.
 *
 * `GScreen`s may also optionally include a `Box2D` world.
 * @property game parent GFramework instance
 */
abstract class GScreen<T : GFramework>(val game: T) : KtxScreen, ContactListener
{
	/** Main camera for this GScreen. Defaults to an `OrthographicCamera`. */
	open val camera: Camera = OrthographicCamera().apply { setToOrtho(false) }
	/** Viewport to project camera contents. Defaults to `ExtendViewport`. */
	open val viewport: Viewport by lazy { ExtendViewport(1000F, 600F, camera) }
	val stage by lazy { Stage(viewport, game.batch) }
	val accessibleInputWindows = GdxSet<AccessibleInputWindow>()
	
	open val world: World? = null
	open val mpp = 0.25F
	
	val scheduledAddingGObjects = GdxSet<GObject<T>>()
	val scheduledRemovalGObjects = GdxSet<GObject<T>>()
	
	override fun show()
	{
		Gdx.input.inputProcessor = stage
		world?.setContactListener(this)
	}
	
	/**
	 * Spawns the GObject into the world and adds it to the stage. Calls `GObject::onSpawn()`.
	 */
	fun spawnGObject(gObject: GObject<T>)
	{
		stage += gObject
		gObject.onSpawn()
	}
	
	fun scheduleSpawnGObject(gObject: GObject<T>)
	{
		scheduledAddingGObjects += gObject
	}
	
	override fun beginContact(contact: Contact)
	{
		if (contact.fixtureA.body?.userData is GContactListener)
			(contact.fixtureA.body.userData as GContactListener).beginContact(contact.fixtureB)
		if (contact.fixtureB.body?.userData is GContactListener)
			(contact.fixtureB.body.userData as GContactListener).beginContact(contact.fixtureA)
	}
	
	override fun endContact(contact: Contact)
	{
		if (contact.fixtureA.body?.userData is GContactListener)
			(contact.fixtureA.body.userData as GContactListener).endContact(contact.fixtureB)
		if (contact.fixtureB.body?.userData is GContactListener)
			(contact.fixtureB.body.userData as GContactListener).endContact(contact.fixtureA)
	}
	
	override fun preSolve(contact: Contact, oldManifold: Manifold) {}
	
	override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
	
	override fun resize(width: Int, height: Int)
	{
		viewport.update(width, height, true)
	}
	
	fun onLayoutSizeChange(screenHeight: Int)
	{
		accessibleInputWindows.forEach { it.adjustPosition(screenHeight) }
	}
	
	override fun render(delta: Float)
	{
		clearScreen()
		
		camera.update()
		game.batch.projectionMatrix = camera.combined
		game.shapeRenderer.projectionMatrix = camera.combined
		game.shapeDrawer.update()
		
		for (gObject in scheduledAddingGObjects)
			spawnGObject(gObject)
		scheduledAddingGObjects.clear()
		
		stage.act(delta)
		stage.draw()
		
		world?.step(1/60F, 6, 4)
		
		for (gObject in scheduledRemovalGObjects)
			gObject.remove()
		scheduledRemovalGObjects.clear()
	}
	
	/**
	 * Clears the screen and paints it black. Gets called every frame.
	 *
	 * You can override this to change the background color.
	 */
	open fun clearScreen()
	{
		Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
	}
	
	override fun dispose()
	{
		stage.dispose()
	}
}
