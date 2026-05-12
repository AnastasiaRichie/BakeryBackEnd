package org.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() { embeddedServer(Netty, port = 8080) { module() }.start(wait = true) }


fun main(args: Array<String>) {
    EngineMain.main(args)
}
//docker run --name ktor-postgres \
//  -e POSTGRES_DB=ktor_bakery_db \
//  -e POSTGRES_USER=ktor_user \
//  -e POSTGRES_PASSWORD=secret \
//  -p 5432:5432 \
//  -d postgres