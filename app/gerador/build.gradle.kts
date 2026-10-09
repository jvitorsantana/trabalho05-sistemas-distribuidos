plugins {
    application
}

dependencies {
    implementation(project(":common"))
    runtimeOnly(libs.slf4j.simple)
}

application {
    mainClass = "com.jvitorsantana.gerador.GeradorApp"
}