package com.github.skiot.booking;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
/**
 * Created by srang on 10/24/2017.
 */
@RunWith(Arquillian.class)
public class InventoryApiTest {

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return new Swarm().withProfile("local");
    }

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
            .addPackages(true, Application.class.getPackage())
            .addAsResource("project-local.yml", "project-local.yml")
            .addAsResource("META-INF/test-persistence.xml",  "META-INF/persistence.xml")
            .addAsResource("META-INF/test-load.sql",  "META-INF/test-load.sql");
    }

    @Test
    @RunAsClient
    public void testFindOne() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("/inventory").path("/1");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response, notNullValue());
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.get("itemId").asString(), is("1"));
        assertThat(value.get("link").asString(), is("http://asdf.com/car"));
        assertThat(value.get("location").asString(), is("ORD"));
    }

    @Test
    @RunAsClient
    public void testFindNone() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("/inventory").path("/11");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response, nullValue());
    }

    @Test
    @RunAsClient
    public void testFindAll() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("/inventory");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response, notNullValue());
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.get("itemId").asString(), is("1"));
        assertThat(value.get("link").asString(), is("http://asdf.com/car"));
        assertThat(value.get("location").asString(), is("ORD"));
    }

    @Test
    @RunAsClient
    public void testStatus() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("/status");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(200));
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.get("checks").asString(), notNullValue());
    }
}
