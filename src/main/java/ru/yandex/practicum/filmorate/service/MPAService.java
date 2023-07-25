package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dbStorage.MPADbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class MPAService {

    private final MPADbStorage mpaDbStorage;

    @Autowired
    public MPAService(MPADbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public MPA getMPAById(int id) {
        return mpaDbStorage.getMPAById(id);
    }

    public Collection<MPA> findAllMPA() {
        return mpaDbStorage.findAllMPA().stream()
                .sorted(Comparator.comparing(MPA::getId))
                .collect(Collectors.toList());
    }
}