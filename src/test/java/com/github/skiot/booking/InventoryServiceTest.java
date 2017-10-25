package com.github.skiot.booking;

import com.redhat.coolstore.inventory.model.Inventory;
import com.redhat.coolstore.inventory.service.InventoryService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import javax.inject.Inject;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by srang on 10/24/2017.
 */
@RunWith(Arquillian.class)
public class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return new Swarm().withProfile("local");
    }

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
            .addPackages(true, InventoryService.class.getPackage())
            .addPackages(true, Inventory.class.getPackage())
            .addAsResource("project-local.yml", "project-local.yml")
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/test-load.sql", "META-INF/test-load.sql");
    }

    @Test
    public void testServiceFind() throws Exception {
        assertThat(inventoryService, notNullValue());
        Inventory inventory = inventoryService.getInventory("1");
        assertThat(inventory, notNullValue());
        assertThat(inventory.getLocation(), is("ORD"));
        assertThat(inventory.getLink(), is("http://asdf.com/car"));
    }

    @Test
    public void testServiceNoFind() throws Exception {
        assertThat(inventoryService, notNullValue());
        Inventory inventory = inventoryService.getInventory("12345");
        assertThat(inventory, nullValue());
    }

    @Test
    public void testServiceFindAll() throws Exception {
        assertThat(inventoryService, notNullValue());
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        assertThat(inventoryList, notNullValue());
        assertThat(inventoryList.size(), is(5));
    }
}
