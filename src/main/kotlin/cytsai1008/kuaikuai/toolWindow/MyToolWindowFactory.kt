package cytsai1008.kuaikuai.toolWindow

import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentFactory
import cytsai1008.kuaikuai.settings.ImageChoice
import cytsai1008.kuaikuai.settings.KuaiKuaiSettings
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setType(ToolWindowType.FLOATING, null)
        val panel = KuaiKuaiPanel(project, toolWindow.disposable)
        val content = ContentFactory.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}

private class KuaiKuaiPanel(project: Project, parent: Disposable) : JPanel() {

    @Volatile
    private var failed = false
    private var loadedKey: String? = null
    private var loaded: BufferedImage? = null

    init {
        preferredSize = Dimension(80, 100)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) = repaint()
        })
        // Any non-zero exit counts as failed, including a manual stop.
        project.messageBus.connect(parent).subscribe(ExecutionManager.EXECUTION_TOPIC, object : ExecutionListener {
            override fun processTerminated(
                executorId: String,
                env: ExecutionEnvironment,
                handler: ProcessHandler,
                exitCode: Int,
            ) {
                failed = exitCode != 0
                repaint()
            }
        })
    }

    private fun currentImage(): BufferedImage? {
        val options = KuaiKuaiSettings.getInstance().state
        val choice = if (failed && options.chocoOnFailure) ImageChoice.CHOCO else options.image
        val key = choice.resource ?: options.customPath.orEmpty()
        if (key != loadedKey) {
            loadedKey = key
            loaded = choice.resource?.let { readResource(it) }
                ?: runCatching { ImageIO.read(File(key)) }.getOrNull()
                ?: readResource(ImageChoice.CLASSIC.resource!!)
        }
        return loaded
    }

    private fun readResource(path: String): BufferedImage? =
        javaClass.getResourceAsStream(path)?.use { ImageIO.read(it) }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val img = currentImage() ?: return
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        val padding = 12
        val panelW = width - padding * 2
        val panelH = height - padding * 2
        val imgW = img.width
        val imgH = img.height

        val scale = minOf(panelW.toDouble() / imgW, panelH.toDouble() / imgH)
        val drawW = (imgW * scale).toInt()
        val drawH = (imgH * scale).toInt()
        val x = padding + (panelW - drawW) / 2
        val y = padding + (panelH - drawH) / 2

        g2.drawImage(img, x, y, drawW, drawH, null)
    }
}
