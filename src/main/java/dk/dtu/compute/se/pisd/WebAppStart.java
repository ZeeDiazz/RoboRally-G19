package dk.dtu.compute.se.pisd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.out;

@SpringBootApplication
public class WebAppStart {
    public static void main(String[] args) {

        SpringApplication.run(WebAppStart.class, args);
        out.print("Executed successfully");
    }

}