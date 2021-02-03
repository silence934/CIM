package xyz.nyist.service.impl;

import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * @Author : fucong
 * @Date: 2021-02-02 20:16
 * @Description :
 */
public class JdbcClientDetailsServiceImpl extends JdbcClientDetailsService {
    public JdbcClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }
}
