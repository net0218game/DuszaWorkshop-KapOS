package com.kapos.hypedemo.model.repo;
import com.kapos.hypedemo.model.Warning;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WarningsRepository extends CrudRepository<Warning, Integer> {
    List<Warning> findAll();

}
