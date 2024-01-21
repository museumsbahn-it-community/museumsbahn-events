import mu.KotlinLogging
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


@Component
class EndpointsListener {
    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
        val applicationContext = event.applicationContext
        applicationContext.getBean(RequestMappingHandlerMapping::class.java)
            .getHandlerMethods().forEach { (key: RequestMappingInfo?, value: HandlerMethod?) ->
                LOGGER.info(
                    "{} {}",
                    key,
                    value
                )
            }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {  }
    }
}