package com.example.demo;


import com.example.demo.security.LogEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class DemoApplication   {

	//private final LogEventListener logEventListener;

	/*public DemoApplication(LogEventListener logEventListener) {
		this.logEventListener = logEventListener;
	}*/


	public static void main(String[] args)
	{
		SpringApplication.run(DemoApplication.class, args);


	}


	/*@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logEventListener.start();
	}*/
}
