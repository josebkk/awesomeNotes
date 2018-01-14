package smithmicro.apps.awesomeNotes;

import javax.transaction.Transactional;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import smithmicro.apps.awesomeNotes.model.Role;
import smithmicro.apps.awesomeNotes.repository.RoleRepository;

@SpringBootApplication
public class AwesomeNotesApplication implements CommandLineRunner{


	@Autowired
	RoleRepository roleRepository;


	public static void main(String[] args) {
		SpringApplication.run(AwesomeNotesApplication.class, args);


	}


	// Avoid tomcat connection reset issue for large file uploads and increase maxheadersize
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbedded() {

		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

		tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
			if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
				//-1 means unlimited
				((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
				((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxHttpHeaderSize(1048576);
			}
		});
		return tomcat;
	}



	@Override
	@Transactional
	public void run(String... args) throws Exception {

		// for first run, add user roles.
		if(roleRepository.count() == 0){

			Role defaultRole = new Role("USER", null);

			roleRepository.saveAndFlush(defaultRole);
		}
	}

}
