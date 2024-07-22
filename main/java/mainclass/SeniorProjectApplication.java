package mainclass;

import client.DatabaseManager;
import client.SignUp;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;

@SpringBootApplication
public class SeniorProjectApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SeniorProjectApplication.class)
                .headless(false).run(args);
        Connection conn = DatabaseManager.getConnection();
        SignUp sw = new SignUp(conn);
    }
}
