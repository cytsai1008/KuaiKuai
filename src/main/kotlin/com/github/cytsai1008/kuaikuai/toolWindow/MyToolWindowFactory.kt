package com.github.cytsai1008.kuaikuai.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentFactory
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setType(ToolWindowType.FLOATING, null)
        val panel = KuaiKuaiPanel()
        val content = ContentFactory.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}

private class KuaiKuaiPanel : JPanel() {

    private val source: BufferedImage? =
        javaClass.getResourceAsStream("/images/kuaikuai.png")?.let { ImageIO.read(it) }

    init {
        preferredSize = Dimension(80, 100)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) = repaint()
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val img = source ?: return
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
