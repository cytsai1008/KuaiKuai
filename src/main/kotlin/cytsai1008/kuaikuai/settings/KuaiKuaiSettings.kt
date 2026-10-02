package cytsai1008.kuaikuai.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.selected

enum class ImageChoice(val resource: String?) {
    CLASSIC("/images/kuaikuai.png"),
    CHOCO("/images/kuaikuai_choco.png"),
    CUSTOM(null),
}

@Service(Service.Level.APP)
@State(name = "KuaiKuaiSettings", storages = [Storage("kuaikuai.xml")])
class KuaiKuaiSettings : SimplePersistentStateComponent<KuaiKuaiSettings.Options>(Options()) {

    class Options : BaseState() {
        var image by enum(ImageChoice.CLASSIC)
        var customPath by string()
        var chocoOnFailure by property(false)
    }

    companion object {
        fun getInstance(): KuaiKuaiSettings = service()
    }
}

class KuaiKuaiConfigurable : BoundConfigurable("KuaiKuai") {

    private val options get() = KuaiKuaiSettings.getInstance().state

    override fun createPanel(): DialogPanel = panel {
        lateinit var custom: javax.swing.JRadioButton
        buttonsGroup("Image:") {
            row { radioButton("Classic (green)", ImageChoice.CLASSIC) }
            row { radioButton("Chocolate (red)", ImageChoice.CHOCO) }
            row {
                custom = radioButton("Custom:", ImageChoice.CUSTOM).component
                textFieldWithBrowseButton(FileChooserDescriptorFactory.singleFile())
                    .bindText({ options.customPath.orEmpty() }, { options.customPath = it })
                    .enabledIf(custom.selected)
                    .comment("PNG, JPEG, GIF or BMP")
            }
        }.bind(options::image)
        row {
            checkBox("Show the red 乖乖 when a run fails")
                .bindSelected(options::chocoOnFailure)
        }
    }

    override fun apply() {
        super.apply()
        ProjectManager.getInstance().openProjects.forEach {
            ToolWindowManager.getInstance(it).getToolWindow("KuaiKuai")?.component?.repaint()
        }
    }
}
