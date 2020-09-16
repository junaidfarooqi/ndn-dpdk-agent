package nms.restclient;

import com.google.gson.Gson;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class RestClientImpl implements RestClient {

	private EntryPoint entryPoint;
	private WebClient webClient;

	private static String CANDIDATE_CONFIG_ENDPOINT = "/configuration/candidate-config";
	private static String RUNNING_CONFIG_ENDPOINT = "/configuration/running-config";
	private static String NOTIFICATION_STATUS_ENDPOINT = "/notification/status";
	private static String NOTIFICATION_EVENT_ENDPOINT = "/notification/event";
	private static String NOTIFICATION_FAULT_ENDPOINT = "/notification/fault";
	private static String LOGIN_ENDPOINT = "/login/agent";
	
	private String token;

	public RestClientImpl(Vertx vertx, final EntryPoint entryPoint) {
		this.webClient = WebClient.create(vertx);
		this.entryPoint = entryPoint;
	}

	public RestClientImpl(EntryPoint entryPoint) {
		this.entryPoint = entryPoint;
	}

	@Override
	public Future<Configuration> getCandidateConfiguration(String token) {
		Promise<Configuration> promise = Promise.promise();
		webClient
		.get(entryPoint.getPort(), entryPoint.getHost(), CANDIDATE_CONFIG_ENDPOINT)
		.bearerTokenAuthentication(token)
		.send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				Configuration config = new Gson().fromJson(response.bodyAsString(), Configuration.class);
				promise.complete(config);
			} else {
				promise.fail("could not retrieve candidate configuration");
			}
		});
		return promise.future();
	}

	@Override
	public Future<Configuration> getRunningConfiguration(String token) {
		Promise<Configuration> promise = Promise.promise();
		webClient
		.get(entryPoint.getPort(), entryPoint.getHost(), RUNNING_CONFIG_ENDPOINT)
		.bearerTokenAuthentication(token)
		.send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				Configuration config = new Gson().fromJson(response.bodyAsString(), Configuration.class);
				promise.complete(config);
			} else {
				promise.fail("could not retrieve running configuration");
			}
		});
		return promise.future();
	}

	@Override
	public Future<Void> sendNotification(Notification notification) {
		Promise<Void> promise = Promise.promise();
		String endpoint = "";
		switch (notification.getType()) {
		case STATUS:
			endpoint = NOTIFICATION_STATUS_ENDPOINT;
			break;
		case EVENT:
			endpoint = NOTIFICATION_EVENT_ENDPOINT;
			break;
		case FAULT:
			endpoint = NOTIFICATION_FAULT_ENDPOINT;
			break;
		}

		// TODO: add logic to send notification here
		// this should be a post request
		// look at: https://vertx.io/docs/vertx-web-client/java/#_writing_request_bodies
		return promise.future();
	}

	@Override
	public Future<String> basicAuthentication(String user, String password) {
		Promise<String> promise = Promise.promise();
		webClient
		.post(entryPoint.getPort(), entryPoint.getHost(), LOGIN_ENDPOINT)
		.as(BodyCodec.jsonObject()) // response will be decoded as JsonObject
		.sendJsonObject(new JsonObject()
				.put("username", user)
				.put("password", password), ar -> {
					if (ar.succeeded()) {
						HttpResponse<JsonObject> response = ar.result();
						String token = response.body().getString("token");
						this.setToken(token);
						promise.complete(token);
					} else {
						promise.fail("unable to login");
					}
				});
		return promise.future();
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
