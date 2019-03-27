package com.lc.spring.dao;

import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherDataDAOImpl implements WeatherDataDAO{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(WeatherData weatherData) {
        //Session currentSession = sessionFactory.getCurrentSession();
        Session currentSession = sessionFactory.openSession();
        Transaction transaction = currentSession.beginTransaction();
        currentSession.save(weatherData);
        transaction.commit();
        currentSession.close();
    }

    @Override
    public WeatherData get(WeatherId weatherId) {
        //Session currentSession = sessionFactory.getCurrentSession();
        Session currentSession = sessionFactory.openSession();
        WeatherData weatherData = currentSession.get(WeatherData.class, weatherId);
        currentSession.close();
        return weatherData;
    }
}
