package com.ldapauth.authz.oauth2.common.util;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * @author Dave Syer
 *
 */
public class DefaultJdbcListFactory implements JdbcListFactory {

	private final NamedParameterJdbcOperations jdbcTemplate;

	/**
	 * @param jdbcTemplate the jdbc template to use
	 */
	public DefaultJdbcListFactory(NamedParameterJdbcOperations jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public <T> List<T> getList(String sql, Map<String, Object> parameters, RowMapper<T> rowMapper) {
		return jdbcTemplate.query(sql, parameters, rowMapper);
	}

}
