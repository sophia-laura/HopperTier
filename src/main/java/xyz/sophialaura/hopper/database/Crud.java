package xyz.sophialaura.hopper.database;

public interface Crud<T> {

    void createTables();

    void createOrUpdate(T t);

    void delete(T t);

}
