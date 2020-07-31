package xyz.sophialaura.hopper.database.dao;

import com.google.common.collect.ImmutableList;
import xyz.sophialaura.hopper.database.Crud;
import xyz.sophialaura.hopper.model.Hopper;

public interface HopperDao extends Crud<Hopper> {

    ImmutableList<Hopper> findAll();

}
