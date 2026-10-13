plugins {
    application
}

dependencies {
    implementation(project(":common"))
    implementation(libs.smile.core)
    implementation(libs.smile.nlp)
    runtimeOnly(libs.slf4j.simple)
}

application {
    mainClass = "com.jvitorsantana.suporte.ConsumidorSuporteApp"
}