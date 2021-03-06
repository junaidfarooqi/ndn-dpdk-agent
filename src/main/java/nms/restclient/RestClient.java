package nms.restclient;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.core.Future;


public interface RestClient {

	public Future<Configuration> getConfiguration();

	public Future<String> login(String user, String password);

	public void setToken(String token);

	public Future<Void> sendStatus();

	public List<JsonObject> compareConfiguration(Configuration prev, Configuration current); 

	public void setRootCA(String path);

	public void setPort(Integer port);

	public void setHost(String host);



}
