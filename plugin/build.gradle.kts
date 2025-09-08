group = "net.savagedev"
version = "1.0.4"

subprojects {
    dependencies {
        implementation("redis.clients:jedis:5.2.0")
        implementation("com.rabbitmq:amqp-client:5.24.0")
    }
}
