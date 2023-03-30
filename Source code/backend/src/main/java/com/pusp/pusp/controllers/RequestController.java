package com.pusp.pusp.Controllers;

import java.time.Duration;

import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

@RestController
public class RequestController {

    

	public final Bucket bucket;
	
	public RequestController() {
		Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
		this.bucket = Bucket4j.builder()
				.addLimit(limit)
				.build();
		}

}
    

