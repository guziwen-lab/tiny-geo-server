package com.supermap.common.valid;

import jakarta.validation.Valid;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

public class ValidationList<E> implements List<E> {

    @Delegate
    @Valid
    public final ArrayList<E> list = new ArrayList<>();

    @Override
    public String toString() {
        return list.toString();
    }

}