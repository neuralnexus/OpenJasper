package com.jaspersoft.jasperserver.jmx;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateStatisticsFactoryBean implements FactoryBean<Statistics> {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Statistics getObject() throws Exception {
        return sessionFactory.getStatistics();
    }

    @Override
    public Class<?> getObjectType() {
        return Statistics.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}