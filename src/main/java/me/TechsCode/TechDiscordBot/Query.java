package me.TechsCode.TechDiscordBot;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query<T> {

    protected List<T> objects;

    public Query(List<T> objects) {
        this.objects = objects;
    }

    public Query(Query<T> ... queries){
        this.objects = Arrays.stream(queries).flatMap(q -> q.objects.stream()).collect(Collectors.toList());
    }

    public List<T> all(){
        return objects;
    }

    public boolean hasAny(){
        return objects.size() != 0;
    }

    public boolean hasMultiple(){
        return objects.size() > 1;
    }

    public int amount(){
        return objects.size();
    }

    public T first(){
        return hasAny() ? objects.get(0) : null;
    }

    public Query<T> filter(Predicate<? super T> predicate){
        return new Query<T>(objects.stream().filter(predicate).collect(Collectors.toList()));
    }
}
