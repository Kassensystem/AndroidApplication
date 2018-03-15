package dhbw.sa.kassensystemapplication;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class Entity<T> {

    public static <T> HttpEntity<T> getEntity(T object){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.add("loginname", MainActivity.loginName);
        headers.add("passwordhash",MainActivity.loginPasswordHash);

        HttpEntity<T> entity = new HttpEntity<>(object, headers);

        return entity;

    }

}
