package cytsai1008.kuaikuai

import cytsai1008.kuaikuai.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MyPluginTest : BasePlatformTestCase() {

    fun testProjectServiceIsAvailable() {
        val service = project.service<MyProjectService>()
        assertNotNull(service)
    }
}
