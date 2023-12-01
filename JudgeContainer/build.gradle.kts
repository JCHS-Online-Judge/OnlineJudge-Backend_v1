import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("com.bmuschko.docker-remote-api") version "9.4.0"
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.13")

    implementation("org.mongodb:mongodb-driver-sync:4.11.1")

    implementation("org.java-websocket:Java-WebSocket:1.5.4")
}

docker {
    url.set("unix:///var/run/docker.sock")
}

tasks.create("fatJar", Jar::class) {
    group = "build"

    archiveBaseName = "${project.name}-fat"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.github.ioloolo.onlinejudge.judge.container.JudgeContainer"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    with(tasks["jar"] as CopySpec)
}

tasks.create("buildImage", DockerBuildImage::class) {
    group = "docker"

    dependsOn("fatJar")

    inputDir.set(file("./"))
    images.add("oj-judge-container")
}
