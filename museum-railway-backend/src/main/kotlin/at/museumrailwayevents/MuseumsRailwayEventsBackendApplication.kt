package at.museumrailwayevents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@OpenAPIDefinition(
//    servers = [
//        Server(url = "/", description = "Default Server URL")
//    ],
//    info = Info(
//        title = "Boudicca EventDB",
//        version = "0.1",
//        license = License(
//            name = "unknown"
//        )
//    ),
//)
@SpringBootApplication
class MuseumRailwayBackendApplication : WebMvcConfigurer {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers("/manage/**").hasRole("MANAGE")
            it.requestMatchers("/**").permitAll()
        }
        http.httpBasic(withDefaults())
        http.csrf {
            it.disable()
        }
        return http.build()
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}

fun main(args: Array<String>) {
    runApplication<MuseumRailwayBackendApplication>(*args)
}
