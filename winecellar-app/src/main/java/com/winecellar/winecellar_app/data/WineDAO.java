package com.winecellar.winecellar_app.data;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.winecellar.winecellar_app.model.Wine;

@Stateless
@LocalBean
public class WineDAO {

    @PersistenceContext
    private EntityManager em;
    
	public List<Wine> getAllWines() {
    	Query query=em.createQuery("SELECT w FROM Wine w");
        return query.getResultList();
    }
	
	public List<Wine> getWinesByName(String name) {
    	Query query=em.createQuery("SELECT w FROM Wine AS w "+
    								"WHERE w.name LIKE ?1");
    	query.setParameter(1, "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }
	
	public Wine getWine(int id ) {
        return em.find(Wine.class, id);
    }
	
	public void save(Wine wine){
		em.persist(wine);
	}
	
	public void update(Wine wine) {
		em.merge(wine);
	}
	
	public void delete(int id) {
		em.remove(getWine(id));
		em.persit(wine);
	}
	public void deleteTable(){
		em.createQuery("DELETE FROM Wine").executeUpdate();
	}
      
}


