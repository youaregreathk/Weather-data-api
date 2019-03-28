package com.lc.spring.repository;

import com.lc.spring.entity.WeatherData;
import com.lc.spring.entity.WeatherId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<WeatherData> getWeatherByTimeStamp(String timeStamp) {
        Session currentSession = sessionFactory.openSession();
        String hql = "from WeatherData where time_stamp='" + timeStamp + "'";
        Query<WeatherData> query = currentSession.createQuery(hql, WeatherData.class);

        List<WeatherData> weatherDataList = query.getResultList();
        currentSession.close();
        return weatherDataList;
    }

}
