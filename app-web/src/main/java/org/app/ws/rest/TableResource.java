package org.app.ws.rest;

import java.io.IOException;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;

import org.app.framework.paging.PagingAssembler;
import org.app.framework.paging.PagingForm;
import org.app.framework.paging.PagingParam;
import org.app.framework.paging.PagingResult;
import org.app.repo.jpa.dao.GenericDao;
import org.app.repo.jpa.model.SecUser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/table", produces = { "application/json", "application/xml" })
public class TableResource {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	GenericDao genericDao;

	@Autowired
	PagingAssembler pagingAssembler;

	@Autowired
	ObjectMapper objectMapper;

	@RequestMapping(value = "/{tableName}", method = RequestMethod.POST)
	@ResponseBody
	public PagingResult<Object> create(@PathVariable String tableName, HttpServletRequest request) throws IOException {
		Class<?> clazz = genericDao.getPersistentClassByName(tableName);
		if (clazz == null) {
			throw new TypeMismatchException(tableName, Entity.class);
		}
		try {
			ServerHttpRequest input = new ServletServerHttpRequest(request);
			Object entity = objectMapper.readValue(input.getBody(), clazz);
			genericDao.persist(entity);
			return PagingResult.fromSingleResult(entity);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/{tableName}", method = RequestMethod.GET)
	@ResponseBody
	public PagingResult<?> read(@PathVariable String tableName, PagingForm form) {
		Class<? extends Object> clazz = genericDao.getPersistentClassByName(tableName);
		if (clazz == null) {
			throw new TypeMismatchException(tableName, Entity.class);
		}
		PagingParam pagingParam = pagingAssembler.toPagingParam(form);
		return genericDao.findPaging(clazz, pagingParam);
	}

	@RequestMapping(value = "/{tableName}/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public PagingResult<Object> update(@PathVariable String tableName, @PathVariable String userId,
			HttpServletRequest request, @RequestBody SecUser user) throws IOException {
		Class<?> clazz = genericDao.getPersistentClassByName(tableName);
		if (clazz == null) {
			throw new TypeMismatchException(tableName, Entity.class);
		}
		try {
			ServerHttpRequest input = new ServletServerHttpRequest(request);
			Object entity = objectMapper.readValue(input.getBody(), clazz);
			Object merge = genericDao.merge(entity);
			return PagingResult.fromSingleResult(merge);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}

	}

	@RequestMapping(value = "/{tableName}/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable String tableName, @PathVariable Integer userId) {
		Class<?> clazz = genericDao.getPersistentClassByName(tableName);
		if (clazz == null) {
			throw new TypeMismatchException(tableName, Entity.class);
		}
		genericDao.remove(clazz, userId);
	}

}
