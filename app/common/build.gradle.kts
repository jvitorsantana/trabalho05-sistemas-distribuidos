plugins {
    `java-library`
}

dependencies {
    api(libs.amqp.client)
    api(libs.jackson.databind)
    api(libs.jackson.jsr310)
}