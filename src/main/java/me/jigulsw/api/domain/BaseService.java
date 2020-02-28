package me.jigulsw.api.domain;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import me.jigulsw.api.common.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public abstract class BaseService<T, ID extends Serializable> {
    @PersistenceContext
    protected EntityManager em;

    public boolean isEmpty(String value) {
        return StringUtils.isEmpty(value);
    }

    public boolean isNotEmpty(String value) { return !isEmpty(value); }

    public boolean isEmpty(Collection<?> list) {
        return isNull(list) || list.size() == 0;
    }

    public boolean isNotEmpty(Collection<?> list) { return !isEmpty(list); }

    public boolean isNull(Object o) { return Objects.isNull(o); }

    public boolean isNotNull(Object o) { return !isNull(o); }

    public boolean equals(Object o1, Object o2) {
        if (isNull(o1) || isNull(o2)) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    public boolean notEquals(Object o1, Object o2) {
        return !this.equals(o1, o2);
    }

    public String likeLeft(String str) {
        return "%" + str;
    }

    public String likeRight(String str) {
        return str + "%";
    }

    public String likeBoth(String str) {
        return "%" + str + "%";
    }

    public static <T> List<T> toList(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    protected JPAQuery<T> select() {
        return new JPAQuery(this.em);
    }

    protected JPAUpdateClause update(EntityPath<?> entityPath) {
        return new JPAUpdateClause(this.em, entityPath);
    }

    protected JPADeleteClause delete(EntityPath<?> entityPath) {
        return new JPADeleteClause(this.em, entityPath);
    }

    public long getUserId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return -1;
        }
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object userId = servletRequest.getAttribute("userId");

        return isNotNull(userId) ? (long)userId : -1;
    }

    public long now() {
        return DateUtil.getNow();
    }

    public long nowMilli() { return DateUtil.getNowMilli(); }

    public boolean reCheck(String rePattern, String str) {
        Pattern pattern = Pattern.compile(rePattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public String nvl(String str, String ifNull) {
        return StringUtils.defaultString(str, ifNull);
    }


}
