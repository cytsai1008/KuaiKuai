package cytsai1008.kuaikuai

import cytsai1008.kuaikuai.toolWindow.MyToolWindowFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import javax.imageio.ImageIO

class KuaiKuaiTest : BasePlatformTestCase() {

    fun testImageResourceExists() {
        val stream = javaClass.getResourceAsStream("/images/kuaikuai.png")
        assertNotNull("kuaikuai.png must be on the classpath", stream)
        stream!!.close()
    }

    fun testImageIsValidPng() {
        val stream = javaClass.getResourceAsStream("/images/kuaikuai.png")!!
        val image = ImageIO.read(stream)
        assertNotNull("kuaikuai.png must be readable by ImageIO", image)
        assertTrue("image width should be positive", image.width > 0)
        assertTrue("image height should be positive", image.height > 0)
    }

    fun testToolWindowFactoryShouldBeAvailable() {
        val factory = MyToolWindowFactory()
        assertTrue(factory.shouldBeAvailable(project))
    }
}
