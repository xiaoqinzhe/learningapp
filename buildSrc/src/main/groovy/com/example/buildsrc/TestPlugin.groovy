import org.gradle.api.Plugin
import org.gradle.api.Project

class TestPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.task("testPlugin") {
            println "testPlugin"
            doLast {
                println "testPlugin dolast"
            }
        }
    }
}