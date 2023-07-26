package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dbStorage.MPADbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MPAService {

    private final MPADbStorage mpaDbStorage;

    public MPA getMPAById(int id) {
        return mpaDbStorage.getMPAById(id);
    }

    public Collection<MPA> findAllMPA() {
        return mpaDbStorage.findAllMPA().stream()
                .sorted(Comparator.comparing(MPA::getId))
                .collect(Collectors.toList());
    }
}