package pt.matriz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatrizApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(MatrizApplication.class, args);
        if (context.getEnvironment().getProperty("matriz.demo.seed-only", Boolean.class, false)) {
            System.exit(SpringApplication.exit(context));
        }
    }
}
