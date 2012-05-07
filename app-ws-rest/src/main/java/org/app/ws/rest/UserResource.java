package org.app.ws.rest;

import org.app.framework.paging.PagingAssembler;
import org.app.framework.paging.PagingForm;
import org.app.framework.paging.PagingParam;
import org.app.framework.paging.PagingResult;
import org.app.repo.jpa.dao.UserDao;
import org.app.repo.jpa.model.SecUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest")
public class UserResource {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserDao userDao;

    @Autowired
    PagingAssembler pagingAssembler;

    @RequestMapping(value = "/users", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    @ResponseBody
    public PagingResult<SecUser> create(@RequestBody SecUser user) {
        user.setPasswd("test");
        userDao.persist(user);
        logger.info("stored a user, id {}", user.getSecUserId());
        return PagingResult.fromSingleResult(user);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    @ResponseBody
    public PagingResult<SecUser> read(PagingForm form) {
        PagingParam pagingParam = pagingAssembler.toPagingParam(form);
        return userDao.findPaging(SecUser.class, pagingParam);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT, produces = { "application/json",
            "application/xml" })
    @ResponseBody
    public PagingResult<SecUser> update(@PathVariable String userId, @RequestBody SecUser user) {
        user.setPasswd("test");
        SecUser merge = userDao.merge(user);
        return PagingResult.fromSingleResult(merge);

    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE, produces = { "application/json",
            "application/xml" })
    @ResponseBody
    public void delete(@PathVariable Integer userId) {
        userDao.remove(SecUser.class, userId);
    }

}
