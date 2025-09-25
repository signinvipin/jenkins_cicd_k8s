
package com.levo.dockerexample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping(value = "/", produces = "text/html")
    public String test() {
        return """
            <html>
              <body>
                <h2>Hello! This is a test app.</h2>
                <p>Congratulations on setting up the Docker image successfully.</p>
                <pre>
  /\\_/\\  
 ( o.o ) 
  > ^ < 
                </pre>
              </body>
            </html>
        """;
    }
}
