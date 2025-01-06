package atu.ie.libraryadvisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String BORROW_BOOK_QUEUE = "borrow.book.queue";
    public static final String RETURN_BOOK_QUEUE = "return.book.queue";
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    @Bean
    public Queue borrowBookQueue() {
        log.info("Declaring {}", BORROW_BOOK_QUEUE);
        return new Queue(BORROW_BOOK_QUEUE, true);
    }
    @Bean
    public Queue returnBookQueue() {
        log.info("Declaring {}", RETURN_BOOK_QUEUE);
        return new Queue(RETURN_BOOK_QUEUE, true);
    }
}
