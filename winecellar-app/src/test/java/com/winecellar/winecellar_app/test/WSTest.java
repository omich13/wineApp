package com.winecellar.winecellar_app.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.winecellar.winecellar_app.data.WineDAO;
import com.winecellar.winecellar_app.model.Wine;
import com.winecellar.winecellar_app.rest.JaxRsActivator;
import com.winecellar.winecellar_app.rest.WineWS;
import com.winecellar.winecellar_app.test.utils.UtilsDAO;

@RunWith(Arquillian.class)
public class WSTest {
	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap
				.create(JavaArchive.class, "Test.jar")
				.addClasses(WineDAO.class, Wine.class,
						JaxRsActivator.class,WineWS.class,
						UtilsDAO.class)
			//	.addPackage(EventCause.class.getPackage())
			//	.addPackage(EventCauseDAO.class.getPackage())
						//this line will pick up the production db
				.addAsManifestResource("META-INF/persistence.xml",
						"persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

	}

	@EJB
	private WineWS wineWS;
	
	@EJB
	private WineDAO wineDAO;
	
	@EJB
	private UtilsDAO utilsDAO;
	
	@Before
	public void setUp() {
		//this function means that we start with an empty table
		//And add one wine
		//it should be possible to test with an in memory db for efficiency
		utilsDAO.deleteTable();
		Wine wine=new Wine();
		wine.setCountry("Ireland");
		wine.setGrapes("sour");
		wine.setDescription("arquillian");
		wine.setRegion("Athlone");
		wine.setYear("2000");
		wine.setName("arq");
		wine.setPicture("pic.jpg");
		wineDAO.save(wine);
		wine=new Wine();
		wine.setCountry("France");
		wine.setGrapes("merlot");
		wine.setDescription("another arquillian wine");
		wine.setRegion("Dublin");
		wine.setYear("2017");
		wine.setName("ait");
		wine.setPicture("pic1.jpg");
		wineDAO.save(wine);
	}
	
	@Test
	public void testGetAllWinesWS() {
	Response response=wineWS.findAllWines();
	List<Wine> wineList = (List<Wine>) response.getEntity();
	assertEquals(HttpStatus.SC_OK, response.getStatus());
	assertEquals("Data fetch = data persisted", wineList.size(), 2);
	Wine wine=wineList.get(0);
	assertEquals("arq",wine.getName());
	wine=wineList.get(1);
	assertEquals("ait",wine.getName());
	} 
	 
	
	@Test
	public void testWineById() {
		Response response=wineWS.findWineById(1);
		Wine wine = (Wine) response.getEntity();
		assertEquals(wine.getId(), 1);
		assertEquals(wine.getCountry(), "Ireland");
		assertEquals(wine.getGrapes(), "sour");
		assertEquals(wine.getYear(), "2000");
		assertEquals(wine.getName(), "arq");
		assertEquals(wine.getDescription(), "arquillian");
		assertEquals(wine.getRegion(), "Athlone");
		assertEquals(wine.getPicture(), "pic.jpg");
	}
	
	@Test
	public void testAddWine() {
		Wine wine = new Wine();
		wine.setCountry("Spain");
		wine.setDescription("added by arquillian");
		wine.setGrapes("green");
		wine.setName("added");
		wine.setPicture("pic2.jpg");
		wine.setRegion("Mayo");
		wine.setYear("2016");
		Response response=wineWS.saveWine(wine);
		assertEquals(HttpStatus.SC_CREATED, response.getStatus());
		wine = (Wine) response.getEntity();
		assertEquals(wine.getId(), 3);
		assertEquals(wine.getCountry(), "Spain");
		assertEquals(wine.getGrapes(), "green");
		assertEquals(wine.getYear(), "2016");
		assertEquals(wine.getName(), "added");
		assertEquals(wine.getDescription(), "added by arquillian");
		assertEquals(wine.getRegion(), "Mayo");
		assertEquals(wine.getPicture(), "pic2.jpg");
	}
	
	@Test
	public void testRemoveWine() {
		Response response=wineWS.findAllWines();
		List<Wine> wineList = (List<Wine>) response.getEntity();
		assertEquals(wineList.size(), 2);
		wineWS.deleteWine(2);
		response=wineWS.findAllWines();
		wineList = wineList = (List<Wine>) response.getEntity();
		assertEquals(wineList.size(), 1);
		response=wineWS.findWineById(2);
		Wine wine = (Wine) response.getEntity();
		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(null, wine);
		
	}
	
	@Test
	public void testUpdateWine() {
		Response response=wineWS.findWineById(2);
		Wine wine = (Wine) response.getEntity();
		wine.setCountry("Italy");
		wine.setYear("2015");
		wine.setRegion("Westmeath");
		response=wineWS.updateWine(wine);
		assertEquals(HttpStatus.SC_OK, response.getStatus());
		wine = (Wine) response.getEntity();
		assertEquals(wine.getCountry(), "Italy");
		assertEquals(wine.getGrapes(), "merlot");
		assertEquals(wine.getYear(), "2015");
		assertEquals(wine.getName(), "ait");
		assertEquals(wine.getDescription(), "another arquillian wine");
		assertEquals(wine.getPicture(), "pic1.jpg");
	}
	
	@Test
	public void testSearchWinesByName() {
		Response response=wineWS.findByName("arq");
		List<Wine> wineList = (List<Wine>) response.getEntity();
		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(wineList.size(), 1);
		Wine wine=wineList.get(0);
		assertEquals("arq",wine.getName());
		assertEquals("Ireland",wine.getCountry());
		
	} 
}
