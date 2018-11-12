This is a proof of concept of configuration microservice which uses a Kafka topic as its data store.

It is dynamic and can be updated at runtime to alter system behavior live.  We provide a RESTful POST endpoint for that purpose
but you can also directly post to the Kafka config topic from any other source.

If a configuration value already exists, it will be overwritten.  If the new value is an empty String, the configuration key 
will be deleted.

Future plans include:  
* Export/Import of current configuration 
* Starting a new instance and loading configuration from another running instance
* Controls to clear configuration, reload it from the beginning of the era, or reload it from a particular time

In order to run and test this you will need:
* A copy of gradle that works on your system
* A recent JDK (I built this with Java 8 and made no efforts to think about backward compatibility)
* A running instance of Kafka.  Just follow their quickstart far enough to get the server up and running.
* Either curl or Postman, if you want to exercise the endpoints.  IntelliJ also has a client, or you can just roll your own if you're into that kind of thing.
