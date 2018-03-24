package dhbw.sa.kassensystemapplication;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

/**
 * Diese Klasse ist eine Generics Klasse. Sie dient dazu, den Header und den Body einer Entity zu
 * bearbeiten. Diese Entity wird anschließend an den Server gesendet.
 * Der Header beinhaltet die Login-Daten, der Body das Objekt das an den Server übergeben werden
 * soll. Das Objekt kann auch null sein, wenn zum Beispiel Daten empfangen werden sollen.
 *
 *  @param <T> Hier kann das Objekt übergeben werden, welches an den Server übermittelt werden soll.
 *            Es ist ein Generic Objekt, sodass jedes Objekt übermittelt werden kann.
 */
public class Entity<T> {
    /**
     * Mit dieser Methode wird die Entity bearbeitet. (Header und Body).
     * Anschließend wird die Entity zurückgegeben.
     *
     * @param object Das Objekt, welches an den Server geschickt werden soll.
     * @param <T> Der "Klassen-Typ" Generics. Passt sich der Klasse des Objekts an.
     * @return Entity. Mit bearbeitetem Body und Header.
     */
    public static <T> HttpEntity<T> getEntity(T object){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.add("loginname", MainActivity.loginName);
        headers.add("passwordhash",MainActivity.loginPasswordHash);

        HttpEntity<T> entity = new HttpEntity<>(object, headers);

        return entity;

    }

}
