package com.winecellar.winecellar_app.test.utils;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;

import com.winecellar.winecellar_app.data.WineDAO;
import com.winecellar.winecellar_app.model.Wine;

@Stateless
@LocalBean
public class UtilsDAO {

    @PersistenceContext
    private EntityManager em;
    
	public void deleteTable(){
		em.createQuery("DELETE FROM Wine").executeUpdate();
		em.createNativeQuery("ALTER TABLE wine AUTO_INCREMENT=1")
		.executeUpdate();
	}
	
	
}
