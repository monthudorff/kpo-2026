plugins {
    application
    checkstyle
    java
    jacoco
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "ru.hse.vyshkat.Main"
}

val springVersion = "7.0.9"
val mockitoVersion = "5.24.0"

// Mockito подключается к тестам как java-агент: на JDK 21+ самоподключение
// выдаёт предупреждение и в будущих версиях JDK перестанет работать.
val mockitoAgent: Configuration by configurations.creating

dependencies {
    implementation("org.springframework:spring-context:$springVersion")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.springframework:spring-test:$springVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    mockitoAgent("org.mockito:mockito-core:$mockitoVersion") {
        isTransitive = false
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}", "-Xshare:off")
}

// Интерактивное меню читает System.in.
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required = true
        xml.required = false
        csv.required = false
    }
}

// Ориентир задания — покрытие не ниже 60%: ниже — check падает.
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "0.60".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

val sunChecks = configurations.detachedConfiguration(
    dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
).apply {
    isTransitive = false
}

checkstyle {
    toolVersion = "14.1.0"
    config = resources.text.fromFile(
        layout.buildDirectory.file("checkstyle/sun_checks.xml").get().asFile
    )
    isShowViolations = true
    isIgnoreFailures = false
    maxWarnings = 0
}

val checkstyleConfig = layout.buildDirectory.file("checkstyle/sun_checks.xml")

// Как в practise-4: стандартный Sun-конфиг без JavadocPackage и JavadocVariable.
val prepareCheckstyleConfig = tasks.register("prepareCheckstyleConfig") {
    inputs.files(sunChecks)
    outputs.file(checkstyleConfig)
    doLast {
        val standardConfig = resources.text
            .fromArchiveEntry(sunChecks, "sun_checks.xml")
            .asString()
        val configWithoutPackageInfoRule = standardConfig
            .replace("<module name=\"JavadocPackage\"/>", "")
            .replace("<module name=\"JavadocVariable\"/>", "")
        checkstyleConfig.get().asFile.apply {
            parentFile.mkdirs()
            writeText(configWithoutPackageInfoRule)
        }
    }
}

tasks.withType<Checkstyle>().configureEach {
    dependsOn(prepareCheckstyleConfig)
    reports {
        html.required = true
        xml.required = false
    }
}
