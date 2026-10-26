package com.shortey.medieval_game.repository;

import com.shortey.medieval_game.entity.Territory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerritoryRepository extends JpaRepository<Territory, Long> {

    List<Territory> findAllByOrderByIdAsc();
}