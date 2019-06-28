package com.charges.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public interface SqlSessionFactoryGenerate {
    String MYBATIS_CONFIG = "mybatis-config.xml";

    default SqlSessionFactory getSqlSessionFactory() {
        try {
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            try (InputStream stream = SqlSessionFactoryGenerate.class.getClassLoader()
                    .getResourceAsStream(MYBATIS_CONFIG)) {
                return builder.build(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
