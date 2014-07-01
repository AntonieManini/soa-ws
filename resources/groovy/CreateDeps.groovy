package groovy

import org.apache.maven.project.MavenProject

MavenProject mvnProject = project

new File(mvnProject.properties.finalDir, "${mvnProject.build.finalName}.dept").withWriter { writer ->
    mvnProject.dependencies.each() {
        writer.append("${it.groupId}:${it.artifactId}:${it.version} : ($it.scope)\n")
    }
}
